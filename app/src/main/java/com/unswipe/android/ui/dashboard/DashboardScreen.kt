// Location: app/src/main/java/com/unswipe/android/ui/dashboard/DashboardViewModel.kt

package com.unswipe.android.ui.dashboard

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unswipe.android.ui.components.DashboardHeader
import com.unswipe.android.ui.components.HourlyUsageChart
import com.unswipe.android.ui.components.StatCard
import com.unswipe.android.ui.theme.*

@Composable
fun DashboardScreen(
    dashboardViewModel: DashboardViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToUnlocksDetail: () -> Unit = {},
    onNavigateToAppLaunchesDetail: () -> Unit = {},
    onNavigateToScreenTimeInsights: () -> Unit = {},
    onNavigateToWeeklyProgress: () -> Unit = {},
    onLogout: () -> Unit
) {
    val uiState by dashboardViewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MinimalistWhite) // Pure white background for minimalism
    ) {
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        color = MinimalistBlack,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            uiState.error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        color = MinimalistBlack,
                        shape = RoundedCornerShape(0.dp),
                        shadowElevation = 0.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Error",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = MinimalistWhite,
                                    fontWeight = FontWeight.Bold
                                ),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = uiState.error ?: "Unknown error",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MinimalistWhite
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            else -> {
                ModernDashboardContent(
                    state = uiState,
                    onNavigateToSettings = onNavigateToSettings,
                    onNavigateToUnlocksDetail = onNavigateToUnlocksDetail,
                    onNavigateToAppLaunchesDetail = onNavigateToAppLaunchesDetail,
                    onNavigateToScreenTimeInsights = onNavigateToScreenTimeInsights,
                    onNavigateToWeeklyProgress = onNavigateToWeeklyProgress,
                    onLogout = onLogout
                )
            }
        }
    }
}

@Composable
fun ModernDashboardContent(
    state: DashboardUiState,
    onNavigateToSettings: () -> Unit,
    onNavigateToUnlocksDetail: () -> Unit = {},
    onNavigateToAppLaunchesDetail: () -> Unit = {},
    onNavigateToScreenTimeInsights: () -> Unit = {},
    onNavigateToWeeklyProgress: () -> Unit = {},
    onLogout: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(40.dp))
            
            // Header with greeting and settings
            ModernDashboardHeader(
                userName = "User", // TODO: Get actual user name from state
                onNavigateToSettings = onNavigateToSettings
            )
        }
        
        item {
            // Main usage card
            UsageOverviewCard(
                timeUsed = state.timeUsedTodayFormatted,
                timeRemaining = state.timeRemainingFormatted,
                usagePercentage = state.usagePercentage,
                onScreenTimeClick = onNavigateToScreenTimeInsights
            )
        }
        
        // Permission prompts if needed
        if (state.showUsagePermissionPrompt || state.showAccessibilityPrompt) {
            item {
                ModernPermissionPrompts(
                    showUsagePermissionPrompt = state.showUsagePermissionPrompt,
                    showAccessibilityPrompt = state.showAccessibilityPrompt
                )
            }
        }
        
        item {
            // Stats cards row
            StatsCardsRow(
                unlocks = state.unlocksToday,
                launches = state.swipesToday,
                streak = state.currentStreak,
                onUnlocksClick = onNavigateToUnlocksDetail,
                onAppLaunchesClick = onNavigateToAppLaunchesDetail
            )
        }
        
        item {
            // Weekly progress chart
            WeeklyProgressCard(
                summaries = state.weeklyProgress,
                onWeeklyProgressClick = onNavigateToWeeklyProgress
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(100.dp)) // Bottom padding
        }
    }
}

