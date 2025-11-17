package com.example.happykidneys.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_beverages")
data class FoodBeverage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val nameAr: String,
    val nameFr: String,
    val category: String,
    val waterPercentage: Float,
    val servingSize: String,
    val waterInLiters: Float,
    val iconName: String
)