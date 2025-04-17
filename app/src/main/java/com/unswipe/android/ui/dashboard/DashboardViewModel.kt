package com.unswipe.android.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unswipe.android.domain.model.DashboardData // Make sure this class exists!
import com.unswipe.android.domain.repository.UsageRepository
// Import other needed repos like SettingsRepository if needed directly
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel // <-- Ensure this is uncommented
class DashboardViewModel @Inject constructor(
    private val usageRepository: UsageRepository // Inject Usage Repo
    // Inject SettingsRepository if needed for direct access to streak/limit flows
    // private val settingsRepository: SettingsRepository
) : ViewModel() {

    // --- State Flow ---
    // Use stateIn to keep the flow active while the ViewModel is alive
    // and share the last emitted value with new collectors.
    val dashboardState: StateFlow<DashboardData?> = usageRepository.getDashboardDataFlow() // Make sure this function exists!
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L), // Keep active 5s after last subscriber
            initialValue = null // Start with null or a default loading state (Consider a dedicated Loading state)
        )

    // --- Event Handling (Placeholder) ---
    // Add functions here if the Dashboard needs to trigger actions,
    // e.g., refresh data, navigate, etc.
    // fun onRefresh() { /* Trigger repo data fetch? */ }
}