// Location: app/src/main/java/com/unswipe/android/ui/dashboard/DashboardViewModel.kt

package com.unswipe.android.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// --- Ensure these imports point to your ACTUAL repository interfaces ---
import com.unswipe.android.domain.repository.AuthRepository
import com.unswipe.android.domain.repository.SettingsRepository
import com.unswipe.android.domain.repository.UsageRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.lang.Exception // Import Exception for catch block

// --- Data Class Definitions ADDED HERE ---
// Define the data structures needed by this ViewModel.
// Ideally, these might live in domain/model if shared, but defining them here
// resolves the immediate unresolved reference errors.

data class DailyUsageSummary(
    val dateMillis: Long,
    val totalScreenTimeMillis: Long
)

data class TodayStats(
    // --- Ensure these fields MATCH what your getTodaysUsageStats() function actually returns ---
    val swipes: Int,
    val unlocks: Int,
    val timeUsedMillis: Long
)

// Holds all the data needed by the DashboardScreen UI (references the classes above)
data class DashboardUiState(
    val timeUsedTodayMillis: Long,
    val timeLimitMillis: Long,
    val currentStreak: Int,
    val swipesToday: Int,
    val unlocksToday: Int,
    val weeklyProgress: List<DailyUsageSummary>, // List of daily summaries
    val isPremium: Boolean
)
// --- End Data Class Definitions ---


@HiltViewModel
class DashboardViewModel @Inject constructor(
    // --- Hilt injects your REAL repository implementations here ---
    private val usageRepository: UsageRepository,
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository
    // Add other dependencies as needed
) : ViewModel() {

    // Private MutableStateFlow - Nullable to represent loading state
    private val _dashboardState = MutableStateFlow<DashboardUiState?>(null)
    // Public immutable StateFlow for the UI to observe
    val dashboardState: StateFlow<DashboardUiState?> = _dashboardState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _dashboardState.value = null // Set to null to indicate loading start

            try {
                // --- Fetch data ---
                // CRITICAL: Ensure your REAL repository interfaces DECLARE these functions!
                val todaysStats: TodayStats = usageRepository.getTodaysUsageStats()
                val limit: Long = settingsRepository.getTimeLimitMillis()
                val streak: Int = usageRepository.getCurrentStreak()
                val weeklyData: List<DailyUsageSummary> = usageRepository.getWeeklyUsageSummary()
                val premiumStatus: Boolean = authRepository.isUserPremium()
                // --- End Data Fetching ---


                // --- Construct the state object ---
                // Access fields defined in the TodayStats data class ABOVE
                _dashboardState.value = DashboardUiState(
                    timeUsedTodayMillis = todaysStats.timeUsedMillis, // Uses field from TodayStats
                    timeLimitMillis = limit,
                    currentStreak = streak,
                    swipesToday = todaysStats.swipes,       // Uses field from TodayStats
                    unlocksToday = todaysStats.unlocks,     // Uses field from TodayStats
                    weeklyProgress = weeklyData,            // Uses DailyUsageSummary
                    isPremium = premiumStatus
                )

            } catch (e: Exception) {
                // TODO: Implement proper error handling
                // Log the error (e.g., using Timber or Log.e)
                _dashboardState.value = DashboardUiState(
                    timeUsedTodayMillis = -1L, timeLimitMillis = -1L, currentStreak = -1,
                    swipesToday = -1, unlocksToday = -1,
                    weeklyProgress = emptyList(), // Now knows emptyList() is List<DailyUsageSummary>
                    isPremium = false
                )
            }
        }
    }

    // Optional: Add functions for UI events if needed (e.g., refresh)
    fun onRefresh() {
        loadDashboardData()
    }
}