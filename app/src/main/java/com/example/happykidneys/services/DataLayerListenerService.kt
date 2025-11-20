package com.example.happykidneys.services

import com.example.happykidneys.data.database.AppDatabase
import com.example.happykidneys.data.database.entities.WaterIntake
import com.example.happykidneys.data.repository.WaterIntakeRepository
import com.example.happykidneys.utils.PreferenceManager
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DataLayerListenerService : WearableListenerService() {

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = WaterIntakeRepository(database.waterIntakeDao())
        val preferenceManager = PreferenceManager(applicationContext)
        val userId = preferenceManager.getUserId()

        if (userId == -1L) return // User not logged in

        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val path = event.dataItem.uri.path
                if (path == "/add_water_from_watch") {
                    val dataMapItem = DataMapItem.fromDataItem(event.dataItem)
                    val amount = dataMapItem.dataMap.getFloat("amount")

                    // Add the water to the database
                    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                    CoroutineScope(Dispatchers.IO).launch {
                        repository.insert(WaterIntake(
                            userId = userId,
                            amount = amount,
                            date = today
                        ))
                    }
                }
            }
        }
    }
}