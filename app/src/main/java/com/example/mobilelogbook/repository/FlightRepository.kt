package com.example.mobilelogbook.repository

import android.util.Log
import com.example.mobilelogbook.data.ApiService
import com.example.mobilelogbook.data.FlightDao
import com.example.mobilelogbook.data.FlightEntity
import com.example.mobilelogbook.session.UserSession
import com.google.gson.Gson

class FlightRepository(
    private val flightDao: FlightDao,
    private val apiService: ApiService
) {

    suspend fun addFlight(flight: FlightEntity) {
        flightDao.insertFlight(flight)

        try {
            val payload = flight.toMap()
                .filterValues { it != null }
                .mapValues { it.value!! } // convert to Map<String, Any>

            Log.d("FlightRepository", "📤 Sending to Supabase: ${Gson().toJson(payload)}")
            val response = apiService.addFlight(flight.toMap())

            if (response.isSuccessful) {
                flightDao.markFlightAsSynced(flight.id ?: 0L)
                Log.d("FlightRepository", "✅ Flight synced to Supabase")
            } else {
                Log.e("FlightRepository", "❌ Supabase sync failed: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("FlightRepository", "🔥 Sync exception: ${e.message}")
        }
    }

    suspend fun getAllFlights(): List<FlightEntity> {
        val username = UserSession.getUsername() ?: return emptyList()
        return flightDao.getFlightsByUsername(username)
    }

    suspend fun getFlightsForCurrentUser(): List<FlightEntity> {
        val username = UserSession.getUsername() ?: return emptyList()
        return try {
            val flights = apiService.getFlightsForUser(mapOf("username" to "eq.$username"))
            flightDao.updateLocalDatabase(flights)
            flights
        } catch (e: Exception) {
            Log.e("FlightRepository", "Error fetching Supabase flights: ${e.message}")
            emptyList()
        }
    }

    suspend fun syncFlights() {
        val unsynced = flightDao.getUnsyncedFlights()
        for (flight in unsynced) {
            try {
                val payload = flight.toMap()
                    .filterValues { it != null }
                    .mapValues { it.value!! }

                Log.d("FlightRepository", "📤 Syncing flight: ${Gson().toJson(payload)}")
                val response = apiService.addFlight(payload)

                if (response.isSuccessful) {
                    flightDao.markFlightAsSynced(flight.id ?: 0L)
                    Log.d("FlightRepository", "✅ Synced flight ${flight.id}")
                } else {
                    Log.e("FlightRepository", "❌ Sync failed: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("FlightRepository", "🔥 Exception during sync: ${e.message}")
            }
        }
    }

    suspend fun fetchLatestFlights() {
        try {
            val response = apiService.getFlights()
            flightDao.updateLocalDatabase(response)
            Log.d("FlightRepository", "✅ Fetched ${response.size} flights from Supabase")
        } catch (e: Exception) {
            Log.e("FlightRepository", "❌ Error fetching flights from Supabase: ${e.message}")
        }
    }
}
