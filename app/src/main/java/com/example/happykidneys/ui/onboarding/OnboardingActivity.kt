package com.example.happykidneys.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.happykidneys.R
import com.example.happykidneys.databinding.ActivityOnboardingBinding
import com.example.happykidneys.ui.auth.AuthActivity
import com.example.happykidneys.utils.PreferenceManager

data class OnboardingItem(
    val image: Int,
    val title: String,
    val description: String
)

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var preferenceManager: PreferenceManager

    // Move this inside onCreate - don't initialize at class level!
    private lateinit var onboardingItems: List<OnboardingItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager = PreferenceManager(this)

        // Initialize onboarding items HERE, after context is available
        onboardingItems = listOf(
            OnboardingItem(
                R.drawable.ic_onboarding_1,
                getString(R.string.onboarding_title_1),
                getString(R.string.onboarding_desc_1)
            ),
            OnboardingItem(
                R.drawable.ic_onboarding_2,
                getString(R.string.onboarding_title_2),
                getString(R.string.onboarding_desc_2)
            ),
            OnboardingItem(
                R.drawable.ic_onboarding_3,
                getString(R.string.onboarding_title_3),
                getString(R.string.onboarding_desc_3)
            )
        )

        setupViewPager()
        setupIndicators()
        setupButtons()
    }

    private fun setupViewPager() {
        binding.viewPager.adapter = OnboardingAdapter(onboardingItems)
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateIndicators(position)
                updateButton(position)
            }
        })
    }

    private fun setupIndicators() {
        val indicators = arrayOfNulls<ImageView>(onboardingItems.size)
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)

        for (i in indicators.indices) {
            indicators[i] = ImageView(this)
            indicators[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.indicator_inactive
                )
            )
            indicators[i]?.layoutParams = layoutParams
            binding.indicatorLayout.addView(indicators[i])
        }
        updateIndicators(0)
    }

    private fun updateIndicators(position: Int) {
        for (i in 0 until binding.indicatorLayout.childCount) {
            val indicator = binding.indicatorLayout.getChildAt(i) as ImageView
            indicator.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    if (i == position) R.drawable.indicator_active else R.drawable.indicator_inactive
                )
            )
        }
    }

    private fun updateButton(position: Int) {
        if (position == onboardingItems.size - 1) {
            binding.btnNext.text = getString(R.string.get_started)
        } else {
            binding.btnNext.text = getString(R.string.next)
        }
    }

    private fun setupButtons() {
        binding.btnNext.setOnClickListener {
            if (binding.viewPager.currentItem < onboardingItems.size - 1) {
                binding.viewPager.currentItem += 1
            } else {
                finishOnboarding()
            }
        }

        binding.tvSkip.setOnClickListener {
            finishOnboarding()
        }
    }

    private fun finishOnboarding() {
        preferenceManager.setOnboardingCompleted(true)
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}

class OnboardingAdapter(private val items: List<OnboardingItem>) :
    RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnboardingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_onboarding, parent, false)
        return OnboardingViewHolder(view)
    }

    override fun onBindViewHolder(holder: OnboardingViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class OnboardingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val ivIllustration: ImageView = view.findViewById(R.id.ivIllustration)
        private val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        private val tvDescription: TextView = view.findViewById(R.id.tvDescription)

        fun bind(item: OnboardingItem) {
            ivIllustration.setImageResource(item.image)
            tvTitle.text = item.title
            tvDescription.text = item.description
        }
    }
}