@Composable
private fun ModernDashboardHeader(
    userName: String,
    onNavigateToSettings: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = userName,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Light,
                    color = MinimalistBlack
                )
            )
            Text(
                text = "Dashboard",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MinimalistBlack,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            )
        }
        
        IconButton(
            onClick = onNavigateToSettings,
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = MinimalistBlack,
                    shape = RoundedCornerShape(0.dp)
                )
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = MinimalistWhite,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun UsageOverviewCard(
    timeUsed: String,
    timeRemaining: String,
    usagePercentage: Float,
    onScreenTimeClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(1.dp), // Minimal border effect
        color = MinimalistBlack,
        shape = RoundedCornerShape(0.dp),
        shadowElevation = 0.dp
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp)
                .clickable { onScreenTimeClick() },
            color = MinimalistWhite,
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Main time display
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = timeUsed,
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Light,
                            color = MinimalistBlack
                        )
                    )
                    Text(
                        text = "Screen time today",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MinimalistBlack,
                            fontSize = 14.sp
                        )
                    )
                }
                
                // Progress bar (minimalist line)
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(MinimalistBlack)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(usagePercentage)
                                .height(2.dp)
                                .background(MinimalistBlack)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "${(usagePercentage * 100).toInt()}% of daily limit",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MinimalistBlack,
                            fontSize = 12.sp
                        )
                    )
                }
                
                // Additional stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Remaining",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MinimalistBlack,
                                fontSize = 12.sp
                            )
                        )
                        Text(
                            text = timeRemaining,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = MinimalistBlack,
                                fontSize = 16.sp
                            )
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Status",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MinimalistBlack,
                                fontSize = 12.sp
                            )
                        )
                        Text(
                            text = if (usagePercentage < 0.8f) "On Track" else "Over Limit",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = MinimalistBlack,
                                fontSize = 16.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatsCardsRow(
    unlocks: Int,
    launches: Int,
    streak: Int,
    onUnlocksClick: () -> Unit = {},
    onAppLaunchesClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(1.dp) // Minimal spacing for borders
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            icon = "",
            value = unlocks.toString(),
            label = "Unlocks",
            color = MinimalistBlack,
            onClick = onUnlocksClick
        )
        
        StatCard(
            modifier = Modifier.weight(1f),
            icon = "",
            value = launches.toString(),
            label = "Launches",
            color = MinimalistBlack,
            onClick = onAppLaunchesClick
        )
        
        StatCard(
            modifier = Modifier.weight(1f),
            icon = "",
            value = "${streak}d",
            label = "Streak",
            color = MinimalistBlack
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: String,
    value: String,
    label: String,
    color: androidx.compose.ui.graphics.Color,
    onClick: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        color = MinimalistBlack,
        shape = RoundedCornerShape(0.dp),
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Light,
                    color = MinimalistWhite,
                    fontSize = 24.sp
                )
            )
            
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MinimalistWhite,
                    fontSize = 12.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun WeeklyProgressCard(
    summaries: List<DailyUsageSummary>,
    onWeeklyProgressClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(1.dp),
        color = MinimalistBlack,
        shape = RoundedCornerShape(0.dp),
        shadowElevation = 0.dp
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp)
                .clickable { onWeeklyProgressClick() },
            color = MinimalistWhite,
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Weekly Progress",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Normal,
                        color = MinimalistBlack,
                        fontSize = 20.sp
                    )
                )
                
                Text(
                    text = "Daily usage pattern",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MinimalistBlack,
                        fontSize = 14.sp
                    )
                )
                
                ModernWeeklyChart(summaries = summaries)
            }
        }
    }
}

@Composable
private fun ModernWeeklyChart(
    summaries: List<DailyUsageSummary>
) {
    val maxBarHeight = 80.dp
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        summaries.forEach { summary ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                val barHeight = maxBarHeight * summary.usagePercentage
                val barColor = when {
                    summary.isToday -> UnswipePrimary
                    summary.usagePercentage < 0.5f -> UnswipeGreen
                    summary.usagePercentage < 0.8f -> UnswipeWarning
                    else -> UnswipeRed
                }
                
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(barHeight)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    barColor,
                                    barColor.copy(alpha = 0.6f)
                                )
                            ),
                            shape = RoundedCornerShape(
                                topStart = 12.dp,
                                topEnd = 12.dp
                            )
                        )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = summary.dayLabel,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (summary.isToday) UnswipePrimary else UnswipeTextSecondary,
                        fontWeight = if (summary.isToday) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun ModernPermissionPrompts(
    showUsagePermissionPrompt: Boolean,
    showAccessibilityPrompt: Boolean
) {
    val context = LocalContext.current
    
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (showUsagePermissionPrompt) {
            ModernPermissionCard(
                icon = "📊",
                title = "Usage Access Required",
                description = "Grant permission to track your app usage",
                buttonText = "Grant Access",
                onGrantClick = {
                    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                    context.startActivity(intent)
                }
            )
        }
        
        if (showAccessibilityPrompt) {
            ModernPermissionCard(
                icon = "🛡️",
                title = "Accessibility Service Required",
                description = "Enable service for app launch confirmations",
                buttonText = "Enable Service",
                onGrantClick = {
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    context.startActivity(intent)
                }
            )
        }
    }
}

@Composable
private fun ModernPermissionCard(
    icon: String,
    title: String,
    description: String,
    buttonText: String,
    onGrantClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = UnswipeWarning.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(width = 1.dp, color = UnswipeWarning.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = 24.sp
                ),
                modifier = Modifier.padding(end = 16.dp)
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = UnswipeTextPrimary
                    )
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = UnswipeTextSecondary
                    )
                )
            }
            
            Button(
                onClick = onGrantClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = UnswipeWarning,
                    contentColor = UnswipeBlack
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = buttonText,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

@Composable
fun DashboardContent(
    modifier: Modifier = Modifier,
    state: DashboardUiState,
    onNavigateToSettings: () -> Unit,
    onNavigateToUnlocksDetail: () -> Unit = {},
    onNavigateToAppLaunchesDetail: () -> Unit = {}
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

        WeeklyUsageChart(
            summaries = state.weeklyProgress,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            onBarClicked = { /* Handle bar click */ }
        )

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
                value = state.unlocksToday.toString(),
                onClick = onNavigateToUnlocksDetail
            )
            StatCard(
                modifier = Modifier.weight(1f),
                label = "App Launches",
                value = state.swipesToday.toString(), // Assuming swipes == app launches for now
                onClick = onNavigateToAppLaunchesDetail
            )
             StatCard(
                modifier = Modifier.weight(1f),
                label = "Goal",
                value = "${(state.usagePercentage * 100).toInt()}%"
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
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text("Grant")
            }
        }
    }
}

@Composable
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
