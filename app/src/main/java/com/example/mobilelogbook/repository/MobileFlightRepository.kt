package com.example.mobilelogbook.repository

import android.util.Log
import com.example.mobilelogbook.data.*
import com.example.mobilelogbook.dto.LoginRequest
import com.example.mobilelogbook.model.AppUser
import com.example.mobilelogbook.session.UserSession
import com.example.mobilelogbook.util.isOnline
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import retrofit2.HttpException

class MobileFlightRepository(
    private val apiService: ApiService,
    private val flightDao: FlightDao,
    private val userDao: UserDao
) {
    private var sessionCookie: String? = null

    suspend fun loginUser(request: LoginRequest): Boolean {
        val context = UserSession.getContext()
        val trimmedUsername = request.username.trim()
        val trimmedPassword = request.password.trim()

        if (context != null && isOnline(context)) {
            try {
                Log.d("MobileFlightRepo", "🔐 Trying online login for $trimmedUsername")
                val response = apiService.login(LoginRequest(trimmedUsername, trimmedPassword))

                val cookie = response.headers()["Set-Cookie"]
                if (cookie != null && cookie.contains("JSESSIONID")) {
                    sessionCookie = cookie.substringBefore(";")
                    Log.d("MobileFlightRepo", "🍪 Received session: $sessionCookie")
                }

                if (response.isSuccessful && response.body() != null) {
                    cacheUserLocally(AppUser(username = trimmedUsername, password = trimmedPassword))
                    Log.d("MobileFlightRepo", "✅ Online login success")
                    return true
                } else {
                    Log.e("MobileFlightRepo", "❌ Server login failed: ${response.code()}")
                }
            } catch (e: HttpException) {
                Log.e("MobileFlightRepo", "❌ HTTP error: ${e.code()} - ${e.message()}")
            } catch (e: Exception) {
                Log.e("MobileFlightRepo", "❌ Network error, fallback to local: ${e.message}")
            }
        } else {
            Log.w("MobileFlightRepo", "🌐 Offline - fallback to local login")
        }

        val localUser = userDao.getUserByUsername(trimmedUsername)
        val isValid = localUser?.password == trimmedPassword
        Log.d("MobileFlightRepo", "📦 Local login: ${if (isValid) "✅ Valid" else "❌ Invalid"}")
        return isValid
    }

    suspend fun cacheUserLocally(user: AppUser) {
        userDao.insertUser(user)
        Log.d("MobileFlightRepo", "📦 Cached user: ${user.username}")
    }

    suspend fun isValidLocalUser(username: String, password: String): Boolean {
        val localUser = userDao.getUserByUsername(username.trim())
        return localUser?.password == password.trim()
    }

    suspend fun loadFlightsForCurrentUser(): List<FlightEntity> {
        val username = UserSession.getUsername() ?: return emptyList()
        return withContext(Dispatchers.IO) {
            flightDao.getFlightsForUser(username)
        }
    }

    suspend fun saveFlightOffline(flight: FlightEntity) {
        withContext(Dispatchers.IO) {
            flightDao.insertFlight(flight)
            Log.d("MobileFlightRepo", "📦 Saved offline flight")
        }
    }

    suspend fun saveFlightAndSync(flight: FlightEntity) {
        saveFlightOffline(flight)

        val context = UserSession.getContext() ?: return
        if (!isOnline(context)) {
            Log.w("MobileFlightRepo", "🌐 Offline - cannot sync")
            return
        }

        try {
            val response = apiService.addFlight(flight.toDto(), sessionCookie ?: "")
            if (response.isSuccessful) {
                withContext(Dispatchers.IO) {
                    flightDao.updateFlightSyncStatus(flight.id, true)
                }
                Log.d("MobileFlightRepo", "✅ Synced flight with server")
            } else {
                Log.e("MobileFlightRepo", "❌ Sync failed: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("MobileFlightRepo", "❌ Exception during sync: ${e.message}")
        }
    }

    suspend fun syncFlightsIfOnline() {
        val context = UserSession.getContext() ?: return
        if (!isOnline(context)) {
            Log.w("MobileFlightRepo", "🌐 Offline - cannot sync flights")
            return
        }

        try {
            val unsynced = withContext(Dispatchers.IO) {
                flightDao.getUnsyncedFlights()
            }

            if (unsynced.isNotEmpty()) {
                val dtos = unsynced.map { it.toDto() }
                val response = apiService.syncFlights(dtos, sessionCookie ?: "")
                if (response.isSuccessful) {
                    withContext(Dispatchers.IO) {
                        unsynced.forEach { flightDao.updateFlightSyncStatus(it.id, true) }
                    }
                    Log.d("MobileFlightRepo", "✅ Synced ${unsynced.size} flights")
                } else {
                    Log.e("MobileFlightRepo", "❌ Sync failed: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            Log.e("MobileFlightRepo", "❌ Sync exception: ${e.message}")
        }
    }
}
