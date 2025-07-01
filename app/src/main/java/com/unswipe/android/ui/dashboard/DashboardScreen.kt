// Location: app/src/main/java/com/unswipe/android/ui/dashboard/DashboardViewModel.kt

package com.unswipe.android.ui.dashboard

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unswipe.android.ui.components.DashboardHeader
import com.unswipe.android.ui.components.HourlyUsageChart
import com.unswipe.android.ui.components.StatCard

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToSettings: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        // The top bar can be removed if the header is handled within the content
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            else -> {
                DashboardContent(
                    modifier = Modifier.padding(padding),
                    state = uiState,
                    onNavigateToSettings = onNavigateToSettings
                )
            }
        }
    }
}

@Composable
fun DashboardContent(
    modifier: Modifier = Modifier,
    state: DashboardUiState,
    onNavigateToSettings: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 16.dp)
    ) {
        DashboardHeader(
            // Replace with actual user name from ViewModel/Repo if available
            userName = "Eddie",
            totalScreenTime = state.timeUsedTodayFormatted,
            onNavigateToSettings = onNavigateToSettings
        )

        // Permission Prompts
        if (state.showUsagePermissionPrompt || state.showAccessibilityPrompt) {
            PermissionPrompts(
                showUsagePermissionPrompt = state.showUsagePermissionPrompt,
                showAccessibilityPrompt = state.showAccessibilityPrompt,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        WeeklyUsageChart(weeklyProgress = state.weeklyProgress)

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                label = "Screen Unlocks",
                value = state.unlocksToday.toString()
            )
            StatCard(
                modifier = Modifier.weight(1f),
                label = "App Launches",
                value = state.swipesToday.toString() // Assuming swipes == app launches for now
            )
             StatCard(
                modifier = Modifier.weight(1f),
                label = "Goal",
                value = "80%" // Placeholder
            )
        }
    }
}

@Composable
private fun DashboardContent(
    state: DashboardUiState,
    modifier: Modifier = Modifier,
    onBarSelected: (DailyUsageSummary) -> Unit
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(text = "Time used today: ${state.timeUsedTodayFormatted}")
        Text(text = "Time remaining: ${state.timeRemainingFormatted}")
        LinearProgressIndicator(progress = state.usagePercentage)

        Text(text = "Streak: ${state.currentStreak} days")
        Text(text = "Swipes today: ${state.swipesToday}")
        Text(text = "Unlocks today: ${state.unlocksToday}")

        WeeklyUsageChart(
            summaries = state.weeklyProgress,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            onBarClicked = onBarSelected
        )
    }
}

@Composable
private fun WeeklyUsageChart(
    summaries: List<DailyUsageSummary>,
    modifier: Modifier = Modifier,
    onBarClicked: (DailyUsageSummary) -> Unit
) {
    val maxBarHeight: Dp = 120.dp
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        summaries.forEach { summary ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                val barHeight = maxBarHeight * summary.usagePercentage
                Box(
                    modifier = Modifier
                        .height(barHeight)
                        .fillMaxWidth(0.6f)
                        .background(
                            if (summary.isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                        .clickable { onBarClicked(summary) }
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = summary.dayLabel)
            }
        }
    }
}

@Composable
private fun PermissionPrompts(
    showUsagePermissionPrompt: Boolean,
    showAccessibilityPrompt: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    Column(modifier = modifier) {
        if (showUsagePermissionPrompt) {
            PermissionPromptCard(
                title = "Usage Statistics Permission Required",
                description = "Grant usage access to track your social media time",
                onGrantPermission = {
                    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                    context.startActivity(intent)
                }
            )
            
            if (showAccessibilityPrompt) {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
        
        if (showAccessibilityPrompt) {
            PermissionPromptCard(
                title = "Accessibility Service Required",
                description = "Enable accessibility service for app launch confirmations",
                onGrantPermission = {
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    context.startActivity(intent)
                }
            )
        }
    }
}

@Composable
private fun PermissionPromptCard(
    title: String,
    description: String,
    onGrantPermission: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            OutlinedButton(
                onClick = onGrantPermission,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text("Grant")
            }
        }
    }
}

fun DayProgressView(summary: DailyUsageSummary) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .width(IntrinsicSize.Min) // Ensure column takes minimum width needed by its children
    ) {
        Text(
            text = summary.dayLabel,
            fontSize = 12.sp,
            color = if (summary.isToday) MaterialTheme.colorScheme.primary else Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .height(100.dp) // Fixed height for the bar
                .width(20.dp)   // Fixed width for the bar
                .fillMaxHeight()
                .align(Alignment.CenterHorizontally)
        ) {
            LinearProgressIndicator(
                progress = summary.usagePercentage,
                modifier = Modifier
                    .fillMaxHeight() // Fill the height of the Box
                    .width(20.dp),   // Ensure the indicator itself also has a width
                color = if (summary.isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${(summary.usagePercentage * 100).toInt()}%",
            fontSize = 10.sp,
            color = if (summary.isToday) MaterialTheme.colorScheme.primary else Color.Gray
        )
    }
}
