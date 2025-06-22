package com.unswipe.android.ui.onboarding

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.unswipe.android.ui.components.CustomTimePicker

@Composable
fun WakeupTimeScreen(
    onNavigateToNext: () -> Unit
) {
    var hour by remember { mutableStateOf(7) } // Default wakeup time
    var minute by remember { mutableStateOf(0) }

    OnboardingScreenLayout(
        title = "Wakeup Time",
        subtitle = "Tell us about your Waking Time",
        buttonText = "Set",
        onButtonClick = {
            // Here you would typically save the time:
            // viewModel.setWakeupTime(hour, minute)
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