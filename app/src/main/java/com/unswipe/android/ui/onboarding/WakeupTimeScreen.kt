package com.unswipe.android.ui.onboarding

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.unswipe.android.ui.components.CustomTimePicker

@Composable
fun WakeupTimeScreen(
    onNavigateToNext: () -> Unit,
    // viewModel: OnboardingViewModel = hiltViewModel() // TEMPORARILY DISABLED
) {
    // TEMPORARILY SIMPLIFIED
    Text("Wakeup Time Screen - Working!")
} 