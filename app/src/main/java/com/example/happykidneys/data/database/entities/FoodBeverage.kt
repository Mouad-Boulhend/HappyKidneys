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
    val waterPercentage: Float, // This remains, e.g., 87 for 87%
    val iconName: String,

    // --- NEW NULLABLE FIELDS ---

    // Default serving info (for the grid view)
    val servingSize: String?, // e.g., "1 medium"
    val waterInLiters: Float?, // e.g., 0.15

    // New fields for quantity selection (g)
    val weightSmall_g: Float? = null,   // e.g., 100
    val weightMedium_g: Float? = null,  // e.g., 150
    val weightLarge_g: Float? = null,   // e.g., 200
    val weightUnit_g: Float? = 1f,      // To allow "grams"

    // New fields for quantity selection (ml)
    val volumeCup_ml: Float? = null,    // e.g., 240
    val volumeUnit_ml: Float? = 1f       // To allow "ml"
)