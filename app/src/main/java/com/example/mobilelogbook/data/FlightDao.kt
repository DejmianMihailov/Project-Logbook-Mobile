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

    @Query("SELECT * FROM flight_log WHERE status = 'pending'")
    suspend fun getUnsyncedFlights(): List<FlightEntity>

    @Query("UPDATE flight_log SET status = 'synced' WHERE id = :flightId")
    suspend fun markFlightAsSynced(flightId: Long)

    @Query("SELECT * FROM flight_log")
    suspend fun getAllFlights(): List<FlightEntity>

    @Transaction
    suspend fun updateLocalDatabase(flights: List<FlightEntity>) {
        flights.forEach { insertFlight(it) }

    }
}
