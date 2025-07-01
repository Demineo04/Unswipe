package com.unswipe.android.domain.repository

import com.unswipe.android.domain.model.DailyUsageSummary
import com.unswipe.android.domain.model.TodayStats       // Adjust import if needed
import com.unswipe.android.data.model.UsageEvent // Or a domain model equivalent
import com.unswipe.android.domain.model.DashboardData // Assuming you created this in domain/model
import kotlinx.coroutines.flow.Flow

interface UsageRepository {

    /**
     * Logs a raw usage event (e.g., swipe, unlock, app open - if tracking opens this way).
     */
    suspend fun logUsageEvent(event: UsageEvent) // Consider using a Domain model for event

    /**
     * Provides a Flow of the data needed for the main dashboard screen.
     * This likely combines data from UsageStatsManager, local DB summaries, and SettingsRepository.
     */
    fun getDashboardDataFlow(): Flow<DashboardData>

    /**
     * Gets the daily summary for today.
     * Should ideally calculate based on UsageStats and locally logged events (swipes/unlocks).
     */
    suspend fun getTodaysSummary(): DailyUsageSummary? // Or a domain model equivalent

    /**
     * (Optional) Syncs relevant aggregated usage data (like summaries) to the cloud (e.g., Firestore).
     * Triggered periodically or manually.
     */
    suspend fun syncUsageToCloud()

    /**
     * Deletes old usage events and/or summaries from the local database to manage storage.
     */
    suspend fun clearOldData(olderThanTimestamp: Long)

    suspend fun getTodaysUsageStats(): TodayStats

    suspend fun getWeeklyUsageSummary(): List<DailyUsageSummary>

    /**
     * Gets today's usage time for a specific app package.
     */
    suspend fun getAppUsageToday(packageName: String): Long

    /**
     * Records an app launch attempt for analytics and learning.
     */
    suspend fun recordAppLaunchAttempt(
        packageName: String,
        wasBlocked: Boolean,
        usageAtTime: Long
    )

}