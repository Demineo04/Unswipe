package com.unswipe.android.ui.dashboard

// UI representation of daily summary data
data class DailyUsageSummary(
    // Define fields needed specifically for the UI
    // Example: These might differ from the Domain model
    val dayLabel: String, // e.g., "M", "T"
    val usagePercentage: Float, // 0.0f to 1.0f
    val isToday: Boolean = false
)