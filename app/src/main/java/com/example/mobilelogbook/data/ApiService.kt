package com.example.mobilelogbook.data

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import retrofit2.Response

interface ApiService {

    @GET("flight_log")
    suspend fun getFlights(): List<FlightEntity>

    @GET("flight_log")
    suspend fun getFlightsForUser(
        @QueryMap filters: Map<String, String>
    ): List<FlightEntity>

    @POST("flight_log")
    suspend fun addFlight(
        @Body flight: Map<String, @JvmSuppressWildcards Any>
    ): Response<Void>

    @PATCH("flight_log")
    suspend fun updateFlight(
        @Body flight: Map<String, Any>,
        @Query("id") id: String
    ): Response<Void>

    companion object {
        private const val BASE_URL = "https://aobcyifoourdxqekneuc.supabase.co/rest/v1/"
        private const val API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImFvYmN5aWZvb3VyZHhxZWtuZXVjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAzMjgwNzUsImV4cCI6MjA1NTkwNDA3NX0.0q2WRoKr9MQS5ByjV3fAh1s-McE4moG76FjjddYW7bg"

        fun create(): ApiService {
            val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("apikey", API_KEY)
                        .addHeader("Authorization", "Bearer $API_KEY")
                        .addHeader("Content-Type", "application/json")
                        .build()
                    chain.proceed(request)
                }
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}


//        private const val BASE_URL = "https://aobcyifoourdxqekneuc.supabase.co/rest/v1/"
//        private const val API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImFvYmN5aWZvb3VyZHhxZWtuZXVjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDAzMjgwNzUsImV4cCI6MjA1NTkwNDA3NX0.0q2WRoKr9MQS5ByjV3fAh1s-McE4moG76FjjddYW7bg"