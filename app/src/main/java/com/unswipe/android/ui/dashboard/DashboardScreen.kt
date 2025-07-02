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
    viewModel: DashboardViewModel,
    onNavigateToSettings: () -> Unit,
    onNavigateToUnlocksDetail: () -> Unit = {},
    onNavigateToAppLaunchesDetail: () -> Unit = {},
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        UnswipeBlack,
                        UnswipeSurface
                    )
                )
            )
    ) {
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        color = UnswipePrimary,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            uiState.error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = UnswipeCard
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "⚠️",
                                style = MaterialTheme.typography.headlineLarge,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Text(
                                text = "Something went wrong",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = UnswipeTextPrimary,
                                    fontWeight = FontWeight.Bold
                                ),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = uiState.error ?: "Unknown error",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = UnswipeTextSecondary
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp)
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
                userName = "Eddie", // TODO: Get from user preferences
                onNavigateToSettings = onNavigateToSettings
            )
        }
        
        item {
            // Main usage card
            UsageOverviewCard(
                timeUsed = state.timeUsedTodayFormatted,
                timeRemaining = state.timeRemainingFormatted,
                usagePercentage = state.usagePercentage
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
                summaries = state.weeklyProgress
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
                text = "Hi, $userName",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = UnswipeTextPrimary
                )
            )
            Text(
                text = "Let's check your progress",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = UnswipeTextSecondary,
                    fontSize = 16.sp
                )
            )
        }
        
        IconButton(
            onClick = onNavigateToSettings,
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = UnswipeCard,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = UnswipeTextPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun UsageOverviewCard(
    timeUsed: String,
    timeRemaining: String,
    usagePercentage: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = UnswipeCard
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box {
            // Background gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                UnswipePrimary.copy(alpha = 0.1f),
                                UnswipeSecondary.copy(alpha = 0.1f)
                            )
                        )
                    )
            )
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = timeUsed,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = UnswipePrimary
                            )
                        )
                        Text(
                            text = "Total screen time today",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = UnswipeTextSecondary,
                                fontSize = 14.sp
                            )
                        )
                    }
                    
                    // Circular progress indicator
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(64.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { usagePercentage },
                            modifier = Modifier.size(64.dp),
                            color = UnswipePrimary,
                            strokeWidth = 6.dp,
                            trackColor = UnswipeGray.copy(alpha = 0.3f)
                        )
                        Text(
                            text = "${(usagePercentage * 100).toInt()}%",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = UnswipeTextPrimary,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Remaining",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = UnswipeTextSecondary,
                                fontSize = 12.sp
                            )
                        )
                        Text(
                            text = timeRemaining,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = UnswipeTextPrimary,
                                fontSize = 18.sp
                            )
                        )
                    }
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Status",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = UnswipeTextSecondary,
                                fontSize = 12.sp
                            )
                        )
                        Text(
                            text = if (usagePercentage < 0.8f) "On Track 🎯" else "Over Limit ⚠️",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (usagePercentage < 0.8f) UnswipeGreen else UnswipeWarning,
                                fontSize = 18.sp
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
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            icon = "🔓",
            value = unlocks.toString(),
            label = "Unlocks",
            color = UnswipeSecondary,
            onClick = onUnlocksClick
        )
        
        StatCard(
            modifier = Modifier.weight(1f),
            icon = "📱",
            value = launches.toString(),
            label = "App Launches",
            color = UnswipeWarning,
            onClick = onAppLaunchesClick
        )
        
        StatCard(
            modifier = Modifier.weight(1f),
            icon = "🔥",
            value = "${streak}d",
            label = "Goal Streak",
            color = UnswipeGreen
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
    Card(
        modifier = modifier
            .then(
                if (onClick != null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = UnswipeCard
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = 24.sp
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = color,
                    fontSize = 20.sp
                )
            )
            
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = UnswipeTextSecondary,
                    fontSize = 12.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun WeeklyProgressCard(
    summaries: List<DailyUsageSummary>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = UnswipeCard
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Weekly Progress",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = UnswipeTextPrimary,
                    fontSize = 20.sp
                )
            )
            
            Text(
                text = "Your daily usage pattern",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = UnswipeTextSecondary,
                    fontSize = 14.sp
                ),
                modifier = Modifier.padding(bottom = 20.dp)
            )
            
            ModernWeeklyChart(summaries = summaries)
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
