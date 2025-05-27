package com.example.mobilelogbook.model

data class FlightTime(
    val id: Long,
    val singlePilotTime: Int,
    val multiPilotTime: Int,
    val totalFlightTime: Int
)
