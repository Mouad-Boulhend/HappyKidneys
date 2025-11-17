package com.example.happykidneys.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.happykidneys.databinding.DialogAddWaterBinding

class AddWaterTabFragment : Fragment() {

    private var _binding: DialogAddWaterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddWaterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Quick Add buttons
        binding.btn250ml.setOnClickListener { sendResult(0.25f) }
        binding.btn500ml.setOnClickListener { sendResult(0.5f) }
        binding.btn1000ml.setOnClickListener { sendResult(1.0f) }

        // --- NOTE ---
        // Your old dialog_add_water.xml has hardcoded food buttons
        // (Orange, Watermelon, Coffee). You should remove them from that XML
        // since you now have a dedicated "Food & Drinks" tab.

        // Custom amount
        binding.btnAdd.setOnClickListener {
            val amountText = binding.etAmount.text.toString()
            if (amountText.isNotEmpty()) {
                val amount = amountText.toFloatOrNull()
                if (amount != null && amount > 0) {
                    sendResult(amount)
                } else {
                    Toast.makeText(requireContext(), "Invalid amount", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Hide the cancel button, the dialog will handle this
        binding.btnCancel.visibility = View.GONE
    }

    private fun sendResult(amount: Float) {
        val result = Bundle().apply {
            putFloat("waterAmount", amount)
        }
        // Send the result to the parent (the main dialog)
        parentFragmentManager.setFragmentResult("intakeRequest", result)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}