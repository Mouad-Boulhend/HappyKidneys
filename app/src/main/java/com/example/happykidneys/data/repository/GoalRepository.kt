package com.example.happykidneys.data.repository

import com.example.happykidneys.data.database.dao.GoalDao
import com.example.happykidneys.data.database.entities.Goal
import kotlinx.coroutines.flow.Flow

class GoalRepository(private val goalDao: GoalDao) {

    suspend fun insert(goal: Goal) {
        goalDao.insert(goal)
    }

    fun getAllGoals(userId: Long): Flow<List<Goal>> {
        return goalDao.getAllGoals(userId)
    }

    fun getGoalForDate(userId: Long, date: String): Flow<Goal?> {
        return goalDao.getGoalForDate(userId, date)
    }

    suspend fun updateGoalProgress(userId: Long, date: String, amount: Float, achieved: Boolean) {
        goalDao.updateGoalProgress(userId, date, amount, achieved)
    }
}