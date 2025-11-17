package com.example.happykidneys.ui.goals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.happykidneys.data.database.AppDatabase
import com.example.happykidneys.data.repository.UserRepository
import com.example.happykidneys.databinding.FragmentQuizResultBinding
import com.example.happykidneys.utils.PreferenceManager

class QuizResultFragment : Fragment() {

    private var _binding: FragmentQuizResultBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GoalCalculatorViewModel by activityViewModels()

    // We need these to save the final goal
    private lateinit var userRepository: UserRepository
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize repositories (just like in GoalsFragment)
        val database = AppDatabase.getDatabase(requireContext())
        userRepository = UserRepository(database.userDao())
        preferenceManager = PreferenceManager(requireContext())

        // Calculate and display the goal
        val goal = viewModel.calculateGoal()
        binding.tvResult.text = String.format("%.1f L", goal)

        binding.btnSave.setOnClickListener {
            val userId = preferenceManager.getUserId()
            if (userId != -1L) {
                viewModel.saveGoal(userId, userRepository, preferenceManager)
                Toast.makeText(requireContext(), "New goal saved!", Toast.LENGTH_SHORT).show()
                activity?.finish()
            }
        }

        binding.btnBack.setOnClickListener {
            (activity as? GoalCalculatorActivity)?.previousPage()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}