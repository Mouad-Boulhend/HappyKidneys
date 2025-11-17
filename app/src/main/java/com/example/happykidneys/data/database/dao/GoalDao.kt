package com.example.happykidneys.data.database.dao

import androidx.room.*
import com.example.happykidneys.data.database.entities.Goal
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: Goal)

    @Query("SELECT * FROM goals WHERE userId = :userId ORDER BY date DESC")
    fun getAllGoals(userId: Long): Flow<List<Goal>>

    @Query("SELECT * FROM goals WHERE userId = :userId AND date = :date LIMIT 1")
    fun getGoalForDate(userId: Long, date: String): Flow<Goal?>

    @Query("UPDATE goals SET actualAmount = :amount, achieved = :achieved WHERE userId = :userId AND date = :date")
    suspend fun updateGoalProgress(userId: Long, date: String, amount: Float, achieved: Boolean)
}