package com.example.happykidneys.ui.goals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.happykidneys.R
import com.example.happykidneys.data.database.AppDatabase
import com.example.happykidneys.data.database.entities.Goal
import com.example.happykidneys.data.repository.GoalRepository
import com.example.happykidneys.data.repository.UserRepository
import com.example.happykidneys.data.repository.WaterIntakeRepository
import com.example.happykidneys.databinding.FragmentGoalsBinding
import com.example.happykidneys.utils.PreferenceManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class GoalsFragment : Fragment() {

    private var _binding: FragmentGoalsBinding? = null
    private val binding get() = _binding!!
    private lateinit var goalRepository: GoalRepository
    private lateinit var waterIntakeRepository: WaterIntakeRepository
    private lateinit var userRepository: UserRepository
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var goalHistoryAdapter: GoalHistoryAdapter
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoalsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        goalRepository = GoalRepository(database.goalDao())
        waterIntakeRepository = WaterIntakeRepository(database.waterIntakeDao())
        userRepository = UserRepository(database.userDao())
        preferenceManager = PreferenceManager(requireContext())

        setupRecyclerView()
        setupListeners()
        loadData()
    }

    private fun setupRecyclerView() {
        goalHistoryAdapter = GoalHistoryAdapter()
        binding.rvGoalHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = goalHistoryAdapter
        }
    }

    private fun setupListeners() {
        binding.btnSetGoal.setOnClickListener {
            val goalText = binding.etGoal.text.toString()
            if (goalText.isNotEmpty()) {
                val goal = goalText.toFloatOrNull()
                if (goal != null && goal > 0) {
                    updateDailyGoal(goal)
                } else {
                    Toast.makeText(requireContext(), "Invalid goal amount", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadData() {
        val userId = preferenceManager.getUserId()
        val today = dateFormat.format(Date())
        val dailyGoal = preferenceManager.getDailyGoal()

        // Display current goal
        binding.etGoal.setText(dailyGoal.toString())

        // Load today's progress
        viewLifecycleOwner.lifecycleScope.launch {
            waterIntakeRepository.getTotalForDate(userId, today).collect { total ->
                val currentTotal = total ?: 0f
                updateProgress(currentTotal, dailyGoal)

                // Update today's goal in database
                updateTodayGoalInBackground(userId, today, currentTotal, dailyGoal)
            }
        }

        // Load goal history only once
        loadGoalHistory(userId)
    }

    private fun loadGoalHistory(userId: Long) {
        viewLifecycleOwner.lifecycleScope.launch {
            goalRepository.getAllGoals(userId).collect { goals ->
                val sortedGoals = goals.sortedByDescending { it.date }
                if (sortedGoals.isEmpty()) {
                    binding.tvEmptyHistory.visibility = View.VISIBLE
                    binding.rvGoalHistory.visibility = View.GONE
                } else {
                    binding.tvEmptyHistory.visibility = View.GONE
                    binding.rvGoalHistory.visibility = View.VISIBLE
                    goalHistoryAdapter.submitList(sortedGoals)
                }
            }
        }
    }

    private fun updateTodayGoalInBackground(userId: Long, today: String, currentAmount: Float, targetAmount: Float) {
        viewLifecycleOwner.lifecycleScope.launch {
            val achieved = currentAmount >= targetAmount

            // Check if goal exists for today
            var goalExists = false
            goalRepository.getGoalForDate(userId, today).collect { existingGoal ->
                if (existingGoal == null && !goalExists) {
                    // Create new goal only if it doesn't exist
                    val goal = Goal(
                        userId = userId,
                        targetAmount = targetAmount,
                        date = today,
                        achieved = achieved,
                        actualAmount = currentAmount
                    )
                    goalRepository.insert(goal)
                    goalExists = true
                } else if (existingGoal != null) {
                    // Update existing goal
                    goalRepository.updateGoalProgress(userId, today, currentAmount, achieved)
                    goalExists = true
                }
            }
        }
    }

    private fun updateProgress(current: Float, goal: Float) {
        val percentage = ((current / goal) * 100).toInt().coerceAtMost(100)

        binding.tvProgressPercentage.text = "$percentage%"
        binding.tvProgressAmount.text = String.format("%.1f / %.1f L", current, goal)
        binding.circularProgress.progress = percentage

        // Update achievement message
        val message = when {
            percentage >= 100 -> getString(R.string.goal_reached)
            percentage >= 75 -> "Almost there! Keep going!"
            percentage >= 50 -> "You're halfway there!"
            percentage >= 25 -> "Good start! Keep drinking!"
            else -> "Let's get started!"
        }
        binding.tvAchievementMessage.text = message
    }

    private fun updateDailyGoal(newGoal: Float) {
        val userId = preferenceManager.getUserId()

        viewLifecycleOwner.lifecycleScope.launch {
            userRepository.updateDailyGoal(userId, newGoal)
            preferenceManager.setDailyGoal(newGoal)

            Toast.makeText(
                requireContext(),
                "Daily goal updated to ${newGoal}L",
                Toast.LENGTH_SHORT
            ).show()

            // Reload data with new goal
            loadData()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}