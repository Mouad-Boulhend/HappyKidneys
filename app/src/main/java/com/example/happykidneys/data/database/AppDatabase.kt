// File: data/database/AppDatabase.kt
package com.example.happykidneys.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.happykidneys.data.database.dao.GoalDao
import com.example.happykidneys.data.database.dao.UserDao
import com.example.happykidneys.data.database.dao.WaterIntakeDao
import com.example.happykidneys.data.database.dao.FoodBeverageDao
import com.example.happykidneys.data.database.entities.Goal
import com.example.happykidneys.data.database.entities.User
import com.example.happykidneys.data.database.entities.WaterIntake
import com.example.happykidneys.data.database.entities.FoodBeverage
import com.example.happykidneys.utils.FoodBeverageData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [User::class, WaterIntake::class, Goal::class, FoodBeverage::class],
    version = 2,  // Increment version since we added a new entity
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun waterIntakeDao(): WaterIntakeDao
    abstract fun goalDao(): GoalDao
    abstract fun foodBeverageDao(): FoodBeverageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "happy_kidneys_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateFoodBeverageData(database.foodBeverageDao())
                    }
                }
            }
        }

        private suspend fun populateFoodBeverageData(dao: FoodBeverageDao) {
            try {
                if (dao.getCount() == 0) {
                    dao.insertAll(FoodBeverageData.getAllItems())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}