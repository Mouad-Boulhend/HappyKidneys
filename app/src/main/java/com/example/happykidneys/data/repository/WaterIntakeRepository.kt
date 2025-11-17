package com.example.happykidneys.data.repository

import com.example.happykidneys.data.database.dao.DailyTotal
import com.example.happykidneys.data.database.dao.WaterIntakeDao
import com.example.happykidneys.data.database.entities.WaterIntake
import kotlinx.coroutines.flow.Flow

class WaterIntakeRepository(private val waterIntakeDao: WaterIntakeDao) {

    fun getIntakesForDate(userId: Long, date: String): Flow<List<WaterIntake>> {
        return waterIntakeDao.getIntakesForDate(userId, date)
    }

    fun getTotalForDate(userId: Long, date: String): Flow<Float?> {
        return waterIntakeDao.getTotalForDate(userId, date)
    }

    fun getWeeklyIntakes(userId: Long, startDate: String): Flow<List<DailyTotal>> {
        return waterIntakeDao.getWeeklyIntakes(userId, startDate)
    }

    suspend fun insert(waterIntake: WaterIntake) {
        waterIntakeDao.insert(waterIntake)
    }

    suspend fun delete(waterIntake: WaterIntake) {
        waterIntakeDao.delete(waterIntake)
    }
}
