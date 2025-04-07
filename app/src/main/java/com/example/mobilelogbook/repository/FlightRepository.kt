package com.example.mobilelogbook.repository

import android.util.Log
import com.example.mobilelogbook.data.ApiService
import com.example.mobilelogbook.data.FlightDao
import com.example.mobilelogbook.data.FlightEntity
import com.example.mobilelogbook.session.UserSession

class FlightRepository(
    private val flightDao: FlightDao,
    private val apiService: ApiService
) {
    suspend fun addFlight(flight: FlightEntity) {
        flightDao.insertFlight(flight)
        try {
            val response = apiService.addFlight(flight)
            if (response.isSuccessful) {
                flightDao.markFlightAsSynced(flight.id)
                Log.d("FlightRepository", "Flight ${flight.id} synced successfully.")
            } else {
                Log.e("FlightRepository", "Sync failed: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("FlightRepository", "Sync exception: ${e.message}")
        }
    }

    suspend fun getAllFlights(): List<FlightEntity> {
        val username = UserSession.username ?: return emptyList()
        return try {
            flightDao.getFlightsByUsername(username)
        } catch (e: Exception) {
            Log.e("FlightRepository", "Error fetching local flights: ${e.message}")
            emptyList()
        }
    }


    suspend fun getFlightsForCurrentUser(): List<FlightEntity> {
        val username = UserSession.username ?: return emptyList()
        return try {
            val result = apiService.getFlightsForUser(username)
            flightDao.updateLocalDatabase(result)
            result
        } catch (e: Exception) {
            Log.e("FlightRepository", "Error fetching Supabase flights: ${e.message}")
            emptyList()
        }
    }

    suspend fun syncFlights() {
        val unsynced = flightDao.getUnsyncedFlights()
        for (flight in unsynced) {
            try {
                val response = apiService.addFlight(flight)
                if (response.isSuccessful) {
                    flightDao.markFlightAsSynced(flight.id)
                }
            } catch (e: Exception) {
                Log.e("FlightRepository", "Sync error: ${e.message}")
            }
        }
    }

    suspend fun fetchLatestFlights() {
        try {
            val flights = apiService.getFlights()
            flightDao.updateLocalDatabase(flights)
        } catch (e: Exception) {
            Log.e("FlightRepository", "Fetch error: ${e.message}")
        }
    }
}
