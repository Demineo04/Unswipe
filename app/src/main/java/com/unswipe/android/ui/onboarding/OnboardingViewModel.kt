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
    val sleepTime: LocalTime? = null
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun saveWakeupTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                val time = LocalTime.of(hour, minute)
                onboardingRepository.saveWakeupTime(time)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    wakeupTime = time,
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

    fun saveWorkSchedule(startHour: Int, startMinute: Int, endHour: Int, endMinute: Int) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                val startTime = LocalTime.of(startHour, startMinute)
                val endTime = LocalTime.of(endHour, endMinute)
                
                onboardingRepository.saveWorkSchedule(startTime, endTime)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    workStartTime = startTime,
                    workEndTime = endTime,
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

    fun saveSleepTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                val time = LocalTime.of(hour, minute)
                onboardingRepository.saveSleepTime(time)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    sleepTime = time,
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