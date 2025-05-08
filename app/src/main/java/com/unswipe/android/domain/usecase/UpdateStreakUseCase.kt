// Location: app/src/main/java/com/unswipe/android/domain/usecase/UpdateStreakUseCase.kt

package com.unswipe.android.domain.usecase

import android.util.Log // Added for logging
import com.unswipe.android.domain.repository.SettingsRepository
import com.unswipe.android.domain.repository.UsageRepository
import kotlinx.coroutines.flow.firstOrNull
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class UpdateStreakUseCase @Inject constructor(
    private val usageRepository: UsageRepository, // Use the interface
    private val settingsRepository: SettingsRepository // Use the interface
) {
    companion object {
        private const val TAG = "UpdateStreakUseCase"
    }

    // Note: This logic needs refinement. It currently checks today's usage against today's limit.
    // A better approach updates the streak based on *yesterday's* usage *after* midnight.
    suspend operator fun invoke() {
        Log.d(TAG, "Executing UpdateStreakUseCase")
        try {
            // Get current settings (limit, current streak)
            val settings = settingsRepository.getUserSettings().firstOrNull()
            if (settings == null) {
                Log.w(TAG, "Could not get user settings.")
                return
            }

            // Get today's usage summary (ensure method name matches UsageRepository)
            val todaySummary = usageRepository.getTodaysSummary() // <-- FIX: Assumes this is the correct method name
            if (todaySummary == null) {
                Log.w(TAG, "Could not get today's usage summary.")
                // Decide how to handle - maybe skip update? Or assume under limit if no data?
                // For simplicity, let's skip if no summary found for today yet.
                return
            }

            Log.d(TAG, "Today's usage: ${todaySummary.totalUsageMillis}ms, Limit: ${settings.dailyUsageLimitMillis}ms, Current Streak: ${settings.currentStreak}")

            // --- Basic Logic (Needs Improvement for Accuracy) ---
            // This simple check resets if today's usage exceeds the limit.
            // It doesn't handle incrementing correctly based on previous days.
            if (todaySummary.totalUsageMillis > settings.dailyUsageLimitMillis) {
                if (settings.currentStreak > 0) {
                    Log.i(TAG, "Usage exceeded limit. Resetting streak.")
                    settingsRepository.resetStreak()
                } else {
                    Log.d(TAG, "Usage exceeded limit, but streak was already 0.")
                }
            } else {
                // TODO: Implement proper increment logic.
                // This requires knowing if the day has rolled over since the last check
                // and checking *yesterday's* final usage. This simple check here isn't enough.
                Log.d(TAG, "Usage is within limit. Increment logic needs implementation.")
                // settingsRepository.incrementStreak() // DON'T CALL THIS UNCONDITIONALLY
            }
            // --- End Basic Logic ---

        } catch (e: Exception) {
            Log.e(TAG, "Error updating streak", e)
        }
    }

    // Example of more robust logic structure (requires storing last update time)
    // suspend fun invokeRobust(currentTime: Long) {
    //     val lastUpdateTime = // Get from settingsRepository/DataStore
    //     val startOfToday = // Calculate start of today
    //     val startOfYesterday = // Calculate start of yesterday
    //
    //     if (currentTime >= startOfToday && lastUpdateTime < startOfToday) { // Day rolled over
    //         val yesterdaySummary = usageRepository.getYesterdaysSummary(startOfYesterday) // Need this method
    //         val settings = settingsRepository.getUserSettings().first()
    //         if (yesterdaySummary != null && yesterdaySummary.totalScreenTimeMillis <= settings.dailyUsageLimitMillis) {
    //             settingsRepository.incrementStreak()
    //         } else {
    //              settingsRepository.resetStreak()
    //         }
    //         // Store currentTime as lastUpdateTime in settingsRepository/DataStore
    //     }
    // }
}