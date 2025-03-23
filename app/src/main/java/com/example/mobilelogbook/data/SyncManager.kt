package com.example.mobilelogbook.data

import android.content.Context
import android.util.Log
import androidx.work.*
import com.example.mobilelogbook.repository.FlightRepository
import com.example.mobilelogbook.worker.SyncWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.util.concurrent.TimeUnit

class SyncManager(private val context: Context, private val repository: FlightRepository) {

    // Функция за стартиране на автоматична синхронизация на всеки 15 минути
    fun startSyncWorker() {
        Log.d("SyncManager", "Starting periodic sync worker...")

        val workRequest = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED) // Работи само при наличие на интернет
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "SyncWorker",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )

        Log.d("SyncManager", "Sync worker scheduled successfully.")
    }

    // Функция за ръчно извикване на синхронизация (при натискане на Refresh бутон)
    suspend fun syncNow() {
        withContext(Dispatchers.IO) {
            try {
                Log.d("SyncManager", "Manual sync started...")

                repository.syncFlights() // Качва локалните записи в Supabase
                repository.fetchLatestFlights() // Взема последните записи от Supabase

                Log.d("SyncManager", "Manual sync completed successfully.")
            } catch (e: HttpException) {
                Log.e("SyncManager", "Sync failed: ${e.message}")
                e.printStackTrace()
            } catch (e: Exception) {
                Log.e("SyncManager", "Unexpected error during sync: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}
