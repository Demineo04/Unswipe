package com.unswipe.android.ui.dashboard

// UI representation of daily usage summary for the dashboard
data class DailyUsageSummary(
    val dayLabel: String, // e.g., "Mon", "Tue", etc.
    val usagePercentage: Float, // 0.0 to 1.0 representing percentage of daily limit used
    val isToday: Boolean = false // Whether this represents today's usage
)