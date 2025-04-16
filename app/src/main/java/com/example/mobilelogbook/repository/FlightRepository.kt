package com.example.mobilelogbook.repository

import android.util.Log
import com.example.mobilelogbook.data.ApiService
import com.example.mobilelogbook.data.FlightDao
import com.example.mobilelogbook.data.FlightEntity
import com.example.mobilelogbook.session.UserSession
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FlightRepository(
    private val flightDao: FlightDao,
    private val apiService: ApiService
) {

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    suspend fun addFlight(flight: FlightEntity) {
        flightDao.insertFlight(flight)
        try {
            val payload = mapOf<String, Any>(
                "departure_time" to LocalDateTime.parse(flight.departureTime).format(formatter),
                "arrival_time" to LocalDateTime.parse(flight.arrivalTime).format(formatter),
                "flight_duration" to (flight.flightDuration ?: 0),
                "pilot_name" to flight.pilotName,
                "departure_airport" to flight.departureAirport,
                "arrival_airport" to flight.arrivalAirport,
                "aircraft" to (flight.aircraft ?: ""),
                "username" to flight.username,
                "status" to flight.status,
                "flight_time_id" to flight.flightTimeId,
                "landing_id" to flight.landingId,
                "pilot_function_id" to flight.pilotFunctionId
            )

            Log.d("FlightRepository", "📤 Syncing flight: $payload")
            val response = apiService.addFlight(payload)
            if (response.isSuccessful) {
                flightDao.markFlightAsSynced(flight.id)
                Log.d("FlightRepository", "✅ Synced flight ${flight.id}")
            } else {
                Log.e("FlightRepository", "❌ Sync failed: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("FlightRepository", "🔥 Exception during sync: ${e.message}")
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
            addFlight(flight)
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
