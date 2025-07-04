package com.unswipe.android.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unswipe.android.ui.components.ModernSlidingTimePicker
import com.unswipe.android.ui.theme.*

@Composable
fun WakeupTimeScreen(
    onNavigateToNext: () -> Unit,
    onSkip: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedHour by remember { mutableStateOf(uiState.wakeupTime?.hour ?: 7) }
    var selectedMinute by remember { mutableStateOf(uiState.wakeupTime?.minute ?: 0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        UnswipeBlack,
                        UnswipeSurface
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Skip button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onSkip) {
                    Text(
                        text = "Skip",
                        color = UnswipeTextSecondary,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Progress indicator
            LinearProgressIndicator(
                progress = { 1f / 4f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = UnswipePrimary,
                trackColor = UnswipeGray.copy(alpha = 0.3f),
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Icon
            Text(
                text = "🌅",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 64.sp
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Title
            Text(
                text = "When do you wake up?",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = UnswipeTextPrimary,
                    fontSize = 28.sp
                ),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Subtitle
            Text(
                text = "Help us understand your daily routine to provide personalized insights and smart notifications.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = UnswipeTextSecondary,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Time Picker
            ModernSlidingTimePicker(
                hour = selectedHour,
                minute = selectedMinute,
                onHourChange = { selectedHour = it },
                onMinuteChange = { selectedMinute = it },
                label = "Wake-up Time"
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Next button
            Button(
                onClick = {
                    viewModel.setWakeupTime(selectedHour, selectedMinute)
                    onNavigateToNext()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = UnswipePrimary,
                    contentColor = UnswipeBlack
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Continue",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Helper text
            Text(
                text = "Don't worry, you can change this later in settings",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = UnswipeTextSecondary.copy(alpha = 0.7f),
                    fontSize = 12.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
} 