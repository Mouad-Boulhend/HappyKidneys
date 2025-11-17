package com.example.happykidneys.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.happykidneys.databinding.DialogAddWaterEnhancedBinding
import com.google.android.material.tabs.TabLayoutMediator

class AddIntakeDialogFragment : DialogFragment() {

    private var _binding: DialogAddWaterEnhancedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddWaterEnhancedBinding.inflate(inflater, container, false)

        // 1. Set up the ViewPager adapter
        val pagerAdapter = AddIntakePagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        // 2. Link the TabLayout to the ViewPager
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Water"
                1 -> "Food & Drinks"
                else -> null
            }
        }.attach()

        // 3. Listen for results from either tab
        childFragmentManager.setFragmentResultListener("intakeRequest", this) { requestKey, bundle ->
            val waterAmount = bundle.getFloat("waterAmount")
            if (waterAmount > 0) {
                // Send the final result back to the DashboardFragment
                val result = Bundle().apply {
                    putFloat("waterAmount", waterAmount)
                }
                parentFragmentManager.setFragmentResult("addIntakeResult", result)
                dismiss() // Close the dialog
            }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        // Set the dialog size
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.95).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AddIntakeDialog"
    }
}