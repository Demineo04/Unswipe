package com.unswipe.android.domain.usecase

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.unswipe.android.data.workers.UsageTrackingWorker.Companion.LAST_STREAK_UPDATE_KEY // Access key
import com.unswipe.android.domain.repository.SettingsRepository
import com.unswipe.android.domain.repository.UsageRepository
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

// NOTE: The core logic for updating the streak is now integrated within UsageTrackingWorker
// for better cohesion, as it relies on daily summary calculation.
// This UseCase might become redundant or could be refactored to trigger the worker
// or perform other related actions if needed.
// Keeping it here as a placeholder for potential future complex logic separation.

class UpdateStreakUseCase @Inject constructor(
    private val usageRepository: UsageRepository,
    private val settingsRepository: SettingsRepository,
    private val dataStore: DataStore<Preferences>
) {
    // This operator might not be called directly if worker handles the logic.
    // It demonstrates the core logic if separated.
    suspend operator fun invoke() {
        Log.d("UpdateStreakUseCase", "Manual invocation (potentially redundant if worker is active).")
        val currentTime = System.currentTimeMillis()
        val settings = settingsRepository.getUserSettings().first()
        val todayMillis = getStartOfDayMillis(currentTime)

        val lastStreakUpdateMillis = dataStore.data.first()[LAST_STREAK_UPDATE_KEY] ?: 0L
        val lastUpdateDayMillis = getStartOfDayMillis(lastStreakUpdateMillis)

        if (lastUpdateDayMillis < todayMillis) {
            Log.d("UpdateStreakUseCase", "Performing daily streak check manually.")
            val yesterdayMillis = todayMillis - TimeUnit.DAYS.toMillis(1)
            val yesterdaySummary = usageRepository.getDailySummary(yesterdayMillis) // UsageRepo needs this method

            if (yesterdaySummary != null) {
                if (yesterdaySummary.totalScreenTimeMillis <= settings.dailyUsageLimitMillis) {
                    settingsRepository.incrementStreak()
                    Log.i("UpdateStreakUseCase", "Streak incremented.")
                } else {
                    settingsRepository.resetStreak()
                    Log.i("UpdateStreakUseCase", "Streak reset.")
                }
            } else {
                 if (settings.currentStreak > 0) {
                     settingsRepository.resetStreak()
                     Log.i("UpdateStreakUseCase", "Streak reset due to missing yesterday summary.")
                 }
            }
            dataStore.edit { prefs -> prefs[LAST_STREAK_UPDATE_KEY] = currentTime }
        } else {
             // Proactive check for today (same as in worker)
             val todaySummary = usageRepository.getDailySummary(todayMillis)
             if (todaySummary != null &&
                 todaySummary.totalScreenTimeMillis > settings.dailyUsageLimitMillis &&
                 settings.currentStreak > 0) {
                     settingsRepository.resetStreak()
                     Log.i("UpdateStreakUseCase", "Streak reset proactively.")
                     dataStore.edit { prefs -> prefs[LAST_STREAK_UPDATE_KEY] = currentTime }
             }
            Log.d("UpdateStreakUseCase", "Streak update check skipped (already processed today or limit ok).")
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
} 