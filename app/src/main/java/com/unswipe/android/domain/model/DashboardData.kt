package com.unswipe.android.domain.model

import com.unswipe.android.data.model.DailyUsageSummary
import java.util.concurrent.TimeUnit

data class DashboardData(
    val timeUsedTodayMillis: Long,
    val timeLimitMillis: Long,
    val currentStreak: Int,
    val swipesToday: Int,
    val unlocksToday: Int,
    val weeklyProgress: List<DailyUsageSummary>, // Last 7 days, oldest first
    val isPremium: Boolean,
    val hasUsageStatsPermission: Boolean, // To prompt user if needed
    val isAccessibilityEnabled: Boolean // To prompt user if needed
) {
     val timeRemainingMillis: Long
        get() = (timeLimitMillis - timeUsedTodayMillis).coerceAtLeast(0)

     val usagePercentage: Float
        get() = if (timeLimitMillis > 0) {
            (timeUsedTodayMillis.toFloat() / timeLimitMillis.toFloat()).coerceIn(0f, 1f)
        } else {
            0f // Avoid division by zero
        }

    companion object {
        // Define a default/loading state
        val Loading = DashboardData(
            timeUsedTodayMillis = 0L,
            timeLimitMillis = TimeUnit.HOURS.toMillis(3), // Default limit
            currentStreak = 0,
            swipesToday = 0,
            unlocksToday = 0,
            weeklyProgress = emptyList(),
            isPremium = false,
            hasUsageStatsPermission = false, // Assume no permission initially
            isAccessibilityEnabled = false // Assume not enabled initially
        )
    }
} 