package com.example.happykidneys.data.database.dao

import androidx.room.*
import com.example.happykidneys.data.database.entities.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User): Long

    @Update
    suspend fun update(user: User)

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): User?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: Long): Flow<User?>

    @Query("UPDATE users SET dailyGoal = :goal WHERE id = :userId")
    suspend fun updateDailyGoal(userId: Long, goal: Float)
}

