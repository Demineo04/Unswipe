// Location: app/src/main/java/com/unswipe/android/ui/dashboard/DashboardViewModel.kt

package com.unswipe.android.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    onNavigateToSettings: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Dashboard") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            DashboardContent(
                state = uiState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                onBarSelected = { summary ->
                    snackbarHostState.showSnackbar(
                        message = "${summary.dayLabel}: ${(summary.usagePercentage * 100).toInt()}%"
                    )
                }
            )

    Surface(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            else -> {
                DashboardContent(
                    uiState = uiState,
                    onNavigateToSettings = onNavigateToSettings,
                    onLogout = onLogout
                )
            }
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
fun DashboardContent(
    uiState: DashboardUiState,
    onNavigateToSettings: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Unswipe Dashboard", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Today's Stats
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Today's Usage", style = MaterialTheme.typography.titleMedium)
                Text("Time Used: ${uiState.timeUsedTodayFormatted}")
                Text("Time Remaining: ${uiState.timeRemainingFormatted}")
                LinearProgressIndicator(
                    progress = uiState.usagePercentage,
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Swipes: ${uiState.swipesToday}")
                Text("Unlocks: ${uiState.unlocksToday}")
            }
        }

        // Current Streak
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Current Streak: ${uiState.currentStreak} days", style = MaterialTheme.typography.titleMedium)
            }
        }

        // Weekly Progress
        Text("Weekly Progress", style = MaterialTheme.typography.titleMedium)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(uiState.weeklyProgress) { daySummary ->
                DayProgressView(daySummary)
            }
        }

        Spacer(modifier = Modifier.weight(1f)) // Pushes content below to the bottom

        // Prompts
        if (uiState.showUsagePermissionPrompt) {
            Text(
                "Usage permission needed for accurate tracking.",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            // TODO: Add a button to navigate to permission settings
        }
        if (uiState.showAccessibilityPrompt) {
            Text(
                "Accessibility service needed for swipe detection.",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            // TODO: Add a button to navigate to accessibility settings
        }

        // Premium Status
        Text(if (uiState.isPremium) "Premium User" else "Free User")


        // Navigation Buttons
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = onNavigateToSettings) {
                Text("Settings")
            }
            Button(onClick = onLogout) {
                Text("Logout")
            }
        }
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
