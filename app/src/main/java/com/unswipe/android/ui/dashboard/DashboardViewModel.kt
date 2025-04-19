package com.unswipe.android.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unswipe.android.domain.model.DashboardData
import com.unswipe.android.domain.repository.UsageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.util.concurrent.TimeUnit // Needed for formatting example
import javax.inject.Inject

// Make sure the UI model for daily summary exists and is imported
// import com.unswipe.android.ui.dashboard.DailyUsageSummary as UiDailyUsageSummary

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val usageRepository: UsageRepository
) : ViewModel() {

    // Renamed the flow exposed to the UI
    val uiState: StateFlow<DashboardUiState> = usageRepository.getDashboardDataFlow()
        .map { domainData ->
            // Map the Domain model (DashboardData) to the UI State model (DashboardUiState)
            mapDomainToUiState(domainData)
        }
        .catch { e ->
            // Handle errors from the upstream flow (e.g., repository)
            // You could emit a specific error state here
            emit(DashboardUiState(isLoading = false, error = e.localizedMessage ?: "An error occurred"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = DashboardUiState.Loading // Use the UI State's loading state
        )

    // --- Helper function for mapping ---
    private fun mapDomainToUiState(domainData: DashboardData?): DashboardUiState {
        if (domainData == null) {
            // Handle the case where the initial value or an error resulted in null
            // Could return Loading state or an Initial/Empty state
            return DashboardUiState.Loading // Or DashboardUiState(isLoading=false, error="Data unavailable")
        }

        // Perform the mapping from domainData properties to DashboardUiState properties
        return DashboardUiState(
            isLoading = false, // Data has arrived
            timeUsedTodayFormatted = formatMillis(domainData.timeUsedTodayMillis), // Example formatting
            timeRemainingFormatted = formatMillis(domainData.timeRemainingMillis), // Use calculated property
            usagePercentage = domainData.usagePercentage, // Use calculated property
            currentStreak = domainData.currentStreak,
            swipesToday = domainData.swipesToday,
            unlocksToday = domainData.unlocksToday,
            weeklyProgress = domainData.weeklyProgress.map { domainSummary ->
                // Map domain.model.DailyUsageSummary to ui.dashboard.DailyUsageSummary
                com.unswipe.android.ui.dashboard.DailyUsageSummary(
                    // Assign properties based on what ui.dashboard.DailyUsageSummary needs
                    // Example: dayLabel = domainSummary.date.dayOfWeek.name.take(1),
                    // Example: usagePercentage = (domainSummary.totalUsageMillis.toFloat() / domainData.timeLimitMillis.toFloat()).coerceIn(0f, 1f)
                )
            },
            isPremium = domainData.isPremium,
            showUsagePermissionPrompt = !domainData.hasUsageStatsPermission, // Example logic
            showAccessibilityPrompt = !domainData.isAccessibilityEnabled, // Example logic
            error = null // No error if mapping succeeded
        )
    }

    // Example formatting function (place appropriately, maybe in a utils file)
    private fun formatMillis(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        return String.format("%dh %02dm", hours, minutes)
        // Or use Duration class for more complex formatting
    }

    // --- Event Handling (Placeholder) ---
    // fun onRefresh() { /* Trigger repo data fetch? */ }
}