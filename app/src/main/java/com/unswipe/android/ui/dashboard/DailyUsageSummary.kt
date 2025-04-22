package com.unswipe.android.ui.dashboard

// Example Data class for UI representation
data class DailyUsageSummary(
    val dayLabel: String,       // e.g., "M", "T", "W" or "Mon"
    val usagePercentage: Float, // e.g., 0.0f to 1.0f representing usage vs limit
    val isToday: Boolean = false // Example: To highlight today's bar/item
)