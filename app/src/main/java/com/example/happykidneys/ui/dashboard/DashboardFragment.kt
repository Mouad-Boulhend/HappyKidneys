package com.example.happykidneys.ui.dashboard

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.example.happykidneys.R
import com.example.happykidneys.data.database.AppDatabase
import com.example.happykidneys.data.database.entities.WaterIntake
import com.example.happykidneys.data.repository.WaterIntakeRepository
import com.example.happykidneys.databinding.FragmentDashboardBinding
import com.example.happykidneys.ui.dashboard.WaterIntakeAdapter
import com.example.happykidneys.utils.PreferenceManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import com.example.happykidneys.data.repository.GoalRepository
import kotlinx.coroutines.flow.first
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class DashboardFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var waterIntakeRepository: WaterIntakeRepository
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var intakeAdapter: WaterIntakeAdapter
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private lateinit var goalRepository: GoalRepository
    private lateinit var sensorManager: SensorManager
    private var rotationSensor: Sensor? = null
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

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


        // This listens for the final result from AddIntakeDialogFragment
        parentFragmentManager.setFragmentResultListener("addIntakeResult", viewLifecycleOwner) { requestKey, bundle ->
            val waterAmount = bundle.getFloat("waterAmount")
            if (waterAmount > 0) {
                addWater(waterAmount) // Call your existing addWater function
            }
        }

        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // Use Rotation Vector for a more stable reading than accelerometer/gyroscope alone
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    }

    override fun onResume() {
        super.onResume()
        loadData()

        // Start listening for sensor updates when the fragment is visible
        rotationSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()

        // Stop listening when the fragment is not visible to save battery
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
            SensorManager.getOrientation(rotationMatrix, orientationAngles)

            val roll = orientationAngles[2] // Side-to-side tilt

            // Set rotation with reduced sensitivity
            binding.fillingGlassView.setPhoneRotation(roll * 0.8f)
        }
    }

    // --- ADD THIS NEW FUNCTION (for SensorEventListener) ---
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for this, but required by the interface
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
            // Show the new tabbed dialog
            AddIntakeDialogFragment().show(parentFragmentManager, AddIntakeDialogFragment.TAG)
        }
    }

    private fun loadData() {
        val userId = preferenceManager.getUserId()
        val today = dateFormat.format(Date())
        val dailyGoal = preferenceManager.getDailyGoal()

        // Load today's total
        lifecycleScope.launch {
            waterIntakeRepository.getTotalForDate(userId, today).collect { total ->
                val currentTotal = total ?: 0f
                binding.tvTodayConsumption.text = String.format("%.1f L", currentTotal)

                val percentage = (currentTotal / dailyGoal * 100).toInt()

                binding.tvGoalProgress.text = "$percentage% of ${dailyGoal}L goal"

                // Post the UI update to the view's message queue.
                // This ensures the container has a measured height.
                binding.fillingGlassView.setPercentage(percentage)
            }
        }

        // Load recent intakes
        lifecycleScope.launch {
            waterIntakeRepository.getIntakesForDate(userId, today).collect { intakes ->
                intakeAdapter.submitList(intakes)
            }
        }

        // Load weekly data
        loadWeeklyData(userId)

        // Load streak data
        loadStreakData(userId)
    }

    private fun loadWeeklyData(userId: Long) {
        val calendar = Calendar.getInstance()
        // SimpleDateFormat to get the day abbreviation (e.g., "Mon")
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())

        val lastSevenDays = mutableListOf<String>() // "yyyy-MM-dd"
        val dayLabels = mutableListOf<String>()    // "Mon", "Tue", etc.

        // Start 6 days ago to get a total of 7 days (including today)
        calendar.add(Calendar.DAY_OF_YEAR, -6)

        for (i in 0..6) {
            // Add the date string (e.g., "2025-11-11")
            lastSevenDays.add(dateFormat.format(calendar.time))
            // Add the day label (e.g., "Tue")
            dayLabels.add(dayFormat.format(calendar.time))
            // Move to the next day
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        // The startDate is the first day in our list (6 days ago)
        val startDate = lastSevenDays.first()

        lifecycleScope.launch {
            waterIntakeRepository.getWeeklyIntakes(userId, startDate).collect { weeklyData ->
                // Calculate average
                val totalIntake = weeklyData.sumOf { it.total.toDouble() }
                // We divide by 7, as it's a 7-day period
                val average = if (totalIntake == 0.0) 0.0 else totalIntake / 7.0
                binding.tvWeeklyAverage.text = String.format("%.1f L", average)

                // Pass the DB data AND the lists we just generated
                updateChart(weeklyData, lastSevenDays, dayLabels)
            }
        }
    }

    private fun loadStreakData(userId: Long) {
        lifecycleScope.launch {
            // Get the full history, just once
            val goals = goalRepository.getAllGoals(userId).first()

            // Create a lookup map (Date String -> Goal)
            val goalMap = goals.associateBy { it.date }

            var streakCount = 0
            val calendar = Calendar.getInstance()

            // Check if today's goal (if it exists) is failed.
            // If so, the streak is 0.
            val todayString = dateFormat.format(calendar.time)
            if (goalMap[todayString]?.achieved == false) {
                binding.tvStreak.text = "0"
                return@launch
            }

            // If today is not failed (it's either null or achieved),
            // we start counting from today and go backwards.
            for (i in 0..365) { // Limit to a 1-year streak check
                val dateString = dateFormat.format(calendar.time)
                val goal = goalMap[dateString]

                if (goal != null && goal.achieved) {
                    // Day was achieved, add to streak
                    streakCount++
                } else if (goal == null && i == 0) {
                    // It's today and no goal is logged yet.
                    // This doesn't break the streak, so just continue to yesterday.
                } else {
                    // Day was not achieved, or no goal was logged for a past day.
                    // The streak is broken.
                    break
                }

                // Move calendar to the previous day
                calendar.add(Calendar.DAY_OF_YEAR, -1)
            }

            binding.tvStreak.text = streakCount.toString()
        }
    }

    private fun updateChart(
        weeklyData: List<com.example.happykidneys.data.database.dao.DailyTotal>,
        lastSevenDays: List<String>, // <-- New parameter
        dayLabels: List<String>      // <-- New parameter
    ) {
        // Create a map of (date -> total) for fast lookup.
        // This contains only the days the user *actually* drank water.
        val dataMap = weeklyData.associateBy({ it.date }, { it.total })

        // Create BarEntry list by iterating over the *lastSevenDays* list.
        // This ensures we have 7 entries, one for each day.
        val entries = lastSevenDays.mapIndexed { index, dateString ->
            // Get the total from our map, or 0f if the user didn't drink
            val total = dataMap[dateString] ?: 0f
            BarEntry(index.toFloat(), total)
        }

        val dataSet = BarDataSet(entries, "Water Intake").apply {
            color = ContextCompat.getColor(requireContext(), R.color.primary_blue)
            valueTextSize = 12f
            valueTextColor = ContextCompat.getColor(requireContext(), R.color.text_primary)
        }

        // Set the X-axis labels dynamically using the "dayLabels" list
        binding.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(dayLabels)

        binding.barChart.data = BarData(dataSet)
        binding.barChart.invalidate() // Refresh the chart
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