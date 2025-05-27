package com.example.mobilelogbook.data


import com.example.mobilelogbook.dto.*
import com.example.mobilelogbook.data.FlightEntity

data class FlightLogMobileDto(
    val departureTime: String,
    val arrivalTime: String,
    val flightDuration: Int,
    val pilotName: String,
    val username: String,
    val departureAirport: String,
    val arrivalAirport: String,
    val distance: Int? = null,
    val totalFlightTime: Long? = null,

    val aircraft: AircraftDto? = null,
    val flightTimeId: Long? = null,

    val takeoff: TakeoffDto? = null,
    val landing: LandingDto? = null,

    val operationalCondition: OperationalConditionDto? = null,
    val pilotFunction: PilotFunctionDto? = null,
    val remarks: RemarksDto? = null,

    val pilotRole: String? = null
)

fun FlightLogMobileDto.toEntity(synced: Boolean = false): FlightEntity = FlightEntity(
    departureTime = departureTime,
    arrivalTime = arrivalTime,
    flightDuration = flightDuration,
    pilotName = pilotName,
    username = username,
    departureAirport = departureAirport,
    arrivalAirport = arrivalAirport,
    distance = distance,
    totalFlightTime = totalFlightTime,

    aircraftId = aircraft?.id,
    aircraftMake = aircraft?.make,
    aircraftModel = aircraft?.model,
    aircraftRegistration = aircraft?.registration,

    flightTimeId = flightTimeId,

    takeoffId = takeoff?.id,
    takeoffType = takeoff?.type,

    landingId = landing?.id,
    landingType = landing?.type,

    operationalConditionId = operationalCondition?.id,
    operationalCondition = operationalCondition?.nightFlightTime?.toString(),

    pilotFunctionId = pilotFunction?.id,
    pilotFunction = pilotFunction?.name,

    remarksId = remarks?.id,
    remarksText = remarks?.text,

    pilotRole = pilotRole,
    synced = synced
)


fun FlightEntity.toDto(): FlightLogMobileDto = FlightLogMobileDto(
    departureTime = departureTime,
    arrivalTime = arrivalTime,
    flightDuration = flightDuration,
    pilotName = pilotName,
    username = username,
    departureAirport = departureAirport,
    arrivalAirport = arrivalAirport,
    distance = distance,
    totalFlightTime = totalFlightTime,

    aircraft = AircraftDto(aircraftId, aircraftRegistration, aircraftMake, aircraftModel),
    flightTimeId = flightTimeId,

    takeoff = TakeoffDto(takeoffId, takeoffType),
    landing = LandingDto(landingId, landingType),

    operationalCondition = OperationalConditionDto(
        id = operationalConditionId,
        nightFlightTime = nightFlightTime
    ),
    pilotFunction = PilotFunctionDto(pilotFunctionId, pilotFunction),
    remarks = RemarksDto(remarksId, remarksText),

    pilotRole = pilotRole
)

