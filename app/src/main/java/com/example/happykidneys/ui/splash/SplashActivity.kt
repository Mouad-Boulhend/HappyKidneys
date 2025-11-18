package com.example.happykidneys.ui.splash

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.happykidneys.databinding.ActivitySplashBinding
import com.example.happykidneys.ui.MainActivity
import com.example.happykidneys.ui.onboarding.OnboardingActivity
import com.example.happykidneys.ui.auth.AuthActivity
import com.example.happykidneys.utils.PreferenceManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferenceManager = PreferenceManager(this)

        // Animate logo
        animateLogo()

        // Navigate after delay
        lifecycleScope.launch {
            delay(2500)
            navigateToNextScreen()
        }
    }

    private fun animateLogo() {
        // Scale animation
        binding.ivLogo.scaleX = 0f
        binding.ivLogo.scaleY = 0f
        binding.ivLogo.alpha = 0f

        binding.ivLogo.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(800)
            .setInterpolator(DecelerateInterpolator())
            .start()

        // Fade in text
        binding.tvAppName.alpha = 0f
        binding.tvTagline.alpha = 0f

        binding.tvAppName.animate()
            .alpha(1f)
            .setStartDelay(400)
            .setDuration(600)
            .start()

        binding.tvTagline.animate()
            .alpha(1f)
            .setStartDelay(600)
            .setDuration(600)
            .start()
    }

    private fun navigateToNextScreen() {
        val intent = when {
            !preferenceManager.isOnboardingCompleted() -> {
                Intent(this, OnboardingActivity::class.java)
            }
            !preferenceManager.isLoggedIn() -> {
                Intent(this, AuthActivity::class.java)
            }
            else -> {
                Intent(this, MainActivity::class.java)
            }
        }
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.lottieLoading.cancelAnimation()
    }
}