package com.example.happykidneys.data.database.dao

import androidx.room.*
import com.example.happykidneys.data.database.entities.WaterIntake
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterIntakeDao {
    @Insert
    suspend fun insert(waterIntake: WaterIntake)

    @Query("SELECT * FROM water_intakes WHERE userId = :userId AND date = :date ORDER BY timestamp DESC")
    fun getIntakesForDate(userId: Long, date: String): Flow<List<WaterIntake>>

    @Query("SELECT SUM(amount) FROM water_intakes WHERE userId = :userId AND date = :date")
    fun getTotalForDate(userId: Long, date: String): Flow<Float?>

    @Query("SELECT date, SUM(amount) as total FROM water_intakes WHERE userId = :userId AND date >= :startDate GROUP BY date ORDER BY date")
    fun getWeeklyIntakes(userId: Long, startDate: String): Flow<List<DailyTotal>>

    @Delete
    suspend fun delete(waterIntake: WaterIntake)
}

data class DailyTotal(
    val date: String,
    val total: Float
)