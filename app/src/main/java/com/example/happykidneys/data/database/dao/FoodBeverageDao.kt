package com.example.happykidneys.data.database.dao

import androidx.room.*
import com.example.happykidneys.data.database.entities.FoodBeverage
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodBeverageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FoodBeverage>)

    @Query("SELECT * FROM food_beverages ORDER BY name ASC")
    fun getAllItems(): Flow<List<FoodBeverage>>

    @Query("SELECT * FROM food_beverages WHERE category = :category ORDER BY name ASC")
    fun getItemsByCategory(category: String): Flow<List<FoodBeverage>>

    @Query("SELECT * FROM food_beverages WHERE name LIKE '%' || :query || '%' OR nameAr LIKE '%' || :query || '%' OR nameFr LIKE '%' || :query || '%'")
    fun searchItems(query: String): Flow<List<FoodBeverage>>

    @Query("SELECT COUNT(*) FROM food_beverages")
    suspend fun getCount(): Int

    @Query("SELECT * FROM food_beverages WHERE id = :id LIMIT 1")
    suspend fun getItemById(id: Long): FoodBeverage?


}