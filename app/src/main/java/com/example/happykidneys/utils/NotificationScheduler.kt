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

        val reminderRequest = PeriodicWorkRequestBuilder<HydrationReminderWorker>(
            intervalHours.toLong(), TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .setInitialDelay(intervalHours.toLong(), TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            HydrationReminderWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            reminderRequest
        )
    }

    fun cancelReminders(context: Context) {
        WorkManager.getInstance(context)
            .cancelUniqueWork(HydrationReminderWorker.WORK_NAME)
    }
}