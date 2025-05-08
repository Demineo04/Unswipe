package com.unswipe.android.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.unswipe.android.data.model.DailyUsageSummary
import com.unswipe.android.data.model.UsageEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface UsageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsageEvent(event: UsageEvent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailySummary(summary: DailyUsageSummary)

    @Query("SELECT * FROM usage_events WHERE timestamp >= :startTime AND timestamp < :endTime ORDER BY timestamp DESC")
    fun getUsageEventsForPeriodFlow(startTime: Long, endTime: Long): Flow<List<UsageEvent>>

    // Get total time spent in specific apps for a given day
    // Note: Calculating exact foreground time from events is complex.
    // UsageStatsManager is better for accurate screen time.
    // This DAO might primarily store raw events like swipes/unlocks.
    // Screen time calculation might happen separately using UsageStatsManager.

    @Query("SELECT * FROM daily_summaries WHERE dateMillis >= :startDateMillis ORDER BY dateMillis DESC LIMIT :limit")
    fun getDailySummariesFlow(startDateMillis: Long, limit: Int = 7): Flow<List<DailyUsageSummary>>

     @Query("SELECT * FROM daily_summaries WHERE dateMillis = :dateMillis")
    suspend fun getDailySummary(dateMillis: Long): DailyUsageSummary?

    @Query("SELECT * FROM daily_summaries ORDER BY dateMillis DESC LIMIT 1")
    suspend fun getLatestDailySummary(): DailyUsageSummary?

    @Query("SELECT * FROM daily_summaries WHERE dateMillis >= :startDateMillis ORDER BY dateMillis ASC")
    suspend fun getSummariesSince(startDateMillis: Long): List<DailyUsageSummary>

    @Query("DELETE FROM usage_events WHERE timestamp < :olderThanTimestamp")
    suspend fun deleteOldUsageEvents(olderThanTimestamp: Long)

    @Query("DELETE FROM daily_summaries WHERE dateMillis < :olderThanTimestamp")
    suspend fun deleteOldSummaries(olderThanTimestamp: Long)

    @Query("SELECT COUNT(*) FROM usage_events WHERE timestamp >= :startTimeMillis AND eventType = :eventType")
    suspend fun getEventCountSince(startTimeMillis: Long, eventType: String): Int
} 