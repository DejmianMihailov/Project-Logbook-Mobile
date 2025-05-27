package com.example.mobilelogbook.data

import com.example.mobilelogbook.data.FlightEntity
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface MobileApiService {

    @POST("/api/flights/manual")
    suspend fun addFlight(@Body flight: FlightEntity): Response<Void>

    @GET("/api/flights/user")
    suspend fun getFlightsForUser(@Query("username") username: String): List<FlightEntity>

    companion object {
        private const val BASE_URL = "http://192.168.1.7:8081" // ⚡ IP адресът на твоя компютър

        fun create(): MobileApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MobileApiService::class.java)
        }
    }
}
