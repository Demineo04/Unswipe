package com.unswipe.android.ui.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.unswipe.android.ui.components.CustomTimePicker

@Composable
fun WorkTimeScreen(
    onNavigateToNext: () -> Unit
) {
    var hour by remember { mutableStateOf(9) } // Default work start time
    var minute by remember { mutableStateOf(0) }

    OnboardingScreenLayout(
        title = "Work Time",
        subtitle = "Tell us about your Work Time",
        buttonText = "Set",
        onButtonClick = {
            // viewModel.setWorkTime(hour, minute)
            onNavigateToNext()
        }
    ) {
        CustomTimePicker(
            hour = hour,
            minute = minute,
            onHourChange = { hour = it },
            onMinuteChange = { minute = it }
        )
    }
} 