package com.example.mobilelogbook.sync

import android.content.Context
import androidx.work.*
import com.example.mobilelogbook.worker.SyncWorker
import java.util.concurrent.TimeUnit

object SyncManager {

    fun startSyncWorker(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
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
