package com.unswipe.android.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unswipe.android.domain.model.DashboardData
import com.unswipe.android.domain.repository.UsageRepository
import com.unswipe.android.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import java.time.format.TextStyle // Example for date formatting
import java.util.Locale // Example for date formatting
import java.time.LocalDate // If your domain model uses LocalDate
import com.unswipe.android.ui.dashboard.DashboardUiState
import com.unswipe.android.ui.dashboard.DailyUsageSummary as UiDailyUsageSummary // Use an alias for clarity
import com.unswipe.android.domain.model.DailyUsageSummary as DomainDailyUsageSummary


@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val usageRepository: UsageRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    
    // User name flow
    val userName: StateFlow<String> = authRepository.getCurrentUserFlow()
        .map { user ->
            user?.displayName?.takeIf { it.isNotBlank() } 
                ?: user?.email?.substringBefore("@")?.capitalize() 
                ?: "User"
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = "User"
        )

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
            swipesToday = domainData.swipesToday,
            unlocksToday = domainData.unlocksToday,

            // --- IMPLEMENTED MAPPING for weeklyProgress ---
            weeklyProgress = if (domainData.weeklyProgress.isNotEmpty()) {
                domainData.weeklyProgress.map { domainSummary ->
                    // Map domain.model.DailyUsageSummary to ui.dashboard.DailyUsageSummary
                    UiDailyUsageSummary(
                        // Use correct DomainDailyUsageSummary properties
                        dayLabel = domainSummary.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        usagePercentage = if (domainData.timeLimitMillis > 0) {
                            (domainSummary.totalUsageMillis.toFloat() / domainData.timeLimitMillis.toFloat()).coerceIn(0f, 2f) // Allow up to 200% for visualization
                        } else {
                            0f
                        },
                        isToday = domainSummary.date.isEqual(LocalDate.now())
                    )
                }
            } else {
                // Generate sample data for demonstration when no real data is available
                generateSampleWeeklyData(domainData.timeLimitMillis)
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
        // Basic placeholder implementation
        val minutes = java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(millis)
        return "${minutes}m"
    }

    // TODO: Add permission/accessibility check logic
    private fun checkPermissions(): Pair<Boolean, Boolean> {
        // Placeholder
        return Pair(first = false, second = false) // (hasUsageStatsPermission, isAccessibilityEnabled)
    }

    // Generate sample weekly data for demonstration
    private fun generateSampleWeeklyData(timeLimitMillis: Long): List<UiDailyUsageSummary> {
        val today = LocalDate.now()
        val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        
        return (0..6).map { dayOffset ->
            val date = today.minusDays(6 - dayOffset.toLong())
            val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            
            // Generate realistic usage percentages (some days over limit, some under)
            val usagePercentage = when (dayOffset) {
                0 -> 0.3f  // Monday - light usage
                1 -> 0.7f  // Tuesday - moderate usage
                2 -> 1.2f  // Wednesday - over limit
                3 -> 0.9f  // Thursday - approaching limit
                4 -> 0.5f  // Friday - moderate usage
                5 -> 1.4f  // Saturday - way over limit
                6 -> 0.8f  // Sunday (today) - current usage
                else -> 0.5f
            }
            
            UiDailyUsageSummary(
                dayLabel = dayOfWeek,
                usagePercentage = usagePercentage,
                isToday = date.isEqual(today)
            )
        }
    }

    // --- Event Handling (Placeholder) ---
    // fun onRefresh() { /* Trigger repo data fetch? */ }
}

