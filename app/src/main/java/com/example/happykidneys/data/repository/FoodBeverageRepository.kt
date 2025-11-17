package com.example.happykidneys.data.repository

import com.example.happykidneys.data.database.dao.FoodBeverageDao
import com.example.happykidneys.data.database.entities.FoodBeverage
import kotlinx.coroutines.flow.Flow

class FoodBeverageRepository(private val foodBeverageDao: FoodBeverageDao) {

    fun getAllItems(): Flow<List<FoodBeverage>> = foodBeverageDao.getAllItems()

    fun getItemsByCategory(category: String): Flow<List<FoodBeverage>> =
        foodBeverageDao.getItemsByCategory(category)

    fun searchItems(query: String): Flow<List<FoodBeverage>> =
        foodBeverageDao.searchItems(query)

    suspend fun insertAll(items: List<FoodBeverage>) = foodBeverageDao.insertAll(items)

    suspend fun getCount(): Int = foodBeverageDao.getCount()

    suspend fun getItemById(id: Long): FoodBeverage? {
        return foodBeverageDao.getItemById(id)
    }
}