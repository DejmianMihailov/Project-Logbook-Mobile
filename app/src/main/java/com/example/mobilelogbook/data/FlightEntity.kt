package com.example.mobilelogbook.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "flight_log")
data class FlightEntity(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @SerializedName("departure_time")
    @ColumnInfo(name = "departure_time")
    val departureTime: String, // ISO формат

    @SerializedName("arrival_time")
    @ColumnInfo(name = "arrival_time")
    val arrivalTime: String, // ISO формат

    @SerializedName("flight_duration")
    @ColumnInfo(name = "flight_duration")
    val flightDuration: Int? = null,

    @SerializedName("pilot_name")
    @ColumnInfo(name = "pilot_name")
    val pilotName: String,

    @SerializedName("departure_airport")
    @ColumnInfo(name = "departure_airport")
    val departureAirport: String,

    @SerializedName("arrival_airport")
    @ColumnInfo(name = "arrival_airport")
    val arrivalAirport: String,

    @SerializedName("aircraft")
    @ColumnInfo(name = "aircraft")
    val aircraft: String? = null,

    @SerializedName("username")
    @ColumnInfo(name = "username")
    val username: String,

    @SerializedName("status")
    @ColumnInfo(name = "status")
    val status: String = "pending",

    @SerializedName("flight_time_id")
    @ColumnInfo(name = "flight_time_id")
    val flightTimeId: Long,

    @SerializedName("landing_id")
    @ColumnInfo(name = "landing_id")
    val landingId: Long,

    @SerializedName("pilot_function_id")
    @ColumnInfo(name = "pilot_function_id")
    val pilotFunctionId: Long
)
