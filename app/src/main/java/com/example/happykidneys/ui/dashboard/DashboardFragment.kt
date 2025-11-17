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

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var waterIntakeRepository: WaterIntakeRepository
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
        preferenceManager = PreferenceManager(requireContext())

        setupRecyclerView()
        setupChart()
        setupListeners()
        loadData()
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
            showAddWaterDialog()
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
                binding.progressBar.progress = percentage.coerceAtMost(100)
                binding.tvGoalProgress.text = "$percentage% of ${dailyGoal}L goal"
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
                // Pass the DB data AND the lists we just generated
                updateChart(weeklyData, lastSevenDays, dayLabels)
            }
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

    private fun showAddWaterDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_add_water)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val etAmount = dialog.findViewById<TextInputEditText>(R.id.etAmount)
        val btnAdd = dialog.findViewById<MaterialButton>(R.id.btnAdd)
        val btnCancel = dialog.findViewById<MaterialButton>(R.id.btnCancel)
        val btn250 = dialog.findViewById<MaterialCardView>(R.id.btn250ml)
        val btn500 = dialog.findViewById<MaterialCardView>(R.id.btn500ml)
        val btn1000 = dialog.findViewById<MaterialCardView>(R.id.btn1000ml)
        val btnOrange = dialog.findViewById<MaterialCardView>(R.id.btnOrange)
        val btnWatermelon = dialog.findViewById<MaterialCardView>(R.id.btnWatermelon)
        val btnCoffee = dialog.findViewById<MaterialCardView>(R.id.btnCoffee)

        btn250.setOnClickListener {
            addWater(0.25f)
            dialog.dismiss()
        }

        btn500.setOnClickListener {
            addWater(0.5f)
            dialog.dismiss()
        }

        btn1000.setOnClickListener {
            addWater(1.0f)
            dialog.dismiss()
        }

        btnOrange.setOnClickListener {
            showCustomAmountDialog("Orange", "grams", 0.87f) // 87% water content
            dialog.dismiss()
        }

        btnWatermelon.setOnClickListener {
            showCustomAmountDialog("Watermelon", "grams", 0.92f) // 92% water content
            dialog.dismiss()
        }

        btnCoffee.setOnClickListener {
            showCustomAmountDialog("Coffee", "ml", 0.99f) // 99% water content
            dialog.dismiss()
        }

        btnAdd.setOnClickListener {
            val amountText = etAmount.text.toString()
            if (amountText.isNotEmpty()) {
                val amount = amountText.toFloatOrNull()
                if (amount != null && amount > 0) {
                    addWater(amount)
                    dialog.dismiss()
                } else {
                    Toast.makeText(requireContext(), "Invalid amount", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showCustomAmountDialog(itemName: String, unit: String, waterPercentage: Float) {
        val customDialog = Dialog(requireContext())
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_custom_amount, null)
        customDialog.setContentView(dialogView)
        customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val tvTitle = dialogView.findViewById<TextView>(R.id.tvDialogTitle)
        val tilAmount = dialogView.findViewById<TextInputLayout>(R.id.tilCustomAmount)
        val etAmount = dialogView.findViewById<TextInputEditText>(R.id.etCustomAmount)
        val btnConfirm = dialogView.findViewById<MaterialButton>(R.id.btnConfirm)
        val btnCancel = dialogView.findViewById<MaterialButton>(R.id.btnCancelCustom)

        tvTitle.text = "How much $itemName?"
        tilAmount.suffixText = " $unit"
        tilAmount.hint = "Amount in $unit"

        btnConfirm.setOnClickListener {
            val amountText = etAmount.text.toString()
            if (amountText.isNotEmpty()) {
                val amount = amountText.toFloatOrNull()
                if (amount != null && amount > 0) {
                    // Convert to liters
                    val waterAmount = if (unit == "ml") {
                        amount / 1000f * waterPercentage
                    } else {
                        // grams
                        amount / 1000f * waterPercentage
                    }
                    addWater(waterAmount)
                    customDialog.dismiss()
                } else {
                    Toast.makeText(requireContext(), "Invalid amount", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please enter an amount", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            customDialog.dismiss()
        }

        customDialog.show()
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