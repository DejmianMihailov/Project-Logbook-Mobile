package com.example.mobilelogbook.data

import com.example.mobilelogbook.dto.LoginRequest
import com.example.mobilelogbook.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("/api/mobile/flights")
    suspend fun addFlight(
        @Body flight: FlightLogMobileDto,
        @Header("Cookie") session: String
    ): Response<Unit>

    @POST("/api/mobile/flights/batch")
    suspend fun syncFlights(
        @Body flights: List<FlightLogMobileDto>,
        @Header("Cookie") session: String
    ): Response<Unit>

    @GET("/api/mobile/flights/user/{username}")
    suspend fun getFlightsForUser(@Path("username") username: String): List<FlightLogMobileDto>

    @GET("/api/aircrafts")
    suspend fun getAircrafts(): List<Aircraft>

    @GET("/api/flighttimes")
    suspend fun getFlightTimes(): List<FlightTime>

    @GET("/api/landings")
    suspend fun getLandings(): List<Landing>

    @GET("/api/takeoffs")
    suspend fun getTakeoffs(): List<Takeoff>

    @GET("/api/pilotfunctions")
    suspend fun getPilotFunctions(): List<PilotFunction>

    @GET("/api/aircrafts")
    suspend fun getAllAircrafts(): List<Aircraft>

    @POST("/api/mobile/login")
    suspend fun login(@Body request: LoginRequest): Response<AppUser>

    companion object {
        fun create(): ApiService {
            return RetrofitClient.retrofit.create(ApiService::class.java)
        }
    }
}
