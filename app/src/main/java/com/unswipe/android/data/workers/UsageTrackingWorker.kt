package com.unswipe.android.data.workers

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.unswipe.android.data.local.dao.UsageDao
import com.unswipe.android.data.model.DailyUsageSummary
import com.unswipe.android.data.repository.SettingsRepositoryImpl // Access Pref keys directly if needed
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
    private val preferencesDataStore: DataStore<Preferences>, // Inject DataStore
    private val packageManager: PackageManager,
    // private val updateStreakUseCase: UpdateStreakUseCase // Inject use case if complex logic needed
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "UsageTrackingWorker"
        private const val TAG = "UsageTrackingWorker"
        // How far back to query UsageStats each time. Adjust based on worker frequency.
        // Should slightly overlap with worker frequency to avoid gaps.
        private val QUERY_INTERVAL_MINUTES = TimeUnit.MINUTES.toMillis(16) // e.g., slightly more than 15min interval
        // DataStore key for last streak update timestamp
        val LAST_STREAK_UPDATE_KEY = longPreferencesKey("last_streak_update_timestamp")
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

            // --- Update Streak ---
            updateStreak(currentTime)

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

     // Handles streak logic based on daily summaries
     private suspend fun updateStreak(currentTime: Long) {
         val settings = settingsRepository.getUserSettings().first() // Get current settings
         val todayMillis = getStartOfDayMillis(currentTime)

         // Get timestamp of the last streak update
         val lastStreakUpdateMillis = preferencesDataStore.data.first()[LAST_STREAK_UPDATE_KEY] ?: 0L
         val lastUpdateDayMillis = getStartOfDayMillis(lastStreakUpdateMillis)

         // Only perform daily check if the last update was before today
         if (lastUpdateDayMillis < todayMillis) {
             Log.d(TAG, "Performing daily streak check for transition to $todayMillis")
             val yesterdayMillis = todayMillis - TimeUnit.DAYS.toMillis(1)
             val yesterdaySummary = usageDao.getDailySummary(yesterdayMillis)

             if (yesterdaySummary != null) {
                 // Check yesterday's usage against the limit *that was active yesterday*
                 // For simplicity, we use the current limit here. Could store historical limits if needed.
                 if (yesterdaySummary.totalScreenTimeMillis <= settings.dailyUsageLimitMillis) {
                     settingsRepository.incrementStreak()
                     Log.i(TAG, "Streak incremented based on yesterday's usage.")
                 } else {
                     settingsRepository.resetStreak()
                     Log.i(TAG, "Streak reset based on yesterday's usage.")
                 }
             } else {
                // No summary for yesterday? Could happen on first day or if data missing.
                // Maybe start streak at 1 if it's the first ever recorded day? Or reset?
                // Resetting is safer if history is expected.
                if (settings.currentStreak > 0) {
                    settingsRepository.resetStreak()
                    Log.i(TAG, "Streak reset due to missing yesterday summary.")
                }
             }

             // Record that the update for *today* has been done
             preferencesDataStore.edit { prefs ->
                 prefs[LAST_STREAK_UPDATE_KEY] = currentTime
             }
         } else {
              // Still the same day, but check if usage *just* exceeded the limit
              // This provides a faster reset than waiting for the next day's check
              val todaySummary = usageDao.getDailySummary(todayMillis)
              if (todaySummary != null &&
                  todaySummary.totalScreenTimeMillis > settings.dailyUsageLimitMillis &&
                  settings.currentStreak > 0) { // Only reset if streak was positive
                      settingsRepository.resetStreak()
                      Log.i(TAG, "Streak reset proactively as today's limit exceeded.")
                      // Update timestamp to prevent multiple resets today
                      preferencesDataStore.edit { prefs ->
                          prefs[LAST_STREAK_UPDATE_KEY] = currentTime
                      }
              }
             Log.d(TAG, "Streak update check skipped, already processed for today or limit not yet exceeded.")
         }
     }
} 