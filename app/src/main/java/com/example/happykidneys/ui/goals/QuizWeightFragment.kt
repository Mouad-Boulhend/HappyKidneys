package com.example.happykidneys.ui.goals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.happykidneys.databinding.FragmentQuizWeightBinding

class QuizWeightFragment : Fragment() {

    private var _binding: FragmentQuizWeightBinding? = null
    private val binding get() = _binding!!

    // Get the ViewModel shared by the Activity
    private val viewModel: GoalCalculatorViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizWeightBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNext.setOnClickListener {
            val weightText = binding.etWeight.text.toString()
            if (weightText.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter your weight", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            saveData()
            (activity as? GoalCalculatorActivity)?.nextPage()
        }

        // Save data to ViewModel whenever it changes
        binding.etWeight.doOnTextChanged { _, _, _, _ -> saveData() }
        binding.rgUnits.setOnCheckedChangeListener { _, _ -> saveData() }
    }

    private fun saveData() {
        val weight = binding.etWeight.text.toString().toFloatOrNull() ?: 0f
        val unit = if (binding.rbKg.isChecked) "kg" else "lbs"
        viewModel.setWeight(weight, unit)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}