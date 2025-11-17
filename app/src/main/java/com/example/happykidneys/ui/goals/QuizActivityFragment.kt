package com.example.happykidneys.ui.goals

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.happykidneys.R
import com.example.happykidneys.databinding.FragmentQuizActivityBinding

class QuizActivityFragment : Fragment() {

    private var _binding: FragmentQuizActivityBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GoalCalculatorViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizActivityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rgActivity.setOnCheckedChangeListener { _, checkedId ->
            val level = when (checkedId) {
                R.id.rbModerate -> "moderate"
                R.id.rbActive -> "active"
                else -> "sedentary"
            }
            viewModel.setActivityLevel(level)
        }

        binding.btnBack.setOnClickListener {
            (activity as? GoalCalculatorActivity)?.previousPage()
        }

        binding.btnNext.setOnClickListener {
            (activity as? GoalCalculatorActivity)?.nextPage()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}