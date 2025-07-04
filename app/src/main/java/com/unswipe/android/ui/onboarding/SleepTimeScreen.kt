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
fun SleepTimeScreen(
    onNavigateToNext: () -> Unit,
    onBack: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedHour by remember { mutableStateOf(uiState.sleepTime?.hour ?: 22) }
    var selectedMinute by remember { mutableStateOf(uiState.sleepTime?.minute ?: 0) }

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
            Spacer(modifier = Modifier.height(40.dp))
            
            // Progress indicator
            LinearProgressIndicator(
                progress = { 3f / 4f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = UnswipePrimary,
                trackColor = UnswipeGray.copy(alpha = 0.3f),
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Icon
            Text(
                text = "🌙",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 64.sp
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Title
            Text(
                text = "Sleep Schedule",
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
                text = "When do you usually go to bed? We'll help you wind down with smart restrictions.",
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
                label = "Bedtime"
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Sleep quality tip card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = UnswipePrimary.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "💡",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Better Sleep Quality",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = UnswipeTextPrimary
                            )
                        )
                        Text(
                            text = "We'll automatically reduce blue light and block distracting apps 1 hour before bedtime.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = UnswipeTextSecondary,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onBack,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = UnswipeTextSecondary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Back")
                }
                
                Button(
                    onClick = {
                        viewModel.setSleepTime(selectedHour, selectedMinute)
                        onNavigateToNext()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = UnswipePrimary,
                        contentColor = UnswipeBlack
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                        .height(56.dp)
                ) {
                    Text(
                        text = "Continue",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Helper text
            Text(
                text = "You can adjust these settings anytime in your profile",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = UnswipeTextSecondary.copy(alpha = 0.7f),
                    fontSize = 12.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
} 