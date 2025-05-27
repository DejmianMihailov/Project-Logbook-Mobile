package com.example.mobilelogbook.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface FlightDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlight(flight: FlightEntity)

    @Query("SELECT * FROM flight_log WHERE username = :username")
    suspend fun getFlightsForUser(username: String): List<FlightEntity>

    @Query("SELECT * FROM flight_log WHERE synced = 0")
    suspend fun getUnsyncedFlights(): List<FlightEntity>

    @Query("UPDATE flight_log SET synced = :synced WHERE id = :flightId")
    suspend fun updateFlightSyncStatus(flightId: Long, synced: Boolean)

    @Query("SELECT * FROM flight_log")
    suspend fun getAllFlights(): List<FlightEntity>
    @Transaction
    suspend fun updateLocalDatabase(flights: List<FlightEntity>) {
        for (flight in flights) {
            insertFlight(flight)
        }

    }
}
