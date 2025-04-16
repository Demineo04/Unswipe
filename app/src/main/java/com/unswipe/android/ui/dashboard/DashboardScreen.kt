// Location: app/src/main/java/com/unswipe/android/ui/dashboard/DashboardViewModel.kt

package com.unswipe.android.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unswipe.android.domain.repository.AuthRepository // Assuming you have this
import com.unswipe.android.domain.repository.SettingsRepository // Assuming you have this
import com.unswipe.android.domain.repository.UsageRepository // Assuming you have this
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// --- State Definition (Should match what DashboardScreen expects) ---

// Represents usage summary for one day (for the weekly chart)
data class DailyUsageSummary(
    val dateMillis: Long,
    val totalScreenTimeMillis: Long
)

// Holds all the data needed by the DashboardScreen UI
data class DashboardUiState(
    // Removed isLoading flag, we'll use nullability of the state itself
    val timeUsedTodayMillis: Long,
    val timeLimitMillis: Long,
    val currentStreak: Int,
    val swipesToday: Int,
    val unlocksToday: Int,
    val weeklyProgress: List<DailyUsageSummary>, // List of daily summaries
    val isPremium: Boolean
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    // --- Inject your actual repositories/use cases ---
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
                // --- Fetch data concurrently or sequentially ---
                // Example: Fetching different pieces of data.
                // Replace these with actual calls to your repositories.
                // Use Flow's combine operator if your repo functions return Flows.
                // If they are suspend functions, you might use async/await for concurrency.

                // --- Placeholder Data Fetching Logic ---
                // TODO: Replace with real repository calls
                val todaysStats = usageRepository.getTodaysUsageStats() // Assume returns object with swipes, unlocks, timeUsed
                val limit = settingsRepository.getTimeLimitMillis() // Assume returns Long
                val streak = usageRepository.getCurrentStreak() // Assume returns Int
                val weeklyData = usageRepository.getWeeklyUsageSummary() // Assume returns List<DailyUsageSummary>
                val premiumStatus = authRepository.isUserPremium() // Assume returns Boolean
                // --- End Placeholder Logic ---


                // --- Construct the state object ---
                _dashboardState.value = DashboardUiState(
                    timeUsedTodayMillis = todaysStats.timeUsedMillis, // Adjust based on your actual stats object
                    timeLimitMillis = limit,
                    currentStreak = streak,
                    swipesToday = todaysStats.swipes, // Adjust based on your actual stats object
                    unlocksToday = todaysStats.unlocks, // Adjust based on your actual stats object
                    weeklyProgress = weeklyData,
                    isPremium = premiumStatus
                )

            } catch (e: Exception) {
                // TODO: Handle errors appropriately (e.g., show an error message)
                // For now, just stop loading maybe? Or set a specific error state.
                _dashboardState.value = DashboardUiState( // Example error state (could be improved)
                    timeUsedTodayMillis = 0, timeLimitMillis = 0, currentStreak = 0,
                    swipesToday = 0, unlocksToday = 0, weeklyProgress = emptyList(),
                    isPremium = false
                ) // Or keep it null and let the UI show a generic error
                // Log.e("DashboardViewModel", "Error loading dashboard data", e)
            }
        }
    }

    // Optional: Add functions for UI events if needed (e.g., refresh)
    fun onRefresh() {
        loadDashboardData()
    }
}

// --- Dummy Repository Interfaces (Replace with your actual ones) ---
// These are just for the example above to compile. Delete them and use your real ones.

interface UsageRepository {
    suspend fun getTodaysUsageStats(): TodayStats // Define TodayStats data class
    suspend fun getCurrentStreak(): Int
    suspend fun getWeeklyUsageSummary(): List<DailyUsageSummary>
}
data class TodayStats(val swipes: Int, val unlocks: Int, val timeUsedMillis: Long) // Example structure

interface SettingsRepository {
    suspend fun getTimeLimitMillis(): Long
}

/* // Already defined AuthRepository in AuthViewModel example
interface AuthRepository {
    suspend fun isUserPremium(): Boolean
    // ... other auth methods
}
*/