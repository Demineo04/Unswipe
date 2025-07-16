package com.unswipe.android.ui.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unswipe.android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyProgressScreen(
    onNavigateBack: () -> Unit
) {
    val mockWeeklyData = listOf(
        DayData("Mon", "4h 23m", 78, 0.65f, true),
        DayData("Tue", "3h 45m", 92, 0.52f, false),
        DayData("Wed", "5h 12m", 114, 0.78f, true),
        DayData("Thu", "2h 58m", 67, 0.42f, false),
        DayData("Fri", "4h 36m", 89, 0.68f, true),
        DayData("Sat", "6h 24m", 156, 0.89f, true),
        DayData("Sun", "5h 47m", 132, 0.82f, true)
    )
    
    val weeklyAverage = "4h 35m"
    val totalWeeklyTime = "32h 5m"
    val averageUnlocks = 104
    val bestDay = "Thu"
    val worstDay = "Sat"
    
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
                        text = "Weekly Progress",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Light,
                            color = MinimalistBlack
                        )
                    )
                    
                    Spacer(modifier = Modifier.size(48.dp))
                }
            }
            
            // Weekly Summary
            item {
                WeeklySummaryCard(
                    weeklyAverage = weeklyAverage,
                    totalWeeklyTime = totalWeeklyTime,
                    averageUnlocks = averageUnlocks,
                    bestDay = bestDay,
                    worstDay = worstDay
                )
            }
            
            // Weekly Chart
            item {
                WeeklyChartCard(weeklyData = mockWeeklyData)
            }
            
            // Day-by-day breakdown
            item {
                DayBreakdownCard(weeklyData = mockWeeklyData)
            }
            
            // Weekly Trends
            item {
                WeeklyTrendsCard()
            }
            
            // Weekly Goals
            item {
                WeeklyGoalsCard()
            }
            
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
private fun WeeklySummaryCard(
    weeklyAverage: String,
    totalWeeklyTime: String,
    averageUnlocks: Int,
    bestDay: String,
    worstDay: String
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
                    text = "This Week's Summary",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Normal,
                        color = MinimalistBlack,
                        fontSize = 20.sp
                    )
                )
                
                // Main stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WeeklyStatItem(
                        label = "Daily Average",
                        value = weeklyAverage,
                        modifier = Modifier.weight(1f)
                    )
                    WeeklyStatItem(
                        label = "Total Time",
                        value = totalWeeklyTime,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WeeklyStatItem(
                        label = "Avg Unlocks",
                        value = averageUnlocks.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    WeeklyStatItem(
                        label = "Best Day",
                        value = bestDay,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun WeeklyStatItem(
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
private fun WeeklyChartCard(weeklyData: List<DayData>) {
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
                    text = "Weekly Usage Pattern",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Normal,
                        color = MinimalistBlack,
                        fontSize = 20.sp
                    )
                )
                
                Text(
                    text = "Your screen time throughout the week",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MinimalistBlack,
                        fontSize = 14.sp
                    )
                )
                
                // Weekly bar chart
                val maxUsage = weeklyData.maxOfOrNull { it.usagePercentage } ?: 1f
                val maxBarHeight = 120.dp
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    weeklyData.forEach { day ->
                        val barHeight = maxBarHeight * day.usagePercentage
                        
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            // Usage time on top
                            Text(
                                text = day.screenTime,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MinimalistBlack,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Bar
                            Box(
                                modifier = Modifier
                                    .width(24.dp)
                                    .height(barHeight)
                                    .background(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(
                                                MinimalistBlack,
                                                MinimalistBlack.copy(alpha = 0.7f)
                                            )
                                        ),
                                        shape = RoundedCornerShape(
                                            topStart = 4.dp,
                                            topEnd = 4.dp
                                        )
                                    )
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Day label
                            Text(
                                text = day.dayLabel,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MinimalistBlack,
                                    fontSize = 12.sp,
                                    fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayBreakdownCard(weeklyData: List<DayData>) {
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
                    text = "Daily Breakdown",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Normal,
                        color = MinimalistBlack,
                        fontSize = 20.sp
                    )
                )
                
                weeklyData.forEach { day ->
                    DayBreakdownItem(day = day)
                }
            }
        }
    }
}

@Composable
private fun DayBreakdownItem(day: DayData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = day.dayLabel,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Medium,
                color = MinimalistBlack,
                fontSize = 16.sp
            ),
            modifier = Modifier.weight(1f)
        )
        
        Text(
            text = day.screenTime,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MinimalistBlack,
                fontSize = 14.sp
            ),
            modifier = Modifier.weight(1f)
        )
        
        Text(
            text = "${day.unlocks} unlocks",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MinimalistBlack,
                fontSize = 14.sp
            ),
            modifier = Modifier.weight(1f)
        )
        
        // Progress indicator
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(
                    color = if (day.isOverLimit) MinimalistBlack else MinimalistBlack.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(4.dp)
                )
        )
    }
}

@Composable
private fun WeeklyTrendsCard() {
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
                    text = "Weekly Trends",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Normal,
                        color = MinimalistBlack,
                        fontSize = 20.sp
                    )
                )
                
                TrendItem(
                    label = "Screen Time",
                    value = "+12% from last week",
                    isPositive = false
                )
                
                TrendItem(
                    label = "Phone Unlocks",
                    value = "-8% from last week",
                    isPositive = true
                )
                
                TrendItem(
                    label = "Average Session",
                    value = "+5% from last week",
                    isPositive = false
                )
                
                TrendItem(
                    label = "Focus Sessions",
                    value = "+15% from last week",
                    isPositive = true
                )
            }
        }
    }
}

@Composable
private fun TrendItem(
    label: String,
    value: String,
    isPositive: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MinimalistBlack,
                fontSize = 14.sp
            ),
            modifier = Modifier.weight(1f)
        )
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = if (isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                contentDescription = null,
                tint = MinimalistBlack,
                modifier = Modifier.size(16.dp)
            )
            
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MinimalistBlack,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
private fun WeeklyGoalsCard() {
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
                    text = "Weekly Goals",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Normal,
                        color = MinimalistBlack,
                        fontSize = 20.sp
                    )
                )
                
                GoalItem(
                    label = "Daily limit adherence",
                    progress = 0.71f,
                    value = "5/7 days"
                )
                
                GoalItem(
                    label = "Mindful usage sessions",
                    progress = 0.85f,
                    value = "17/20 sessions"
                )
                
                GoalItem(
                    label = "Focus mode usage",
                    progress = 0.45f,
                    value = "9/20 hours"
                )
            }
        }
    }
}

@Composable
private fun GoalItem(
    label: String,
    progress: Float,
    value: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MinimalistBlack,
                    fontSize = 14.sp
                )
            )
            
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MinimalistBlack,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
        
        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(
                    color = MinimalistBlack.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(2.dp)
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(4.dp)
                    .background(
                        color = MinimalistBlack,
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}

data class DayData(
    val dayLabel: String,
    val screenTime: String,
    val unlocks: Int,
    val usagePercentage: Float,
    val isOverLimit: Boolean,
    val isToday: Boolean = false
)