package com.example.happykidneys.ui.goals

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.happykidneys.databinding.ActivityGoalCalculatorBinding

class GoalCalculatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalCalculatorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = GoalCalculatorAdapter(this)
        binding.viewPager.adapter = adapter
        binding.viewPager.isUserInputEnabled = false // Prevent swiping
    }

    fun nextPage() {
        binding.viewPager.currentItem = binding.viewPager.currentItem + 1
    }

    fun previousPage() {
        binding.viewPager.currentItem = binding.viewPager.currentItem - 1
    }

    // ViewPager Adapter
    private class GoalCalculatorAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 3 // 3 screens: Weight, Activity, Result

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> QuizWeightFragment()
                1 -> QuizActivityFragment()
                2 -> QuizResultFragment()
                else -> throw IllegalStateException("Invalid position")
            }
        }
    }
}