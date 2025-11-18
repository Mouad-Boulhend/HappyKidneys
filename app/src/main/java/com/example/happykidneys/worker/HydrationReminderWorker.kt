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

        // 1. Check Global Switch
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
        // We assume the interval is stored in hours (e.g., 2)
        val intervalHours = preferenceManager.getReminderInterval()
        val shouldNotify = checkShouldNotify(preferenceManager.getUserId(), intervalHours)

        if (shouldNotify) {
            createNotificationChannel()
            sendNotification(currentHour)
        }

        return Result.success()
    }

    private suspend fun checkShouldNotify(userId: Long, intervalHours: Int): Boolean {
        val database = AppDatabase.getDatabase(applicationContext)
        val lastTimestamp = database.waterIntakeDao().getLastIntakeTimestamp(userId)

        // If no intake ever recorded, notify
        if (lastTimestamp == null) return true

        val currentTime = System.currentTimeMillis()
        val timeDiff = currentTime - lastTimestamp
        val intervalMillis = TimeUnit.HOURS.toMillis(intervalHours.toLong())

        // Notify only if the time passed is greater than the interval
        return timeDiff >= intervalMillis
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

    private fun sendNotification(currentHour: Int) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // 4. Dynamic Content based on Time of Day
        val (title, text) = getNotificationContent(currentHour)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_water_drop_small)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun getNotificationContent(hour: Int): Pair<String, String> {
        return when (hour) {
            in 6..11 -> {
                "Good Morning! ☀️" to "Start your day right with a glass of water."
            }
            in 12..16 -> {
                "Afternoon Boost 🚀" to "Feeling a dip in energy? Water might help!"
            }
            in 17..21 -> {
                "Evening Hydration 🌙" to "You're doing great! Hit your goal before bed."
            }
            else -> {
                "Hydration Time 💧" to "It's been a while. Time for a drink?"
            }
        }
    }
}