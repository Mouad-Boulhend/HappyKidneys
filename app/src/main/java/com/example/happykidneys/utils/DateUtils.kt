package com.example.happykidneys.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())

    fun getCurrentDate(): String {
        return dateFormat.format(Date())
    }

    fun getFormattedDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }

    fun getDisplayDate(dateString: String): String {
        return try {
            val date = dateFormat.parse(dateString)
            displayDateFormat.format(date ?: Date())
        } catch (e: Exception) {
            dateString
        }
    }

    fun getDisplayDate(timestamp: Long): String {
        return displayDateFormat.format(Date(timestamp))
    }

    fun getTime(timestamp: Long): String {
        return timeFormat.format(Date(timestamp))
    }

    fun getDayName(dateString: String): String {
        return try {
            val date = dateFormat.parse(dateString)
            dayFormat.format(date ?: Date())
        } catch (e: Exception) {
            ""
        }
    }

    fun getStartOfWeek(): String {
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        return dateFormat.format(calendar.time)
    }

    fun getDateDaysAgo(days: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        return dateFormat.format(calendar.time)
    }

    fun calculateAge(birthdayTimestamp: Long): Int {
        val birthday = Calendar.getInstance()
        birthday.timeInMillis = birthdayTimestamp

        val today = Calendar.getInstance()

        var age = today.get(Calendar.YEAR) - birthday.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < birthday.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return age
    }

    fun isToday(dateString: String): Boolean {
        return dateString == getCurrentDate()
    }

    fun getWeekDates(): List<String> {
        val dates = mutableListOf<String>()
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        for (i in 0..6) {
            dates.add(dateFormat.format(calendar.time))
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return dates
    }
}