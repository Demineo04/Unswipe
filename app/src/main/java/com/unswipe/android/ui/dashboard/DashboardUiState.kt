package com.unswipe.android.ui.dashboard

import java.util.concurrent.TimeUnit

// Define the UI model for daily summary if it doesn't exist yet
// Example: app/src/main/java/com/unswipe/android/ui/dashboard/DailyUsageSummary.kt
// data class DailyUsageSummary(val dayLabel: String, val usagePercentage: Float, ...)

data class DashboardUiState(
    val isLoading: Boolean = true,
    val timeUsedTodayFormatted: String = "", // Example: Format for UI
    val timeRemainingFormatted: String = "", // Example: Format for UI
    val usagePercentage: Float = 0f,
    val currentStreak: Int = 0,
    val swipesToday: Int = 0,
    val unlocksToday: Int = 0,
    val weeklyProgress: List<DailyUsageSummary> = emptyList(), // <-- Use the UI model package here
    val isPremium: Boolean = false,
    val showUsagePermissionPrompt: Boolean = false, // Logic based on domain state
    val showAccessibilityPrompt: Boolean = false, // Logic based on domain state
    val error: String? = null // Optional: To display errors
) {
    companion object {
        val Loading = DashboardUiState(isLoading = true)
    }
}