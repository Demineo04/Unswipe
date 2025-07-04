package com.unswipe.android.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unswipe.android.domain.repository.OnboardingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

data class OnboardingUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val wakeupTime: LocalTime? = null,
    val workStartTime: LocalTime? = null,
    val workEndTime: LocalTime? = null,
    val sleepTime: LocalTime? = null,
    val isOnboardingComplete: Boolean = false
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        loadSavedData()
    }

    private fun loadSavedData() {
        viewModelScope.launch {
            try {
                // Load any previously saved onboarding data
                // This would typically come from the repository
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load saved data: ${e.localizedMessage}"
                )
            }
        }
    }

    // Method names that match what the screens are calling
    fun setWakeupTime(hour: Int, minute: Int) {
        val time = LocalTime.of(hour, minute)
        _uiState.value = _uiState.value.copy(wakeupTime = time)
        saveWakeupTime(hour, minute)
    }

    fun setWorkTime(startHour: Int, startMinute: Int, endHour: Int, endMinute: Int) {
        val startTime = LocalTime.of(startHour, startMinute)
        val endTime = LocalTime.of(endHour, endMinute)
        _uiState.value = _uiState.value.copy(
            workStartTime = startTime,
            workEndTime = endTime
        )
        saveWorkSchedule(startHour, startMinute, endHour, endMinute)
    }

    fun setSleepTime(hour: Int, minute: Int) {
        val time = LocalTime.of(hour, minute)
        _uiState.value = _uiState.value.copy(sleepTime = time)
        saveSleepTime(hour, minute)
    }

    private fun saveWakeupTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                val time = LocalTime.of(hour, minute)
                onboardingRepository.saveWakeupTime(time)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to save wakeup time: ${e.localizedMessage}"
                )
            }
        }
    }

    private fun saveWorkSchedule(startHour: Int, startMinute: Int, endHour: Int, endMinute: Int) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                val startTime = LocalTime.of(startHour, startMinute)
                val endTime = LocalTime.of(endHour, endMinute)
                
                onboardingRepository.saveWorkSchedule(startTime, endTime)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to save work schedule: ${e.localizedMessage}"
                )
            }
        }
    }

    private fun saveSleepTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                val time = LocalTime.of(hour, minute)
                onboardingRepository.saveSleepTime(time)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to save sleep time: ${e.localizedMessage}"
                )
            }
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                onboardingRepository.markOnboardingComplete()
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isOnboardingComplete = true,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to complete onboarding: ${e.localizedMessage}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}