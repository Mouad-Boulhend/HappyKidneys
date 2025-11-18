package com.example.happykidneys.utils

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "HappyKidneysPrefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
        private const val KEY_DAILY_GOAL = "daily_goal"
        private const val KEY_NOTIFICATION_ENABLED = "notification_enabled"
        private const val KEY_REMINDER_INTERVAL = "reminder_interval"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_WATER_ROTATION_ENABLED = "water_rotation_enabled" // <-- ADD THIS
    }

    // Onboarding
    fun isOnboardingCompleted(): Boolean {
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
    }

    // User Session
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun login(userId: Long, username: String, email: String, dailyGoal: Float) {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putLong(KEY_USER_ID, userId)
            putString(KEY_USERNAME, username)
            putString(KEY_EMAIL, email)
            putFloat(KEY_DAILY_GOAL, dailyGoal)
            apply()
        }
    }

    fun logout() {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, false)
            remove(KEY_USER_ID)
            remove(KEY_USERNAME)
            remove(KEY_EMAIL)
            apply()
        }
    }

    fun getUserId(): Long {
        return prefs.getLong(KEY_USER_ID, -1)
    }

    fun getUsername(): String {
        return prefs.getString(KEY_USERNAME, "") ?: ""
    }

    fun getEmail(): String {
        return prefs.getString(KEY_EMAIL, "") ?: ""
    }

    fun getDailyGoal(): Float {
        return prefs.getFloat(KEY_DAILY_GOAL, 2.0f)
    }

    fun setDailyGoal(goal: Float) {
        prefs.edit().putFloat(KEY_DAILY_GOAL, goal).apply()
    }

    // Notifications
    fun isNotificationEnabled(): Boolean {
        return prefs.getBoolean(KEY_NOTIFICATION_ENABLED, true)
    }

    fun setNotificationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NOTIFICATION_ENABLED, enabled).apply()
    }

    fun getReminderInterval(): Int {
        return prefs.getInt(KEY_REMINDER_INTERVAL, 2) // Default 2 hours
    }

    fun setReminderInterval(hours: Int) {
        prefs.edit().putInt(KEY_REMINDER_INTERVAL, hours).apply()
    }

    // Language
    fun getLanguage(): String {
        return prefs.getString(KEY_LANGUAGE, "en") ?: "en"
    }

    fun setLanguage(language: String) {
        prefs.edit().putString(KEY_LANGUAGE, language).apply()
    }

    // --- WaterRotationPhysics ---
    fun isWaterRotationEnabled(): Boolean {
        return prefs.getBoolean(KEY_WATER_ROTATION_ENABLED, true) // Default to true
    }

    fun setWaterRotationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_WATER_ROTATION_ENABLED, enabled).apply()
    }
}