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
                Log.d("FlightRepository", "✅ Flight synced to Supabase: ${response.code()}")
            } else {
                val error = response.errorBody()?.string()
                Log.e("FlightRepository", "❌ Supabase error (${response.code()}): $error")
            }
        } catch (e: Exception) {
            Log.e("FlightRepository", "Error syncing to Supabase: ${e.message}")
        }
    }

    suspend fun updateFlight(flight: FlightEntity) {
        try {
            val response = apiService.updateFlight(flight, "eq.${flight.id}")
            if (response.isSuccessful) {
                Log.d("FlightRepository", "Flight ${flight.id} updated successfully in Supabase")
            } else {
                Log.e("FlightRepository", "Failed to update flight: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("FlightRepository", "Update error: ${e.message}")
        }
    }

    suspend fun getAllFlights(): List<FlightEntity> {
        val username = UserSession.getUsername() ?: return emptyList()
        return try {
            val localFlights = flightDao.getFlightsByUsername(username)
            Log.d("FlightRepository", "Fetched ${localFlights.size} local flights for $username")
            localFlights
        } catch (e: Exception) {
            Log.e("FlightRepository", "Error fetching local flights: ${e.message}")
            emptyList()
        }
    }

    suspend fun getFlightsForCurrentUser(): List<FlightEntity> {
        val username = UserSession.getUsername() ?: return emptyList()
        return try {
            val response = apiService.getFlightsForUser(mapOf("username" to "eq.$username"))
            Log.d("FlightRepository", "Fetched ${response.size} flights from Supabase for $username")
            flightDao.updateLocalDatabase(response)
            response
        } catch (e: Exception) {
            Log.e("FlightRepository", "Error fetching flights from Supabase: ${e.message}")
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
                    Log.d("FlightRepository", "Synced flight ${flight.id}")
                } else {
                    Log.e("FlightRepository", "Sync failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("FlightRepository", "Exception during sync: ${e.message}")
            }
        }
    }

    suspend fun fetchLatestFlights() {
        try {
            val response = apiService.getFlights()
            flightDao.updateLocalDatabase(response)
            Log.d("FlightRepository", "Fetched ${response.size} flights from Supabase")
        } catch (e: Exception) {
            Log.e("FlightRepository", "Fetch error: ${e.message}")
        }
    }
}
