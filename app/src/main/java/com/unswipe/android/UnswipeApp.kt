package com.unswipe.android

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.unswipe.android.data.workers.UsageTrackingWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class UnswipeApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        // Initialize anything needed globally on app start
        // e.g., Logging library, analytics
        setupRecurringWork()
    }

    // Provide HiltWorkerFactory for WorkManager DI
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO) // Adjust logging level
            .build()

    private fun setupRecurringWork() {
        val workManager = WorkManager.getInstance(applicationContext)

        // Define constraints (optional)
        // val constraints = Constraints.Builder()
        //    .setRequiredNetworkType(NetworkType.CONNECTED) // If syncing requires network
        //    .build()

        val periodicWorkRequest = PeriodicWorkRequestBuilder<UsageTrackingWorker>(
            15, TimeUnit.MINUTES // Frequency - minimum is 15 mins
        )
        // .setConstraints(constraints) // Add constraints if defined
        .build()

        workManager.enqueueUniquePeriodicWork(
            UsageTrackingWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // Or REPLACE if definition changes
            periodicWorkRequest
        )
        android.util.Log.d("UnswipeApp", "UsageTrackingWorker enqueued.")
    }
} 