package com.unswipe.android.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unswipe.android.domain.model.DashboardData
import com.unswipe.android.domain.repository.UsageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import java.time.format.TextStyle // Example for date formatting
import java.util.Locale // Example for date formatting
import java.time.LocalDate // If your domain model uses LocalDate
import com.unswipe.android.ui.dashboard.DashboardUiState
import com.unswipe.android.ui.dashboard.DailyUsageSummary as UiDailyUsageSummary // Use an alias for clarity


// @HiltViewModel //
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
    // --- Helper function for mapping ---
    private fun mapDomainToUiState(domainData: DashboardData?): DashboardUiState {
        if (domainData == null) {
            return DashboardUiState.Loading
        }

        // Perform the mapping from domainData properties to DashboardUiState properties
        return DashboardUiState(
            isLoading = false, // Data has arrived
            timeUsedTodayFormatted = formatMillis(domainData.timeUsedTodayMillis),
            timeRemainingFormatted = formatMillis(domainData.timeRemainingMillis),
            usagePercentage = domainData.usagePercentage,
            currentStreak = domainData.currentStreak,
            swipesToday = domainData.swipesToday,
            unlocksToday = domainData.unlocksToday,

            // --- IMPLEMENTED MAPPING for weeklyProgress ---
            weeklyProgress = domainData.weeklyProgress.map { domainSummary ->
                // Map domain.model.DailyUsageSummary to ui.dashboard.DailyUsageSummary
                // *** YOU MUST IMPLEMENT THE LOGIC BASED ON YOUR DOMAIN/UI MODELS ***
                UiDailyUsageSummary(
                    // Example: Assuming domainSummary.date is LocalDate
                    dayLabel = domainSummary.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    // Example: Calculate percentage (handle division by zero)
                    usagePercentage = if (domainData.timeLimitMillis > 0) {
                        (domainSummary.totalUsageMillis.toFloat() / domainData.timeLimitMillis.toFloat()).coerceIn(0f, 1f)
                    } else {
                        0f
                    },
                    // Example: Check if it's today
                    isToday = domainSummary.date.isEqual(LocalDate.now())
                )
            },
            // ---------------------------------------------

            isPremium = domainData.isPremium,
            showUsagePermissionPrompt = !domainData.hasUsageStatsPermission,
            showAccessibilityPrompt = !domainData.isAccessibilityEnabled,
            error = null // No error if mapping succeeded
        )
    }

// Keep the rest of the ViewModel code (constructor, uiState flow definition, formatMillis)
// ... (rest of the code as you had it) ...

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

