package com.example.happykidneys.data.repository

import com.example.happykidneys.data.database.dao.UserDao
import com.example.happykidneys.data.database.entities.User
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    suspend fun insert(user: User): Long {
        return userDao.insert(user)
    }

    suspend fun update(user: User) {
        userDao.update(user)
    }

    suspend fun login(email: String, password: String): User? {
        return userDao.login(email, password)
    }

    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }

    fun getUserById(userId: Long): Flow<User?> {
        return userDao.getUserById(userId)
    }

    suspend fun updateDailyGoal(userId: Long, goal: Float) {
        userDao.updateDailyGoal(userId, goal)
    }
}
