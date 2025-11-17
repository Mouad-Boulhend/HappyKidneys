package com.example.happykidneys.ui.dashboard

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class AddIntakePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AddWaterTabFragment()
            1 -> AddFoodTabFragment()
            else -> throw IllegalStateException("Invalid position $position")
        }
    }
}