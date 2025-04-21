package com.unswipe.android

import android.app.Application
// import androidx.hilt.work.HiltWorkerFactory // Commented out import
// import androidx.work.Configuration // Commented out import
// import androidx.work.ExistingPeriodicWorkPolicy // Commented out import
// import androidx.work.PeriodicWorkRequestBuilder // Commented out import
// import androidx.work.WorkManager // Commented out import
// import com.unswipe.android.data.workers.UsageTrackingWorker // Commented out import
import dagger.hilt.android.HiltAndroidApp
// import java.util.concurrent.TimeUnit // Commented out import
// import javax.inject.Inject // Commented out import

@HiltAndroidApp
// class UnswipeApp : Application(), Configuration.Provider { // Commented out 'implements Configuration.Provider'
class UnswipeApp : Application() { // <-- Changed: Removed implements Configuration.Provider

    // @Inject // Commented out annotation
    // lateinit var workerFactory: HiltWorkerFactory // Commented out field

    override fun onCreate() {
        super.onCreate()
        // Initialize anything needed globally on app start
        // e.g., Logging library, analytics

        // setupRecurringWork() // Commented out call
    }

    // Provide HiltWorkerFactory for WorkManager DI
    /* // Commented out entire block
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.INFO) // Adjust logging level
            .build()
    */

    /* // Commented out entire function
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
    */
}