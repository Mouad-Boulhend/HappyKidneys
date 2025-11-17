package com.example.happykidneys.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val email: String,
    val password: String,
    val birthday: Long,
    val dailyGoal: Float = 2.0f, // in liters
    val createdAt: Long = System.currentTimeMillis()
)