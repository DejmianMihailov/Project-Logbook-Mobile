package com.example.mobilelogbook.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.mobilelogbook.ui.MainScreen
import com.example.mobilelogbook.repository.FlightRepository
import com.example.mobilelogbook.data.FlightDatabase
import com.example.mobilelogbook.data.ApiService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val flightDao = FlightDatabase.getDatabase(applicationContext).flightDao()
        val apiService = ApiService.create()
        val repository = FlightRepository(flightDao, apiService)

        setContent {
            MainScreen(repository)
        }
    }
}
