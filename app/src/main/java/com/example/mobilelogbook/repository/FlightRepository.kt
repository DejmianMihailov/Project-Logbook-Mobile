package com.example.mobilelogbook.repository

import android.util.Log
import com.example.mobilelogbook.data.ApiService
import com.example.mobilelogbook.data.FlightDao
import com.example.mobilelogbook.data.FlightEntity

class FlightRepository(
    private val flightDao: FlightDao,
    private val apiService: ApiService
) {
    // Добавяне на нов полет (локално + Supabase)
    suspend fun addFlight(flight: FlightEntity) {
        flightDao.insertFlight(flight) // Запазва в локалната база
        try {
            val response = apiService.addFlight(flight) // Опитва да качи в Supabase
            if (response.isSuccessful) {
                flightDao.markFlightAsSynced(flight.id) // Маркира като "synced"
                Log.d("FlightRepository", "Flight ${flight.id} synced successfully.")
            } else {
                Log.e("FlightRepository", "Failed to sync flight ${flight.id}.")
            }
        } catch (e: Exception) {
            Log.e("FlightRepository", "Sync error: ${e.message}")
        }
    }

    // Синхронизация на локални записи към Supabase
    suspend fun syncFlights() {
        val unsyncedFlights = flightDao.getUnsyncedFlights()
        for (flight in unsyncedFlights) {
            try {
                val response = apiService.addFlight(flight)
                if (response.isSuccessful) {
                    flightDao.markFlightAsSynced(flight.id)
                    Log.d("FlightRepository", "Flight ${flight.id} synced.")
                } else {
                    Log.e("FlightRepository", "Sync failed for flight ${flight.id}.")
                }
            } catch (e: Exception) {
                Log.e("FlightRepository", "Sync error: ${e.message}")
            }
        }
    }

    // Зареждане на най-новите полети от Supabase
    suspend fun fetchLatestFlights() {
        try {
            val flights = apiService.getFlights()
            flightDao.updateLocalDatabase(flights) // Обновява локалната база
            Log.d("FlightRepository", "Fetched ${flights.size} flights from Supabase.")
        } catch (e: Exception) {
            Log.e("FlightRepository", "Error fetching flights: ${e.message}")
        }
    }

    // Извличане на всички полети от SQLite
    suspend fun getAllFlights(): List<FlightEntity> {
        return try {
            val localFlights = flightDao.getAllFlights()
            Log.d("FlightRepository", "Loaded ${localFlights.size} flights from SQLite.")
            localFlights
        } catch (e: Exception) {
            Log.e("FlightRepository", "Error fetching flights from SQLite: ${e.message}")
            emptyList()
        }
    }

    // Извличане на всички полети от Supabase
    suspend fun getAllFlightsFromSupabase(): List<FlightEntity> {
        return try {
            val flights = apiService.getFlights() // Извлича данните от Supabase API
            Log.d("FlightRepository", "Fetched ${flights.size} flights from Supabase.")
            flights
        } catch (e: Exception) {
            Log.e("FlightRepository", "Error fetching flights from Supabase: ${e.message}")
            emptyList()
        }
    }

    // Комбиниране на локалните и онлайн полетите
    suspend fun getAllFlightsCombined(): List<FlightEntity> {
        return try {
            val localFlights = getAllFlights()
            val remoteFlights = getAllFlightsFromSupabase()
            val allFlights = (localFlights + remoteFlights).distinctBy { it.id }
            Log.d("FlightRepository", "Total flights combined: ${allFlights.size} (Local + Supabase)")
            allFlights
        } catch (e: Exception) {
            Log.e("FlightRepository", "Error combining flights: ${e.message}")
            emptyList()
        }
    }
}
