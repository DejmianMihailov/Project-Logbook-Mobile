package com.example.mobilelogbook.sync

import android.content.Context
import androidx.work.*
import com.example.mobilelogbook.worker.SyncWorker
import java.util.concurrent.TimeUnit

object SyncManager {

    fun startPeriodicSync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED) // Само с интернет
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "SyncFlights",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}
