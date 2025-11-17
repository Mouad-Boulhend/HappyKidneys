package com.example.happykidneys.utils

object Constants {

    // Database
    const val DATABASE_NAME = "happy_kidneys_database"
    const val DATABASE_VERSION = 1

    // Shared Preferences
    const val PREF_NAME = "HappyKidneysPrefs"

    // Default Values
    const val DEFAULT_DAILY_GOAL = 2.0f // liters
    const val DEFAULT_REMINDER_INTERVAL = 2 // hours
    const val MIN_AGE = 18

    // Water Amounts (in liters)
    const val QUICK_ADD_250ML = 0.25f
    const val QUICK_ADD_500ML = 0.5f
    const val QUICK_ADD_1L = 1.0f

    // Notification
    const val NOTIFICATION_CHANNEL_ID = "hydration_reminders"
    const val NOTIFICATION_ID = 1001

    // WorkManager
    const val WORK_NAME = "hydration_reminder_work"

    // Date Formats
    const val DATE_FORMAT = "yyyy-MM-dd"
    const val DISPLAY_DATE_FORMAT = "MMM dd, yyyy"
    const val TIME_FORMAT = "HH:mm"
    const val BIRTHDAY_INPUT_FORMAT = "dd/MM/yyyy"

    // Validation
    const val MIN_PASSWORD_LENGTH = 6
    const val MAX_DAILY_GOAL = 10.0f // liters
    const val MIN_DAILY_GOAL = 0.5f // liters

    // UI
    const val ANIMATION_DURATION = 300L
    const val SPLASH_DELAY = 2500L

    // Achievement Thresholds
    const val ACHIEVEMENT_QUARTER = 25
    const val ACHIEVEMENT_HALF = 50
    const val ACHIEVEMENT_THREE_QUARTERS = 75
    const val ACHIEVEMENT_COMPLETE = 100

    // Languages
    const val LANG_ENGLISH = "en"
    const val LANG_FRENCH = "fr"
    const val LANG_ARABIC = "ar"

    // Chart
    const val CHART_ANIMATION_DURATION = 1000
    const val CHART_DAYS_TO_SHOW = 7
}