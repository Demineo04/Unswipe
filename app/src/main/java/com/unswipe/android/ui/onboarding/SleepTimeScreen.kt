package com.unswipe.android.ui.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.unswipe.android.ui.components.CustomTimePicker

@Composable
fun SleepTimeScreen(
    onNavigateToNext: () -> Unit
) {
    var hour by remember { mutableStateOf(22) } // Default sleep time
    var minute by remember { mutableStateOf(0) }

    OnboardingScreenLayout(
        title = "Sleep Time",
        subtitle = "Tell us about your Sleeping Time",
        buttonText = "Done",
        onButtonClick = {
            // viewModel.setSleepTime(hour, minute)
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