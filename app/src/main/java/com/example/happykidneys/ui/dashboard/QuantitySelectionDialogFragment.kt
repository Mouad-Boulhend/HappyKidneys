package com.example.happykidneys.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.happykidneys.data.database.AppDatabase
import com.example.happykidneys.data.database.entities.FoodBeverage
import com.example.happykidneys.data.repository.FoodBeverageRepository
import com.example.happykidneys.databinding.DialogQuantitySelectionBinding
import kotlinx.coroutines.launch

class QuantitySelectionDialogFragment : DialogFragment() {

    private var _binding: DialogQuantitySelectionBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: FoodBeverageRepository
    private var foodItem: FoodBeverage? = null
    private var calculatedWaterInLiters: Float = 0f

    // This data class will hold our dropdown options
    private data class MeasurementOption(
        val label: String, // e.g., "medium (150g)"
        val type: String,  // "weight" or "volume"
        val value: Float   // 150 (for 150g) or 240 (for 240ml)
    )

    private val measurementOptions = mutableListOf<MeasurementOption>()
    private var selectedOption: MeasurementOption? = null

    companion object {
        const val TAG = "QuantitySelectionDialog"
        private const val ARG_ITEM_ID = "item_id"
        const val REQUEST_KEY = "quantityResult"

        fun newInstance(itemId: Long): QuantitySelectionDialogFragment {
            val args = Bundle().apply {
                putLong(ARG_ITEM_ID, itemId)
            }
            return QuantitySelectionDialogFragment().apply {
                arguments = args
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogQuantitySelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        repository = FoodBeverageRepository(database.foodBeverageDao())

        val itemId = arguments?.getLong(ARG_ITEM_ID) ?: 0L

        // Load the item from the DB
        viewLifecycleOwner.lifecycleScope.launch {
            foodItem = repository.getItemById(itemId) // You'll need to add getItemById to your DAO/Repo
            if (foodItem == null) {
                dismiss()
                return@launch
            }
            setupUI()
        }
    }

    private fun setupUI() {
        foodItem?.let { item ->
            binding.tvItemName.text = "Add ${item.name}"

            // --- THIS IS THE NEW DYNAMIC LOGIC ---
            measurementOptions.clear() // Clear the old list

            // Check for and add weight options
            item.weightSmall_g?.let { measurementOptions.add(MeasurementOption("small (${it}g)", "weight", it)) }
            item.weightMedium_g?.let { measurementOptions.add(MeasurementOption("medium (${it}g)", "weight", it)) }
            item.weightLarge_g?.let { measurementOptions.add(MeasurementOption("large (${it}g)", "weight", it)) }
            item.weightUnit_g?.let { measurementOptions.add(MeasurementOption("grams", "weight", it)) }

            // Check for and add volume options
            item.volumeCup_ml?.let { measurementOptions.add(MeasurementOption("cup (${it}ml)", "volume", it)) }
            item.volumeUnit_ml?.let { measurementOptions.add(MeasurementOption("ml", "volume", it)) }
            // --- END NEW LOGIC ---

            if (measurementOptions.isEmpty()) {
                // Fallback in case data is missing
                measurementOptions.add(MeasurementOption("grams", "weight", 1f))
            }

            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                measurementOptions.map { it.label }
            )
            binding.acUnit.setAdapter(adapter)

            // Select the first item by default
            binding.acUnit.setText(measurementOptions[0].label, false)
            selectedOption = measurementOptions[0]

            // 2. Set up listeners (This part is unchanged)
            binding.acUnit.setOnItemClickListener { parent, _, position, _ ->
                selectedOption = measurementOptions[position]
                calculateWater()
            }

            binding.etAmount.doOnTextChanged { text, _, _, _ ->
                calculateWater()
            }

            binding.btnCancel.setOnClickListener { dismiss() }
            binding.btnConfirm.setOnClickListener {
                val result = Bundle().apply {
                    putFloat("waterAmount", calculatedWaterInLiters)
                }
                parentFragmentManager.setFragmentResult(REQUEST_KEY, result)
                dismiss()
            }

            // 3. Do initial calculation (This part is unchanged)
            calculateWater()
        }
    }

    private fun calculateWater() {
        if (foodItem == null || selectedOption == null) return

        val amount = binding.etAmount.text.toString().toFloatOrNull() ?: 0f

        // This is the water content as a decimal (e.g., 87% -> 0.87)
        val waterPerUnit = foodItem!!.waterPercentage / 100f

        // We assume % for beverages is by volume (ml) and for food is by weight (g)
        // (Amount * Unit Value * Water Content) / 1000 (to get Liters)
        calculatedWaterInLiters = (amount * selectedOption!!.value * waterPerUnit) / 1000f

        binding.tvResult.text = String.format("Total Water: %.2fL", calculatedWaterInLiters)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        // Set the dialog size
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.90).toInt(), // 90% of screen width
            ViewGroup.LayoutParams.WRAP_CONTENT // Height wraps to content
        )
    }
}