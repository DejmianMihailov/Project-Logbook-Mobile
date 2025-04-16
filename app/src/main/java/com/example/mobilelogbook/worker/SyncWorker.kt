package com.example.mobilelogbook.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mobilelogbook.data.ApiService
import com.example.mobilelogbook.data.FlightDatabase
import com.example.mobilelogbook.repository.FlightRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        return@withContext try {
            val dao = FlightDatabase.getDatabase(applicationContext).flightDao()
            val api = ApiService.create()
            val repository = FlightRepository(dao, api)

            repository.syncFlights()
            repository.fetchLatestFlights()
            Log.d("SyncWorker", " Sync completed")
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", " Unexpected error: ${e.message}")
            Result.retry()
        }
    }
}
