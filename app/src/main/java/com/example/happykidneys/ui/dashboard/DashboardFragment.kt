package com.example.happykidneys.ui.dashboard

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.happykidneys.R
import com.example.happykidneys.data.database.AppDatabase
import com.example.happykidneys.data.database.entities.WaterIntake
import com.example.happykidneys.data.repository.GoalRepository
import com.example.happykidneys.data.repository.WaterIntakeRepository
import com.example.happykidneys.databinding.FragmentDashboardBinding
import com.example.happykidneys.utils.PreferenceManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
// Import Wearable API
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var waterIntakeRepository: WaterIntakeRepository
    private lateinit var goalRepository: GoalRepository
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var intakeAdapter: WaterIntakeAdapter
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        waterIntakeRepository = WaterIntakeRepository(database.waterIntakeDao())
        goalRepository = GoalRepository(database.goalDao())
        preferenceManager = PreferenceManager(requireContext())

        setupRecyclerView()
        setupChart()
        setupListeners()

        parentFragmentManager.setFragmentResultListener("addIntakeResult", viewLifecycleOwner) { requestKey, bundle ->
            val waterAmount = bundle.getFloat("waterAmount")
            if (waterAmount > 0) {
                addWater(waterAmount)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()

        // Re-apply rotation setting (if you kept that feature)
        if (!preferenceManager.isWaterRotationEnabled()) {
            binding.fillingGlassView.setPhoneRotation(0f)
        }
    }

    private fun setupRecyclerView() {
        intakeAdapter = WaterIntakeAdapter { intake ->
            deleteIntake(intake)
        }
        binding.rvRecentIntakes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = intakeAdapter
        }
    }

    private fun setupChart() {
        binding.barChart.apply {
            description.isEnabled = false
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)
            setFitBars(true)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
            }

            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = Color.parseColor("#E0E0E0")
                axisMinimum = 0f
            }

            axisRight.isEnabled = false
            legend.isEnabled = false
            animateY(1000)
        }
    }

    private fun setupListeners() {
        binding.fabAdd.setOnClickListener {
            AddIntakeDialogFragment().show(parentFragmentManager, AddIntakeDialogFragment.TAG)
        }
    }

    private fun loadData() {
        val userId = preferenceManager.getUserId()
        val today = dateFormat.format(Date())
        val dailyGoal = preferenceManager.getDailyGoal()

        lifecycleScope.launch {
            waterIntakeRepository.getTotalForDate(userId, today).collect { total ->
                val currentTotal = total ?: 0f

                if (_binding != null) {
                    binding.tvTodayConsumption.text = String.format("%.1f L", currentTotal)

                    val percentage = (currentTotal / dailyGoal * 100).toInt()
                    binding.tvGoalProgress.text = "$percentage% of ${dailyGoal}L goal"
                    binding.fillingGlassView.setPercentage(percentage)

                    // --- SEND TO WATCH ---
                    sendWaterToWatch(currentTotal)
                }
            }
        }

        lifecycleScope.launch {
            waterIntakeRepository.getIntakesForDate(userId, today).collect { intakes ->
                intakeAdapter.submitList(intakes)
            }
        }

        loadWeeklyData(userId)
        loadStreakData(userId)
    }

    // --- HELPER TO SEND DATA TO WATCH ---
    private fun sendWaterToWatch(totalLiters: Float) {
        val dataClient = Wearable.getDataClient(requireContext())

        val putDataReq = PutDataMapRequest.create("/water_intake").apply {
            dataMap.putFloat("total_liters", totalLiters)
            // Add timestamp to insure the event triggers every time
            dataMap.putLong("timestamp", System.currentTimeMillis())
        }

        val putDataRequest = putDataReq.asPutDataRequest()
        putDataRequest.setUrgent() // Send immediately

        dataClient.putDataItem(putDataRequest)
    }

    private fun loadWeeklyData(userId: Long) {
        val calendar = Calendar.getInstance()
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val lastSevenDays = mutableListOf<String>()
        val dayLabels = mutableListOf<String>()
        calendar.add(Calendar.DAY_OF_YEAR, -6)
        val startDate = dateFormat.format(calendar.time)

        for (i in 0..6) {
            lastSevenDays.add(dateFormat.format(calendar.time))
            dayLabels.add(dayFormat.format(calendar.time))
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        lifecycleScope.launch {
            waterIntakeRepository.getWeeklyIntakes(userId, startDate).collect { weeklyData ->
                if (_binding != null) {
                    val totalIntake = weeklyData.sumOf { it.total.toDouble() }
                    val average = if (totalIntake == 0.0) 0.0 else totalIntake / 7.0
                    binding.tvWeeklyAverage.text = String.format("%.1f L", average)
                    updateChart(weeklyData, lastSevenDays, dayLabels)
                }
            }
        }
    }

    private fun loadStreakData(userId: Long) {
        lifecycleScope.launch {
            val goals = goalRepository.getAllGoals(userId).first()
            val goalMap = goals.associateBy { it.date }
            var streakCount = 0
            val calendar = Calendar.getInstance()
            val todayString = dateFormat.format(calendar.time)

            if (_binding != null) {
                if (goalMap[todayString]?.achieved == true) {
                    streakCount++
                }

                for (i in 0..365) {
                    calendar.add(Calendar.DAY_OF_YEAR, -1)
                    val dateString = dateFormat.format(calendar.time)
                    val goal = goalMap[dateString]

                    if (goal != null && goal.achieved) {
                        streakCount++
                    } else {
                        break
                    }
                }
                binding.tvStreak.text = streakCount.toString()
            }
        }
    }

    private fun updateChart(
        weeklyData: List<com.example.happykidneys.data.database.dao.DailyTotal>,
        lastSevenDays: List<String>,
        dayLabels: List<String>
    ) {
        val dataMap = weeklyData.associateBy({ it.date }, { it.total })
        val entries = lastSevenDays.mapIndexed { index, dateString ->
            val total = dataMap[dateString] ?: 0f
            BarEntry(index.toFloat(), total)
        }

        val dataSet = BarDataSet(entries, "Water Intake").apply {
            color = ContextCompat.getColor(requireContext(), R.color.primary_blue)
            valueTextSize = 12f
            valueTextColor = ContextCompat.getColor(requireContext(), R.color.text_primary)
        }

        binding.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(dayLabels)
        binding.barChart.data = BarData(dataSet)
        binding.barChart.invalidate()
    }

    private fun addWater(amount: Float) {
        val userId = preferenceManager.getUserId()
        val today = dateFormat.format(Date())

        lifecycleScope.launch {
            val intake = WaterIntake(
                userId = userId,
                amount = amount,
                date = today
            )
            waterIntakeRepository.insert(intake)
            Toast.makeText(
                requireContext(),
                getString(R.string.water_added),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun deleteIntake(intake: WaterIntake) {
        lifecycleScope.launch {
            waterIntakeRepository.delete(intake)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}