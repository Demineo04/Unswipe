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
fun WorkTimeScreen(
    onNavigateToNext: () -> Unit,
    onBack: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var startHour by remember { mutableStateOf(uiState.workStartTime?.hour ?: 9) }
    var startMinute by remember { mutableStateOf(uiState.workStartTime?.minute ?: 0) }
    var endHour by remember { mutableStateOf(uiState.workEndTime?.hour ?: 17) }
    var endMinute by remember { mutableStateOf(uiState.workEndTime?.minute ?: 0) }
    var isStartTimeSelected by remember { mutableStateOf(true) }

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
                progress = { 2f / 4f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = UnswipePrimary,
                trackColor = UnswipeGray.copy(alpha = 0.3f),
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Icon
            Text(
                text = "💼",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 64.sp
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            // Title
            Text(
                text = "Work Schedule",
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
                text = "When do you typically start and end work? This helps us adjust your focus periods.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = UnswipeTextSecondary,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Time selection toggle
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = UnswipeCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    // Start time button
                    Button(
                        onClick = { isStartTimeSelected = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isStartTimeSelected) UnswipePrimary else Color.Transparent,
                            contentColor = if (isStartTimeSelected) UnswipeBlack else UnswipeTextSecondary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Start Time",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = "${startHour.toString().padStart(2, '0')}:${startMinute.toString().padStart(2, '0')}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // End time button
                    Button(
                        onClick = { isStartTimeSelected = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isStartTimeSelected) UnswipePrimary else Color.Transparent,
                            contentColor = if (!isStartTimeSelected) UnswipeBlack else UnswipeTextSecondary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "End Time",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                text = "${endHour.toString().padStart(2, '0')}:${endMinute.toString().padStart(2, '0')}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Time Picker
            ModernSlidingTimePicker(
                hour = if (isStartTimeSelected) startHour else endHour,
                minute = if (isStartTimeSelected) startMinute else endMinute,
                onHourChange = { 
                    if (isStartTimeSelected) startHour = it else endHour = it 
                },
                onMinuteChange = { 
                    if (isStartTimeSelected) startMinute = it else endMinute = it 
                },
                label = if (isStartTimeSelected) "Work Start Time" else "Work End Time"
            )
            
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
                        viewModel.setWorkTime(startHour, startMinute, endHour, endMinute)
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
                text = "We'll use this to enable Focus Mode during work hours",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = UnswipeTextSecondary.copy(alpha = 0.7f),
                    fontSize = 12.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
} 