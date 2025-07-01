package com.unswipe.android.ui.onboarding

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.unswipe.android.ui.components.CustomTimePicker

@Composable
fun WakeupTimeScreen(
    onNavigateToNext: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    var hour by remember { mutableStateOf(7) } // Default wakeup time
    var minute by remember { mutableStateOf(0) }
    
    val uiState by viewModel.uiState.collectAsState()

    OnboardingScreenLayout(
        title = "Wakeup Time",
        subtitle = "Tell us when you typically wake up. This helps us understand your daily routine.",
        buttonText = "Set Wakeup Time",
        isLoading = uiState.isLoading,
        onButtonClick = {
            viewModel.saveWakeupTime(hour, minute)
        }
    ) {
        CustomTimePicker(
            hour = hour,
            minute = minute,
            onHourChange = { hour = it },
            onMinuteChange = { minute = it }
        )
    }
    
    // Navigate when save is successful
    LaunchedEffect(uiState.wakeupTime) {
        if (uiState.wakeupTime != null && !uiState.isLoading) {
            onNavigateToNext()
        }
    }
    
    // Show error if any
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // TODO: Show error snackbar or dialog
            viewModel.clearError()
        }
    }
} 