package com.example.happykidneys.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val targetAmount: Float, // in liters
    val date: String, // Format: "yyyy-MM-dd"
    val achieved: Boolean = false,
    val actualAmount: Float = 0f
)