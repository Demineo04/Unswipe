package com.unswipe.android.domain.repository

import com.unswipe.android.domain.model.DailyUsageSummary
import com.unswipe.android.domain.model.TodayStats
import com.unswipe.android.data.model.UsageEvent
import com.unswipe.android.domain.model.DashboardData
import com.unswipe.android.domain.model.ContextualUsageEvent
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

    // Context-aware methods
    
    /**
     * Gets usage events within a time range for pattern analysis.
     */
    suspend fun getUsageEventsInRange(startTime: Long, endTime: Long): List<UsageEvent>
    
    /**
     * Logs a contextual usage event with context information.
     */
    suspend fun logContextualUsageEvent(event: ContextualUsageEvent)
    
    /**
     * Gets the number of app launch sessions today for a specific app.
     */
    suspend fun getSessionCountToday(packageName: String): Int
    
    /**
     * Gets work-time usage for a specific app.
     */
    suspend fun getWorkDayUsage(packageName: String): Long
    
    /**
     * Gets average work-time usage for a specific app over the last 30 days.
     */
    suspend fun getAverageWorkUsage(packageName: String): Long
    
    /**
     * Gets the number of times an app was opened during work hours today.
     */
    suspend fun getWorkOpenCount(packageName: String): Int
    
    /**
     * Checks if there are frequent work interruptions for an app.
     */
    suspend fun isFrequentWorkInterruption(packageName: String): Boolean

}