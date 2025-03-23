package com.example.mobilelogbook.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.mobilelogbook.data.FlightDatabase
import com.example.mobilelogbook.data.ApiService
import com.example.mobilelogbook.repository.FlightRepository
import kotlinx.coroutines.runBlocking

class SyncWorker(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val flightDao = FlightDatabase.getDatabase(applicationContext).flightDao()
        val apiService = ApiService.create()
        val repository = FlightRepository(flightDao, apiService)

        runBlocking {
            repository.syncFlights()
        }

        return Result.success()
    }
}
