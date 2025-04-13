// Location: app/src/main/java/com/unswipe/android/ui/dashboard/DashboardScreen.kt

package com.unswipe.android.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class) // For Scaffold/TopAppBar
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(), // Get ViewModel via Hilt
    onNavigateToSettings: () -> Unit,
    onLogout: () -> Unit
) {
    // Collect the dashboard state
    val dashboardData by viewModel.dashboardState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Unswipe Dashboard") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply padding from Scaffold
                .padding(16.dp), // Add our own padding
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp) // Spacing between elements
        ) {
            if (dashboardData == null) {
                // Show loading state
                CircularProgressIndicator()
                Text("Loading dashboard...")
            } else {
                // Display actual data once loaded
                DashboardContent(data = dashboardData!!) // Use !! because we checked for null
            }
        }
    }
}

// Separate composable for the main content area
@Composable
fun DashboardContent(data: DashboardData) {
    // --- Time Usage ---
    val timeUsedMinutes = TimeUnit.MILLISECONDS.toMinutes(data.timeUsedTodayMillis)
    val timeLimitMinutes = TimeUnit.MILLISECONDS.toMinutes(data.timeLimitMillis)
    val timeRemainingMinutes = maxOf(0, timeLimitMinutes - timeUsedMinutes)

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Today's Usage", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Used: $timeUsedMinutes min")
            Text("Limit: $timeLimitMinutes min")
            Text("Remaining: $timeRemainingMinutes min", style = MaterialTheme.typography.bodyLarge)
            // Add a simple progress bar maybe?
            if (timeLimitMinutes > 0) {
                LinearProgressIndicator(
                    progress = { (data.timeUsedTodayMillis.toFloat() / data.timeLimitMillis.toFloat()).coerceIn(0f, 1f) }, // Use brace syntax for Compose 1.6+
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }
        }
    }

    // --- Streak ---
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Current Streak", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text("${data.currentStreak} days", style = MaterialTheme.typography.headlineMedium)
        }
    }

    // --- Swipes & Unlocks ---
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Swipes Today", style = MaterialTheme.typography.titleSmall)
                Text("${data.swipesToday}", style = MaterialTheme.typography.bodyLarge)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Unlocks Today", style = MaterialTheme.typography.titleSmall)
                Text("${data.unlocksToday}", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }

    // --- Weekly Chart (Placeholder) ---
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Weekly Progress (Placeholder)", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            // TODO: Implement a Composable chart (e.g., using Canvas or a library)
            // to display data.weeklyProgress list
            Text("Chart showing last 7 days usage would go here.")
            data.weeklyProgress.forEach { summary ->
                val date = remember(summary.dateMillis) { /* Format date */ }
                val minutes = TimeUnit.MILLISECONDS.toMinutes(summary.totalScreenTimeMillis)
                // Text("$date: $minutes min") // Example text output
            }
        }
    }

    if (data.isPremium) {
        Text("Premium Active", style = MaterialTheme.typography.labelSmall)
    }

}