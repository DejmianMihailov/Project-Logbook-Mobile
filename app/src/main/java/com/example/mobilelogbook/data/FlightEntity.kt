package com.example.mobilelogbook.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mobilelogbook.data.FlightLogMobileDto

@Entity(tableName = "flight_log")
data class FlightEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val departureTime: String,
    val arrivalTime: String,
    val flightDuration: Int,
    val pilotName: String,
    val username: String,
    val departureAirport: String,
    val arrivalAirport: String,

    val distance: Int? = null,
    val totalFlightTime: Long? = null,

    // Aircraft
    val aircraftId: Long? = null,
    val aircraft: String? = null,
    val aircraftMake: String? = null,
    val aircraftModel: String? = null,
    val aircraftRegistration: String? = null,

    // Flight time
    val flightTimeId: Long? = null,
    val multiPilotTime: Int? = null,
    val singlePilotTime: Int? = null,

    // Takeoff / Landing
    val takeoffId: Long? = null,
    val takeoffType: String? = null,

    val landingId: Long? = null,
    val landingType: String? = null,

    // Operational / Function / Remarks
    val operationalConditionId: Long? = null,
    val operationalCondition: String? = null,
    val nightFlightTime: Int? = null,

    val pilotFunctionId: Long? = null,
    val pilotFunction: String? = null,


    val remarksId: Long? = null,
    val remarksText: String? = null,

    val pilotRole: String? = null,

    val synced: Boolean = false
)