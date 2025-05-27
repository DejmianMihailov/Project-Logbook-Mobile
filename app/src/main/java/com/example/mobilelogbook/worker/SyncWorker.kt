package com.example.mobilelogbook.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.mobilelogbook.data.ApiService
import com.example.mobilelogbook.data.FlightDatabase
import com.example.mobilelogbook.repository.FlightRepository

class SyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val database = FlightDatabase.getDatabase(applicationContext)
            val dao = database.flightDao()
            val userDao = database.userDao() // ✅ Добавено
            val api = ApiService.create()

            val repository = FlightRepository(dao, userDao, api) // ✅ Поправено

            Log.d("SyncWorker", "🔄 Starting synchronization...")

            val syncResult = repository.syncFlightsToServer()
            repository.fetchFlightsFromServer()

            if (syncResult) {
                Log.d("SyncWorker", "✅ Auto-sync completed successfully")
                Result.success()
            } else {
                Log.w("SyncWorker", "⚠️ Partial sync: some flights may not have been synced. Retrying...")
                Result.retry()
            }

        } catch (e: Exception) {
            Log.e("SyncWorker", "❌ Auto-sync failed: ${e.message}")
            Result.retry()
        }
    }
}
