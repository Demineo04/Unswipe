package com.unswipe.android.ui.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unswipe.android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenTimeInsightsScreen(
    onNavigateBack: () -> Unit
) {
    val mockProblematicApps = listOf(
        ProblematicApp("Instagram", "2h 34m", "45%", "Critical"),
        ProblematicApp("TikTok", "1h 52m", "32%", "High"),
        ProblematicApp("YouTube", "1h 23m", "24%", "Medium"),
        ProblematicApp("Facebook", "47m", "14%", "Low")
    )
    
    val mockHourlyData = mapOf(
        9 to 12, 10 to 18, 11 to 8, 12 to 25, 13 to 15, 14 to 22, 
        15 to 28, 16 to 35, 17 to 42, 18 to 38, 19 to 45, 20 to 52, 
        21 to 38, 22 to 25, 23 to 15
    )
    
    val totalScreenTime = "5h 36m"
    val totalUnlocks = 127
    val averageSession = "2m 38s"
    val longestSession = "45m"
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MinimalistWhite)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(40.dp))
                
                // Header with back button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = MinimalistBlack,
                                shape = RoundedCornerShape(0.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MinimalistWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Text(
                        text = "Screen Time Insights",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Light,
                            color = MinimalistBlack
                        )
                    )
                    
                    Spacer(modifier = Modifier.size(48.dp))
                }
            }
            
            // Today's Summary
            item {
                SummaryCard(
                    totalScreenTime = totalScreenTime,
                    totalUnlocks = totalUnlocks,
                    averageSession = averageSession,
                    longestSession = longestSession
                )
            }
            
            // Hourly Pattern
            item {
                HourlyPatternCard(hourlyData = mockHourlyData)
            }
            
            // Problematic Apps
            item {
                ProblematicAppsCard(apps = mockProblematicApps)
            }
            
            // Digital Wellness Score
            item {
                DigitalWellnessCard(score = 72)
            }
            
            // Recommendations
            item {
                RecommendationsCard()
            }
            
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
private fun SummaryCard(
    totalScreenTime: String,
    totalUnlocks: Int,
    averageSession: String,
    longestSession: String
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
                .padding(2.dp),
            color = MinimalistWhite,
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "Today's Overview",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Normal,
                        color = MinimalistBlack,
                        fontSize = 20.sp
                    )
                )
                
                // Stats Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatItem(
                        label = "Total Time",
                        value = totalScreenTime,
                        modifier = Modifier.weight(1f)
                    )
                    StatItem(
                        label = "Unlocks",
                        value = totalUnlocks.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatItem(
                        label = "Avg Session",
                        value = averageSession,
                        modifier = Modifier.weight(1f)
                    )
                    StatItem(
                        label = "Longest Session",
                        value = longestSession,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Light,
                color = MinimalistBlack,
                fontSize = 24.sp
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MinimalistBlack,
                fontSize = 14.sp
            )
        )
    }
}

@Composable
private fun HourlyPatternCard(hourlyData: Map<Int, Int>) {
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
                .padding(2.dp),
            color = MinimalistWhite,
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Hourly Activity Pattern",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Normal,
                        color = MinimalistBlack,
                        fontSize = 20.sp
                    )
                )
                
                Text(
                    text = "Your phone usage throughout the day",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MinimalistBlack,
                        fontSize = 14.sp
                    )
                )
                
                // Simplified hourly chart
                val maxUsage = hourlyData.values.maxOrNull() ?: 1
                val maxBarHeight = 60.dp
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    hourlyData.forEach { (hour, usage) ->
                        val barHeight = maxBarHeight * (usage.toFloat() / maxUsage)
                        
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(8.dp)
                                    .height(barHeight)
                                    .background(
                                        color = MinimalistBlack,
                                        shape = RoundedCornerShape(
                                            topStart = 2.dp,
                                            topEnd = 2.dp
                                        )
                                    )
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            if (hour % 3 == 0) {
                                Text(
                                    text = "${hour}h",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MinimalistBlack,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProblematicAppsCard(apps: List<ProblematicApp>) {
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
                .padding(2.dp),
            color = MinimalistWhite,
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MinimalistBlack,
                        modifier = Modifier.size(20.dp)
                    )
                    
                    Text(
                        text = "Problematic Apps",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Normal,
                            color = MinimalistBlack,
                            fontSize = 20.sp
                        )
                    )
                }
                
                Text(
                    text = "Apps consuming most of your time",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MinimalistBlack,
                        fontSize = 14.sp
                    )
                )
                
                apps.forEach { app ->
                    AppUsageItem(app = app)
                }
            }
        }
    }
}

@Composable
private fun AppUsageItem(app: ProblematicApp) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = app.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MinimalistBlack,
                    fontSize = 16.sp
                )
            )
            Text(
                text = "${app.timeSpent} • ${app.percentage} of total",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MinimalistBlack,
                    fontSize = 12.sp
                )
            )
        }
        
        Text(
            text = app.severity,
            style = MaterialTheme.typography.labelMedium.copy(
                color = MinimalistBlack,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
private fun DigitalWellnessCard(score: Int) {
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
                .padding(2.dp),
            color = MinimalistWhite,
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Digital Wellness Score",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Normal,
                        color = MinimalistBlack,
                        fontSize = 20.sp
                    )
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$score/100",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Light,
                            color = MinimalistBlack,
                            fontSize = 48.sp
                        )
                    )
                    
                    Column {
                        Text(
                            text = when {
                                score >= 80 -> "Excellent"
                                score >= 60 -> "Good"
                                score >= 40 -> "Fair"
                                else -> "Needs Improvement"
                            },
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = MinimalistBlack,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = "Based on usage patterns",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MinimalistBlack,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecommendationsCard() {
    val recommendations = listOf(
        "Take breaks every 30 minutes",
        "Set app time limits for Instagram",
        "Use Do Not Disturb during work hours",
        "Try the 20-20-20 rule for eye health"
    )
    
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
                .padding(2.dp),
            color = MinimalistWhite,
            shape = RoundedCornerShape(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Recommendations",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Normal,
                        color = MinimalistBlack,
                        fontSize = 20.sp
                    )
                )
                
                recommendations.forEach { recommendation ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .background(
                                    color = MinimalistBlack,
                                    shape = RoundedCornerShape(2.dp)
                                )
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Text(
                            text = recommendation,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MinimalistBlack,
                                fontSize = 14.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

data class ProblematicApp(
    val name: String,
    val timeSpent: String,
    val percentage: String,
    val severity: String
)