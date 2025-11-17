// File: ui/MainActivity.kt
package com.example.happykidneys.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.happykidneys.R
import com.example.happykidneys.databinding.ActivityMainBinding
import com.example.happykidneys.ui.dashboard.DashboardFragment
import com.example.happykidneys.ui.profile.ProfileFragment
import com.example.happykidneys.ui.tips.TipsFragment
import com.example.happykidneys.ui.goals.GoalsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupBottomNavigation()
    }

    private fun setupViewPager() {
        val adapter = MainPagerAdapter(this)
        binding.viewPager.adapter = adapter
        binding.viewPager.isUserInputEnabled = true

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.bottomNavigation.menu.getItem(position).isChecked = true
            }
        })
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    binding.viewPager.currentItem = 0
                    true
                }
                R.id.nav_goals -> {
                    binding.viewPager.currentItem = 1
                    true
                }
                R.id.nav_tips -> {
                    binding.viewPager.currentItem = 2
                    true
                }
                R.id.nav_profile -> {
                    binding.viewPager.currentItem = 3
                    true
                }
                else -> false
            }
        }
    }

    private class MainPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int = 4

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> DashboardFragment()
                1 -> GoalsFragment()
                2 -> TipsFragment()
                3 -> ProfileFragment()
                else -> DashboardFragment()
            }
        }
    }
}