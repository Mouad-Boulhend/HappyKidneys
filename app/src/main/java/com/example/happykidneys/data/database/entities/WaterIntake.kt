package com.example.happykidneys.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_intakes")
data class WaterIntake(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val amount: Float, // in liters
    val timestamp: Long = System.currentTimeMillis(),
    val date: String // Format: "yyyy-MM-dd"
)