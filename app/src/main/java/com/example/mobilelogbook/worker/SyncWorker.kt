package com.example.mobilelogbook.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mobilelogbook.data.ApiService
import com.example.mobilelogbook.data.FlightDatabase
import com.example.mobilelogbook.repository.FlightRepository
import retrofit2.HttpException
import android.util.Log

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val flightDao = FlightDatabase.getDatabase(applicationContext).flightDao()
        val apiService = ApiService.create()
        val repository = FlightRepository(flightDao, apiService)

        return try {
            repository.syncFlights()
            Log.d("SyncWorker", "Sync completed successfully.")
            Result.success()
        } catch (e: HttpException) {
            Log.e("SyncWorker", "Sync failed: ${e.message}")
            Result.retry()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Unexpected error: ${e.message}")
            Result.retry()
        }
    }
}
