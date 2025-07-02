package com.unswipe.android.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyLimitScreen(
    onNavigateBack: () -> Unit,
    // viewModel: SettingsViewModel = hiltViewModel() // TEMPORARILY DISABLED
) {
    // val uiState by viewModel.uiState.collectAsState() // TEMPORARILY DISABLED
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Limit") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Column {
                    Text(
                        text = "Set Your Daily Limit",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Choose how much time you want to spend on social media",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            
            // TEMPORARILY SIMPLIFIED - just show working text
            Text("Daily Limit Screen - Working!")
            
            /*
            // Limit slider
            DailyLimitSlider(
                currentLimitMillis = uiState.dailyLimitMillis,
                onLimitChanged = { newLimit ->
                    viewModel.updateDailyLimit(newLimit)
                }
            )
            
            // Preset options
            Text(
                text = "Quick Presets",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            
            val presets = listOf(
                30 * 60 * 1000L to "30 minutes",
                60 * 60 * 1000L to "1 hour", 
                90 * 60 * 1000L to "1.5 hours",
                120 * 60 * 1000L to "2 hours",
                180 * 60 * 1000L to "3 hours"
            )
            
            presets.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { (millis, label) ->
                        OutlinedButton(
                            onClick = { viewModel.updateDailyLimit(millis) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (uiState.dailyLimitMillis == millis) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            )
                        ) {
                            Text(label)
                        }
                    }
                    // Fill remaining space if odd number of items
                    if (row.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            
            // Information card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "💡 Tip",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Start with a realistic limit and gradually reduce it. Most people spend 2-3 hours daily on social media.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            */
        }
    }
}

@Composable
private fun DailyLimitSlider(
    currentLimitMillis: Long,
    onLimitChanged: (Long) -> Unit
) {
    val minMinutes = 15f
    val maxMinutes = 480f // 8 hours
    val currentMinutes = (currentLimitMillis / (60 * 1000)).toFloat()
    
    Column {
        Text(
            text = "Adjust Limit",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Slider(
            value = currentMinutes,
            onValueChange = { minutes ->
                val millis = (minutes * 60 * 1000).toLong()
                onLimitChanged(millis)
            },
            valueRange = minMinutes..maxMinutes,
            steps = 37, // 15min increments approximately
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "15m",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = "8h",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

private fun formatTime(millis: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(millis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
    
    return when {
        hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
        hours > 0 -> "${hours}h"
        else -> "${minutes}m"
    }
}