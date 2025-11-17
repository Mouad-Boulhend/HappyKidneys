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
    version = 3, // This version is correct
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
                    // Pass the context to the callback
                    .addCallback(DatabaseCallback(context.applicationContext))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // --- THIS CALLBACK IS NOW FIXED ---
        private class DatabaseCallback(
            private val context: Context // <-- 1. Take context
        ) : RoomDatabase.Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // 2. Launch a coroutine
                CoroutineScope(Dispatchers.IO).launch {
                    // 3. Get the database instance (it's now safe to do so)
                    val dao = getDatabase(context).foodBeverageDao()
                    populateFoodBeverageData(dao)
                }
            }
        }
        // --- END FIX ---

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