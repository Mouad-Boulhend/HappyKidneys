package com.example.happykidneys.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.happykidneys.R
import com.example.happykidneys.data.database.AppDatabase
import com.example.happykidneys.ui.MainActivity
import com.example.happykidneys.utils.PreferenceManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

class HydrationReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val CHANNEL_ID = "hydration_reminders"
        const val NOTIFICATION_ID = 1001
        const val WORK_NAME = "hydration_reminder_work"
    }

    override suspend fun doWork(): Result {
        val preferenceManager = PreferenceManager(applicationContext)

        // 1. Check if notifications are enabled
        if (!preferenceManager.isNotificationEnabled()) {
            return Result.success()
        }

        // 2. Check Time Window (7 AM to 9 PM)
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if (currentHour < 7 || currentHour >= 21) {
            // It's night time, don't disturb the user
            return Result.success()
        }

        // 3. Check Last Intake Time
        val userId = preferenceManager.getUserId()
        if (userId == -1L) return Result.success()

        val database = AppDatabase.getDatabase(applicationContext)
        val lastIntakeTimestamp = database.waterIntakeDao().getLastIntakeTimestamp(userId)

        val intervalHours = preferenceManager.getReminderInterval()
        val intervalMillis = TimeUnit.HOURS.toMillis(intervalHours.toLong())
        val currentTime = System.currentTimeMillis()

        // If user drank water recently (within the interval), don't notify
        if (lastIntakeTimestamp != null && (currentTime - lastIntakeTimestamp) < intervalMillis) {
            return Result.success()
        }

        // 4. Send Notification
        createNotificationChannel()
        sendNotification(currentHour)

        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = applicationContext.getString(R.string.notification_channel_name)
            val descriptionText = applicationContext.getString(R.string.notification_channel_desc)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(hour: Int) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val (title, message) = getNotificationContent(hour)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_water_drop_small) // Ensure you have this icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun getNotificationContent(hour: Int): Pair<String, String> {
        return when (hour) {
            in 7..11 -> "Good Morning! ☀️" to "Start your day right with a glass of water."
            in 12..16 -> "Afternoon Boost 🚀" to "Stay energized! Time for some water."
            in 17..21 -> "Evening Hydration 🌙" to "Don't forget to hydrate before bed."
            else -> "Hydration Time 💧" to "It's time to drink some water!"
        }
    }
}