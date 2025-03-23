package com.example.mobilelogbook.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiService {
    @Headers(
        "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImFvYmN5aWZvb3VyZHhxZWtuZXVjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAzMjgwNzUsImV4cCI6MjA1NTkwNDA3NX0.0q2WRoKr9MQS5ByjV3fAh1s-McE4moG76FjjddYW7bg",
        "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImFvYmN5aWZvb3VyZHhxZWtuZXVjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAzMjgwNzUsImV4cCI6MjA1NTkwNDA3NX0.0q2WRoKr9MQS5ByjV3fAh1s-McE4moG76FjjddYW7bg"
    )
    @POST("/rest/v1/flight_log")
    suspend fun addFlight(@Body flight: FlightEntity): retrofit2.Response<Unit>

    @Headers(
        "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImFvYmN5aWZvb3VyZHhxZWtuZXVjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAzMjgwNzUsImV4cCI6MjA1NTkwNDA3NX0.0q2WRoKr9MQS5ByjV3fAh1s-McE4moG76FjjddYW7bg",
        "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImFvYmN5aWZvb3VyZHhxZWtuZXVjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAzMjgwNzUsImV4cCI6MjA1NTkwNDA3NX0.0q2WRoKr9MQS5ByjV3fAh1s-McE4moG76FjjddYW7bg"
    )
    @GET("/rest/v1/flight_log?select=*")
    suspend fun getFlights(): List<FlightEntity>

    companion object {
        private const val BASE_URL = "https://aobcyifoourdxqekneuc.supabase.co"

        fun create(): ApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}
