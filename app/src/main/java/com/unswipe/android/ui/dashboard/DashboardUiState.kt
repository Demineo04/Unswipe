package com.unswipe.android.ui.dashboard

// Import the UI version of DailyUsageSummary after you create it below
// import com.unswipe.android.ui.dashboard.DailyUsageSummary

import java.util.concurrent.TimeUnit

data class DashboardUiState(
    val isLoading: Boolean = true,
    val timeUsedTodayFormatted: String = "",
    val timeRemainingFormatted: String = "",
    val usagePercentage: Float = 0f,
    val currentStreak: Int = 0,
    val swipesToday: Int = 0,
    val unlocksToday: Int = 0,
    val weeklyProgress: List<DailyUsageSummary> = emptyList(), // Use UI model
    val isPremium: Boolean = false,
    val showUsagePermissionPrompt: Boolean = false,
    val showAccessibilityPrompt: Boolean = false,
    val error: String? = null
) {
    companion object {
        // Provides a default loading state
        val Loading = DashboardUiState(isLoading = true)
    }
}