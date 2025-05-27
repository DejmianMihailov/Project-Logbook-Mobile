package com.example.mobilelogbook.repository

import android.util.Log
import com.example.mobilelogbook.data.*
import com.example.mobilelogbook.dto.LoginRequest
import com.example.mobilelogbook.model.*
import com.example.mobilelogbook.session.UserSession
import retrofit2.HttpException


class FlightRepository(
    private val flightDao: FlightDao,
    private val userDao: UserDao,
    private val apiService: ApiService
) {

    suspend fun saveAndSyncFlight(flight: FlightEntity) {
        flightDao.insertFlight(flight)
        val session = UserSession.getSessionId() ?: return
        try {
            val response = apiService.addFlight(flight.toDto(), session)
            if (response.isSuccessful) {
                flightDao.updateFlightSyncStatus(flight.id, true)
                Log.d("FlightRepository", "✅ Flight synced")
            } else {
                Log.e("FlightRepository", "❌ Flight sync failed: ${response.code()} - ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("FlightRepository", "❌ Error during sync: ${e.message}")
        }
    }

    suspend fun getFlightsForUserOffline(): List<FlightEntity> {
        val username = UserSession.getUsername() ?: return emptyList()
        return flightDao.getFlightsForUser(username)
    }

    suspend fun syncFlightsToServer(): Boolean {
        val session = UserSession.getSessionId() ?: return false
        return try {
            val unsyncedFlights = flightDao.getUnsyncedFlights()
            if (unsyncedFlights.isNotEmpty()) {
                val dtos = unsyncedFlights.map { it.toDto() }
                val response = apiService.syncFlights(dtos, session)
                if (response.isSuccessful) {
                    unsyncedFlights.forEach { flightDao.updateFlightSyncStatus(it.id, true) }
                    Log.d("FlightRepository", "✅ Synced ${unsyncedFlights.size} flights")
                    true
                } else {
                    Log.e("FlightRepository", "❌ Sync failed: ${response.code()} ${response.message()}")
                    false
                }
            } else {
                true
            }
        } catch (e: Exception) {
            Log.e("FlightRepository", "❌ Exception during sync: ${e.message}")
            false
        }
    }

    suspend fun fetchFlightsFromServer() {
        val username = UserSession.getUsername() ?: return
        try {
            Log.d("FlightRepository", "📡 Fetching flights for user: $username")
            val remoteFlights = apiService.getFlightsForUser(username)
            val flightEntities = remoteFlights.map { it.toEntity(synced = true) }
            flightDao.updateLocalDatabase(flightEntities)
            Log.d("FlightRepository", "✅ Updated local DB with ${flightEntities.size} flights")
        } catch (e: Exception) {
            Log.e("FlightRepository", "❌ Error fetching flights: ${e.message}")
        }
    }

    suspend fun getAircraftList(): List<Aircraft> {
        return try {
            apiService.getAllAircrafts()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun fetchAircrafts(): List<Aircraft> {
        return try {
            apiService.getAircrafts()
        } catch (e: Exception) {
            Log.e("FlightRepository", "❌ Error fetching aircrafts: ${e.message}")
            emptyList()
        }
    }

    suspend fun fetchFlightTimes(): List<FlightTime> {
        return try {
            apiService.getFlightTimes()
        } catch (e: Exception) {
            Log.e("FlightRepository", "❌ Error fetching flight times: ${e.message}")
            emptyList()
        }
    }

    suspend fun fetchLandings(): List<Landing> {
        return try {
            apiService.getLandings()
        } catch (e: Exception) {
            Log.e("FlightRepository", "❌ Error fetching landings: ${e.message}")
            emptyList()
        }
    }

    suspend fun fetchTakeoffs(): List<Takeoff> {
        return try {
            apiService.getTakeoffs()
        } catch (e: Exception) {
            Log.e("FlightRepository", "❌ Error fetching takeoffs: ${e.message}")
            emptyList()
        }
    }

    suspend fun fetchPilotFunctions(): List<PilotFunction> {
        return try {
            apiService.getPilotFunctions()
        } catch (e: Exception) {
            Log.e("FlightRepository", "❌ Error fetching pilot functions: ${e.message}")
            emptyList()
        }
    }

    suspend fun loginUser(request: LoginRequest): Boolean {
        return try {
            Log.d("FlightRepository", "🔐 Attempt login with: ${request.username}")
            val response = apiService.login(request)

            Log.d("FlightRepository", "📡 Login response code: ${response.code()}")
            if (response.isSuccessful && response.body() != null) {
                val session = response.headers()["Set-Cookie"]
                if (session != null) {
                    UserSession.setSessionId(session)
                    Log.d("FlightRepository", "✅ Session ID saved")
                }
            } else {
                Log.e("FlightRepository", "❌ Login failed: ${response.errorBody()?.string()}")
            }

            response.isSuccessful && response.body() != null
        } catch (e: HttpException) {
            Log.e("FlightRepository", "❌ HTTP error during login: ${e.code()} - ${e.message()}")
            false
        } catch (e: Exception) {
            Log.e("FlightRepository", "❌ Exception during login: ${e.message}")
            false
        }
    }

    suspend fun cacheUserLocally(user: AppUser) {
        userDao.insertUser(user)
        Log.d("FlightRepository", "✅ Cached user locally: ${user.username}")
    }

    suspend fun isValidLocalUser(username: String, password: String): Boolean {
        val localUser = userDao.getUserByUsername(username)
        return localUser?.password == password
    }
}
