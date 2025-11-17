package com.example.happykidneys

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import com.example.happykidneys.utils.NotificationScheduler
import com.example.happykidneys.utils.PreferenceManager
import com.example.happykidneys.worker.HydrationReminderWorker
import java.util.*

class HappyKidneysApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Create notification channel for Android O and above
        createNotificationChannel()

        // Schedule notifications if enabled
        val preferenceManager = PreferenceManager(this)
        if (preferenceManager.isLoggedIn() && preferenceManager.isNotificationEnabled()) {
            val interval = preferenceManager.getReminderInterval()
            NotificationScheduler.scheduleReminders(this, interval)
        }

        // Apply saved language
        val prefs = getSharedPreferences("HappyKidneysPrefs", Context.MODE_PRIVATE)
        val language = prefs.getString("language", "en") ?: "en"
        setLocale(language)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val descriptionText = getString(R.string.notification_channel_desc)
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(
                HydrationReminderWorker.CHANNEL_ID,
                name,
                importance
            ).apply {
                description = descriptionText
                enableVibration(true)
                setShowBadge(true)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}