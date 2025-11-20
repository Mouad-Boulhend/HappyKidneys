package com.example.wear

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.wear.R
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable

class WearActivityMain : Activity(), DataClient.OnDataChangedListener {

    private lateinit var tvAmount: TextView
    private lateinit var btnAdd: Button
    private val TAG = "WearMainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wear_main)

        tvAmount = findViewById(R.id.tvWatchAmount)
        btnAdd = findViewById(R.id.btnAdd250)

        btnAdd.setOnClickListener {
            sendAddWaterToPhone(0.25f)
        }

        Log.d(TAG, "Wear app started")
    }

    override fun onResume() {
        super.onResume()
        Wearable.getDataClient(this).addListener(this)

        // FIX: Immediately check for the latest data when the screen turns on
        fetchCurrentWaterIntake()
    }

    override fun onPause() {
        super.onPause()
        Wearable.getDataClient(this).removeListener(this)
    }

    // --- NEW HELPER FUNCTION ---
    private fun fetchCurrentWaterIntake() {
        Wearable.getDataClient(this).dataItems
            .addOnSuccessListener { dataItems ->
                // Look through all stored data items for our path
                for (item in dataItems) {
                    if (item.uri.path == "/water_intake") {
                        val dataMapItem = DataMapItem.fromDataItem(item)
                        val liters = dataMapItem.dataMap.getFloat("total_liters")

                        // Update UI immediately
                        tvAmount.text = String.format("%.1f L", liters)
                    }
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "Failed to fetch current water intake", it)
            }
    }

    // --- SENDING DATA TO PHONE ---
    private fun sendAddWaterToPhone(amount: Float) {
        val putDataReq = PutDataMapRequest.create("/add_water_from_watch").apply {
            dataMap.putFloat("amount", amount)
            dataMap.putLong("timestamp", System.currentTimeMillis())
        }
        val putDataRequest = putDataReq.asPutDataRequest()
        putDataRequest.setUrgent()

        Wearable.getDataClient(this).putDataItem(putDataRequest)
    }

    // --- RECEIVING DATA FROM PHONE (Live updates) ---
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val path = event.dataItem.uri.path

                if (path == "/water_intake") {
                    val dataMapItem = DataMapItem.fromDataItem(event.dataItem)
                    val liters = dataMapItem.dataMap.getFloat("total_liters")

                    Log.d(TAG, "Received liters: $liters")

                    runOnUiThread {
                        tvAmount.text = String.format("%.1f L", liters)
                    }
                }
            }
        }
    }
}