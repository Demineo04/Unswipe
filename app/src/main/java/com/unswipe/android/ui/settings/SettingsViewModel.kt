package com.unswipe.android.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unswipe.android.domain.repository.AuthRepository
import com.unswipe.android.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val isLoading: Boolean = false,
    val dailyLimitMillis: Long = 2 * 60 * 60 * 1000L, // Default 2 hours
    val blockedApps: Set<String> = emptySet(),
    val isPremium: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val error: String? = null
) {
    companion object {
        val Loading = SettingsUiState(isLoading = true)
    }
}

// @HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState.Loading)
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            try {
                combine(
                    settingsRepository.getDailyLimitFlow(),
                    flowOf(emptySet<String>()), // settingsRepository.getBlockedApps() // TODO: Implement
                    flowOf(false) // settingsRepository.isPremium() // TODO: Implement
                ) { dailyLimit, blockedApps, isPremium ->
                    SettingsUiState(
                        isLoading = false,
                        dailyLimitMillis = dailyLimit,
                        blockedApps = blockedApps,
                        isPremium = isPremium,
                        notificationsEnabled = true, // TODO: Add to repository
                        error = null
                    )
                }.collect { newState ->
                    _uiState.value = newState
                }
            } catch (e: Exception) {
                _uiState.value = SettingsUiState(
                    isLoading = false,
                    error = e.localizedMessage ?: "Failed to load settings"
                )
            }
        }
    }

    fun updateDailyLimit(newLimitMillis: Long) {
        viewModelScope.launch {
            try {
                // settingsRepository.setDailyLimitMillis(newLimitMillis) // TODO: Implement
                _uiState.value = _uiState.value.copy(
                    dailyLimitMillis = newLimitMillis,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to update daily limit: ${e.localizedMessage}"
                )
            }
        }
    }

    fun updateBlockedApps(newBlockedApps: Set<String>) {
        viewModelScope.launch {
            try {
                // settingsRepository.setBlockedApps(newBlockedApps) // TODO: Implement
                _uiState.value = _uiState.value.copy(
                    blockedApps = newBlockedApps,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to update blocked apps: ${e.localizedMessage}"
                )
            }
        }
    }

    fun toggleAppBlocked(packageName: String) {
        val currentBlocked = _uiState.value.blockedApps
        val newBlocked = if (currentBlocked.contains(packageName)) {
            currentBlocked - packageName
        } else {
            currentBlocked + packageName
        }
        updateBlockedApps(newBlocked)
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            try {
                // Reset to default values
                // settingsRepository.setDailyLimitMillis(2 * 60 * 60 * 1000L) // 2 hours // TODO: Implement
                // settingsRepository.setBlockedApps(...) // TODO: Implement
                
                _uiState.value = _uiState.value.copy(
                    dailyLimitMillis = 2 * 60 * 60 * 1000L,
                    blockedApps = setOf(
                        "com.zhiliaoapp.musically",
                        "com.instagram.android",
                        "com.google.android.youtube"
                    ),
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to reset settings: ${e.localizedMessage}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun deleteAccount() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                // Delete user account from Firebase Auth
                authRepository.signOut() // For now, just sign out
                // TODO: Implement actual account deletion in AuthRepository
                
                // Clear all local settings
                settingsRepository.clearAllData()
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to delete account: ${e.localizedMessage}"
                )
            }
        }
    }
}