package com.unswipe.android.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.unswipe.android.ui.components.PrimaryButton
import com.unswipe.android.ui.components.OtpTextField

@Composable
fun OtpVerificationScreen(
    onNavigateToNext: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var otpValue by remember { mutableStateOf("") }
    var isOtpFilled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        // Back button or similar navigation can be placed here in a top bar
        
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "OTP Verification",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                "Enter the verification code we just sent to your email address.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp)
            )

            OtpTextField(
                otpText = otpValue,
                onOtpTextChange = { value, isComplete ->
                    otpValue = value
                    isOtpFilled = isComplete
                },
                otpCount = 6 // As per design
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            TextButton(onClick = { /* TODO: Resend OTP logic */ }) {
                Text("Didn't receive code? Resend")
            }
        }

        PrimaryButton(
            text = "Verify",
            onClick = onNavigateToNext,
            enabled = isOtpFilled,
            modifier = Modifier.padding(bottom = 32.dp)
        )
    }
} 