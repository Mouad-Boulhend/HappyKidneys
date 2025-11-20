package com.example.happykidneys.utils

import android.content.Context
import androidx.work.*
import com.example.happykidneys.worker.HydrationReminderWorker
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    fun scheduleReminders(context: Context, intervalHours: Int) {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(false)
            .build()

        // WorkManager minimum interval is 15 minutes.
        val safeInterval = if (intervalHours < 1) 1 else intervalHours

        val reminderRequest = PeriodicWorkRequestBuilder<HydrationReminderWorker>(
            safeInterval.toLong(), TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            HydrationReminderWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE, // Changed to UPDATE to persist schedule
            reminderRequest
        )
    }

    fun cancelReminders(context: Context) {
        WorkManager.getInstance(context)
            .cancelUniqueWork(HydrationReminderWorker.WORK_NAME)
    }
}