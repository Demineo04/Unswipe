package com.unswipe.android

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory // <-- UNCOMMENTED
import androidx.work.Configuration // <-- UNCOMMENTED
import androidx.work.ExistingPeriodicWorkPolicy // <-- UNCOMMENTED
import androidx.work.PeriodicWorkRequestBuilder // <-- UNCOMMENTED
import androidx.work.WorkManager // <-- UNCOMMENTED
import com.unswipe.android.data.workers.UsageTrackingWorker // <-- UNCOMMENTED (Ensure this path is correct)
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit // <-- UNCOMMENTED
import javax.inject.Inject // <-- UNCOMMENTED

@HiltAndroidApp
// Only ONE class declaration, implementing the interface
class UnswipeApp : Application(), Configuration.Provider { // <-- CORRECTED Class line

    // @Inject // Inject annotation // TEMPORARILY DISABLED
    // lateinit var workerFactory: HiltWorkerFactory // Field for the factory // TEMPORARILY DISABLED

    override fun onCreate() {
        super.onCreate()
        android.util.Log.d("UnswipeApp", "Application onCreate() called")
        // Initialize anything needed globally on app start
        // e.g., Logging library, analytics
        try {
            setupRecurringWork() // Call the setup function
            android.util.Log.d("UnswipeApp", "setupRecurringWork() completed successfully")
        } catch (e: Exception) {
            android.util.Log.e("UnswipeApp", "Error in onCreate: ${e.message}", e)
        }
    }

    // Provide HiltWorkerFactory for WorkManager DI
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            // .setWorkerFactory(workerFactory) // TEMPORARILY DISABLED
            .setMinimumLoggingLevel(android.util.Log.INFO) // Adjust logging level
            .build()

    // The function to set up the worker
    private fun setupRecurringWork() {
        // Check if WorkManager is initialized correctly (optional, but good practice)
        try {
            val workManager = WorkManager.getInstance(applicationContext)

            val periodicWorkRequest = PeriodicWorkRequestBuilder<UsageTrackingWorker>(
                15, TimeUnit.MINUTES // Frequency - minimum is 15 mins
            ).build()

            workManager.enqueueUniquePeriodicWork(
                UsageTrackingWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP, // Or REPLACE if definition changes
                periodicWorkRequest
            )
            android.util.Log.d("UnswipeApp", "UsageTrackingWorker enqueued.")

        } catch (e: Exception) {
            // Log error if WorkManager setup fails (e.g., during testing or edge cases)
            android.util.Log.e("UnswipeApp", "Error setting up WorkManager: ${e.message}", e)
        }
    }
}