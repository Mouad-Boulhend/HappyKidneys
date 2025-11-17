package com.example.happykidneys.ui.goals

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.happykidneys.data.repository.UserRepository
import com.example.happykidneys.utils.PreferenceManager
import kotlinx.coroutines.launch

class GoalCalculatorViewModel : ViewModel() {

    // Internal private live data
    private val _weightInKg = MutableLiveData(70f)
    private val _activityLevel = MutableLiveData("sedentary") // "sedentary", "moderate", "active"

    // Public-facing LiveData for fragments to observe
    val weightInKg: LiveData<Float> = _weightInKg
    val activityLevel: LiveData<String> = _activityLevel

    private var calculatedGoal: Float = 0f

    fun setWeight(weight: Float, unit: String) {
        if (unit == "lbs") {
            _weightInKg.value = weight * 0.453592f
        } else {
            _weightInKg.value = weight
        }
    }

    fun setActivityLevel(level: String) {
        _activityLevel.value = level
    }

    /**
     * Calculates the recommended goal based on stored values.
     */
    fun calculateGoal(): Float {
        val weight = _weightInKg.value ?: 70f
        val activity = _activityLevel.value ?: "sedentary"

        // 1. Base Goal: 30ml per kg of body weight
        var goalInLiters = (weight * 30f) / 1000f // e.g., 70kg * 30ml = 2.1L

        // 2. Adjust for Activity
        when (activity) {
            "moderate" -> goalInLiters += 0.5f // Add 500ml for moderate activity
            "active" -> goalInLiters += 1.0f  // Add 1L for high activity
        }

        // Round to one decimal place
        calculatedGoal = (goalInLiters * 10).toInt() / 10f
        return calculatedGoal
    }

    /**
     * Saves the last calculated goal to the database and preferences.
     * This logic is copied from your GoalsFragment.
     */
    fun saveGoal(
        userId: Long,
        userRepository: UserRepository,
        preferenceManager: PreferenceManager
    ) {
        viewModelScope.launch {
            if (calculatedGoal > 0) {
                userRepository.updateDailyGoal(userId, calculatedGoal) //
                preferenceManager.setDailyGoal(calculatedGoal) //
            }
        }
    }
}