package com.example.happykidneys.ui.tips

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.happykidneys.R
import com.example.happykidneys.databinding.FragmentTipsBinding

data class HydrationTip(
    val title: String,
    val description: String,
    val icon: Int
)

class TipsFragment : Fragment() {

    private var _binding: FragmentTipsBinding? = null
    private val binding get() = _binding!!
    private lateinit var tipsAdapter: TipsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTipsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadTips()
    }

    private fun setupRecyclerView() {
        tipsAdapter = TipsAdapter { tip ->
            shareTip(tip)
        }
        binding.rvTips.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = tipsAdapter
        }
    }

    private fun loadTips() {
        // Create tips list HERE where context is available
        val tips = listOf(
            HydrationTip(
                getString(R.string.tip_title_1),
                getString(R.string.tip_desc_1),
                R.drawable.ic_sunrise
            ),
            HydrationTip(
                getString(R.string.tip_title_2),
                getString(R.string.tip_desc_2),
                R.drawable.ic_restaurant
            ),
            HydrationTip(
                getString(R.string.tip_title_3),
                getString(R.string.tip_desc_3),
                R.drawable.ic_fitness
            ),
            HydrationTip(
                getString(R.string.tip_title_4),
                getString(R.string.tip_desc_4),
                R.drawable.ic_ear
            ),
            HydrationTip(
                getString(R.string.tip_title_5),
                getString(R.string.tip_desc_5),
                R.drawable.ic_science
            ),
            HydrationTip(
                "Carry a Water Bottle",
                "Always keep a reusable water bottle with you as a constant reminder to drink water",
                R.drawable.ic_local_drink
            ),
            HydrationTip(
                "Set Reminders",
                "Use this app's reminder feature to get notifications throughout the day",
                R.drawable.ic_alarm
            ),
            HydrationTip(
                "Eat Water-Rich Foods",
                "Include fruits and vegetables with high water content like watermelon, cucumber, and oranges",
                R.drawable.ic_apple
            ),
            HydrationTip(
                "Track Your Progress",
                "Regularly monitor your water intake to stay motivated and build healthy habits",
                R.drawable.ic_trending_up
            ),
            HydrationTip(
                "Temperature Matters",
                "Cold water can be more refreshing during exercise, while room temperature water is easier to drink in large amounts",
                R.drawable.ic_thermostat
            )
        )

        tipsAdapter.submitList(tips)
    }

    private fun shareTip(tip: HydrationTip) {
        val shareText = "${tip.title}\n\n${tip.description}\n\nShared from Happy Kidneys App"
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(shareIntent, "Share Tip"))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}