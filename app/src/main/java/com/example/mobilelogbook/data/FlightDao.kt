package com.example.mobilelogbook.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface FlightDao {

    // Връща само полетите на текущия потребител
    @Query("SELECT * FROM flight_log WHERE username = :username")
    suspend fun getFlightsByUsername(username: String): List<FlightEntity>

    // Запис на едно летателно събитие
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlight(flight: FlightEntity)

    // Маркира полетите, които не са синхронизирани
    @Query("SELECT * FROM flight_log WHERE status = 'pending'")
    suspend fun getUnsyncedFlights(): List<FlightEntity>

    // Обновява статуса на полетите като "synced"
    @Query("UPDATE flight_log SET status = 'synced' WHERE id = :flightId")
    suspend fun markFlightAsSynced(flightId: Long)

    // Обновява локалната база със списък от полети
    @Transaction
    suspend fun updateLocalDatabase(flights: List<FlightEntity>) {
        for (flight in flights) {
            insertFlight(flight)
        }
    }
}
