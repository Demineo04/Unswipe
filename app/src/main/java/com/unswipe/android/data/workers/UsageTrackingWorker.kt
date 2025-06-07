package com.unswipe.android.data.workers

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.unswipe.android.data.local.dao.UsageDao
import com.unswipe.android.data.model.DailyUsageSummary
import com.unswipe.android.domain.repository.SettingsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.concurrent.TimeUnit
import com.unswipe.android.data.model.EventType
import com.unswipe.android.data.model.UsageEvent as DbUsageEvent // Alias to avoid name clash

@HiltWorker
class UsageTrackingWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val usageStatsManager: UsageStatsManager,
    private val usageDao: UsageDao,
    private val settingsRepository: SettingsRepository,
    private val packageManager: PackageManager,
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "UsageTrackingWorker"
        private const val TAG = "UsageTrackingWorker"
        // How far back to query UsageStats each time. Adjust based on worker frequency.
        // Should slightly overlap with worker frequency to avoid gaps.
        private val QUERY_INTERVAL_MINUTES = TimeUnit.MINUTES.toMillis(16) // e.g., slightly more than 15min interval
    }

    override suspend fun doWork(): Result {
        Log.d(TAG, "Starting usage tracking work.")
        val currentTime = System.currentTimeMillis()
        // Query slightly further back to ensure no events are missed due to timing
        val startTime = currentTime - QUERY_INTERVAL_MINUTES

        try {
            // --- Log Basic Events (Optional - If not relying solely on AccessibilityService) ---
            // This part using queryEvents is less reliable for exact open/close times than Accessibility.
            // Keep it simple or remove if AccessibilityService handles open/close logging.
            // val usageEvents = usageStatsManager.queryEvents(startTime, currentTime)
            // processUsageEvents(usageEvents)

            // --- Calculate Daily Summary (Crucial) ---
            // This uses queryUsageStats which is generally accurate for aggregate time.
            calculateAndSaveDailySummary(currentTime)

            // --- TODO: Add logic to sync aggregated data to Firestore if needed ---
            // syncToFirestore(startOfDayMillis) // Implement this

            // --- TODO: Add logic to prune old data from Room ---
            // pruneOldData() // Implement this

            Log.d(TAG, "Usage tracking work finished.")
            return Result.success()

        } catch (e: SecurityException) {
            Log.e(TAG, "Permission potentially denied for UsageStatsManager", e)
            // TODO: Notify user or disable feature gracefully (e.g., update a flag in DataStore)
            return Result.failure()
        } catch (e: Exception) {
            Log.e(TAG, "Error during usage tracking work", e)
            return Result.retry() // Retry if it seems like a temporary error
        }
    }

    private suspend fun processUsageEvents(usageEvents: UsageEvents?) {
        // Basic event processing if needed (e.g., tracking config changes)
        // Be mindful this might duplicate events logged by AccessibilityService
        if (usageEvents == null) return
        val event = UsageEvents.Event()
        while (usageEvents.hasNextEvent()) {
             usageEvents.getNextEvent(event)
            // Log specific events if necessary, e.g.,
            // if (event.eventType == UsageEvents.Event.CONFIGURATION_CHANGE) { ... }
        }
    }

    private fun getStartOfDayMillis(timestamp: Long): Long {
         return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private suspend fun calculateAndSaveDailySummary(currentTime: Long) {
        val startOfDay = getStartOfDayMillis(currentTime)
        val endOfDay = startOfDay + TimeUnit.DAYS.toMillis(1) // Query until start of next day

        // --- Use queryUsageStats for accurate total screen time ---
        val stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startOfDay, endOfDay)
        val totalTime = stats?.sumOf { it.totalTimeInForeground } ?: 0L

        // --- Get swipe/unlock counts from our own DB events for that day ---
        // Counts are primarily updated by AccessibilityService (swipes) and UnlockReceiver (unlocks).
        // Fetch the *existing* summary for today to get the latest counts.
        val existingSummary = usageDao.getDailySummary(startOfDay)
        val swipes = existingSummary?.swipeCount ?: 0 // Swipes added by AccessibilityService
        val unlocks = existingSummary?.unlockCount ?: 0 // Unlocks added by Receiver

        val summary = DailyUsageSummary(
            dateMillis = startOfDay,
            totalScreenTimeMillis = totalTime,
            swipeCount = swipes,
            unlockCount = unlocks
        )
        usageDao.insertDailySummary(summary)
        Log.d(TAG, "Saved daily summary for $startOfDay: Time=${TimeUnit.MILLISECONDS.toMinutes(totalTime)}min, Swipes=$swipes, Unlocks=$unlocks")
    }

} 