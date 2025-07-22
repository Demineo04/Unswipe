package com.unswipe.android.ui.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unswipe.android.domain.model.AnalyticsPeriod
import com.unswipe.android.domain.model.UsageStats
import com.unswipe.android.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MinimalistWhite)
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
                    ErrorMessage(error = uiState.error!!)
                }
            }
            else -> {
                AnalyticsContent(
                    selectedPeriod = uiState.selectedPeriod,
                    usageStats = uiState.usageStats,
                    onPeriodSelected = viewModel::selectPeriod
                )
            }
        }
    }
}

@Composable
private fun AnalyticsContent(
    selectedPeriod: AnalyticsPeriod,
    usageStats: UsageStats?,
    onPeriodSelected: (AnalyticsPeriod) -> Unit
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            
            // Analytics Header
            Text(
                text = "Analytics",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Light,
                    color = MinimalistBlack
                )
            )
        }
        
        item {
            // Period Selector
            PeriodSelector(
                selectedPeriod = selectedPeriod,
                onPeriodSelected = onPeriodSelected
            )
        }
        
        // Date Selection for Weekly and Monthly
        if (selectedPeriod != AnalyticsPeriod.DAILY) {
            item {
                DateSelector(
                    selectedPeriod = selectedPeriod,
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it }
                )
            }
        }
        
        if (usageStats != null) {
            item {
                // Metrics Cards in 2x2 Grid
                MetricsGrid(
                    usageStats = usageStats,
                    selectedPeriod = selectedPeriod
                )
            }
            
            // Weekly Progress Chart for Weekly view
            if (selectedPeriod == AnalyticsPeriod.WEEKLY) {
                item {
                    WeeklyProgressChart(selectedDate = selectedDate)
                }
            }
            
            // Calendar View for Monthly
            if (selectedPeriod == AnalyticsPeriod.MONTHLY) {
                item {
                    MonthlyCalendarView(selectedDate = selectedDate)
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(100.dp)) // Bottom padding
        }
    }
}

@Composable
private fun PeriodSelector(
    selectedPeriod: AnalyticsPeriod,
    onPeriodSelected: (AnalyticsPeriod) -> Unit
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                
                PeriodButton(
                    text = "Weekly", 
                    isSelected = selectedPeriod == AnalyticsPeriod.WEEKLY,
                    onClick = { onPeriodSelected(AnalyticsPeriod.WEEKLY) },
                    modifier = Modifier.weight(1f)
                )
                
                PeriodButton(
                    text = "Monthly",
                    isSelected = selectedPeriod == AnalyticsPeriod.MONTHLY,
                    onClick = { onPeriodSelected(AnalyticsPeriod.MONTHLY) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun PeriodButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = if (isSelected) MinimalistBlack else MinimalistWhite,
        shape = RoundedCornerShape(0.dp),
        shadowElevation = 0.dp
    ) {
        TextButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    color = if (isSelected) MinimalistWhite else MinimalistBlack,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            )
        }
    }
}

@Composable
private fun DateSelector(
    selectedPeriod: AnalyticsPeriod,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
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
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = when (selectedPeriod) {
                        AnalyticsPeriod.WEEKLY -> "Select Week"
                        AnalyticsPeriod.MONTHLY -> "Select Month"
                        else -> "Select Date"
                    },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Normal,
                        color = MinimalistBlack,
                        fontSize = 18.sp
                    )
                )
                
                // Date navigation
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { 
                            onDateSelected(
                                when (selectedPeriod) {
                                    AnalyticsPeriod.WEEKLY -> selectedDate.minusWeeks(1)
                                    AnalyticsPeriod.MONTHLY -> selectedDate.minusMonths(1)
                                    else -> selectedDate.minusDays(1)
                                }
                            )
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = MinimalistBlack,
                                shape = RoundedCornerShape(0.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Previous",
                            tint = MinimalistWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Text(
                        text = formatDateRange(selectedDate, selectedPeriod),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = MinimalistBlack,
                            fontSize = 16.sp
                        )
                    )
                    
                    IconButton(
                        onClick = { 
                            onDateSelected(
                                when (selectedPeriod) {
                                    AnalyticsPeriod.WEEKLY -> selectedDate.plusWeeks(1)
                                    AnalyticsPeriod.MONTHLY -> selectedDate.plusMonths(1)
                                    else -> selectedDate.plusDays(1)
                                }
                            )
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = MinimalistBlack,
                                shape = RoundedCornerShape(0.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Next",
                            tint = MinimalistWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricsGrid(
    usageStats: UsageStats,
    selectedPeriod: AnalyticsPeriod
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                icon = Icons.Default.Schedule,
                title = "Total Usage",
                value = usageStats.totalUsageTime,
                modifier = Modifier.weight(1f)
            )
            
            MetricCard(
                icon = Icons.Default.BarChart,
                title = "Average Usage",
                value = usageStats.averageUsageTime,
                modifier = Modifier.weight(1f)
            )
        }
        
        // Bottom Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                title = "Highest Usage",
                value = usageStats.peakUsageInfo,
                modifier = Modifier.weight(1f)
            )
            
            MetricCard(
                icon = Icons.Default.Apps,
                title = "Most Used App",
                value = usageStats.mostUsedAppInfo.split(" - ").firstOrNull() ?: "None",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MetricCard(
    icon: ImageVector,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Icon
                Surface(
                    modifier = Modifier.size(32.dp),
                    color = MinimalistBlack,
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MinimalistWhite,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Normal,
                        color = MinimalistBlack,
                        fontSize = 12.sp
                    )
                )
                
                // Value
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = MinimalistBlack,
                        fontSize = 14.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun WeeklyProgressChart(selectedDate: LocalDate) {
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
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Weekly Progress",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Normal,
                        color = MinimalistBlack,
                        fontSize = 18.sp
                    )
                )
                
                Text(
                    text = "Daily usage pattern for the selected week",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MinimalistBlack,
                        fontSize = 14.sp
                    )
                )
                
                // Weekly chart display
                WeeklyChart(selectedDate = selectedDate)
            }
        }
    }
}

@Composable
private fun WeeklyChart(selectedDate: LocalDate) {
    // Mock data for the week - would be replaced with real data
    val weekdays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val usageData = listOf(2.5f, 3.2f, 1.8f, 2.9f, 3.1f, 4.2f, 2.1f) // Hours of usage
    val maxUsage = 4.5f
    val limitHours = 3.0f
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        weekdays.forEachIndexed { index, day ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                val usage = usageData[index]
                val exceeded = usage > limitHours
                val heightRatio = usage / maxUsage
                
                Box(
                    modifier = Modifier
                        .height((80 * heightRatio).dp.coerceAtLeast(8.dp))
                        .width(24.dp)
                        .background(
                            color = if (exceeded) MinimalistBlack else MinimalistBlack.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(0.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (exceeded) {
                        Text(
                            text = "×",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MinimalistWhite,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MinimalistBlack,
                        fontSize = 10.sp
                    )
                )
                
                Text(
                    text = "${usage}h",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MinimalistBlack,
                        fontSize = 9.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun MonthlyCalendarView(selectedDate: LocalDate) {
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
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Usage Overview",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Normal,
                        color = MinimalistBlack,
                        fontSize = 18.sp
                    )
                )
                
                // Calendar grid (simplified)
                CalendarGrid(selectedDate = selectedDate)
            }
        }
    }
}

@Composable
private fun CalendarGrid(selectedDate: LocalDate) {
    val firstDayOfMonth = selectedDate.withDayOfMonth(1)
    val lastDayOfMonth = selectedDate.withDayOfMonth(selectedDate.lengthOfMonth())
    val startDay = firstDayOfMonth.dayOfWeek.value % 7
    
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Days of week header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = MinimalistBlack,
                        fontSize = 12.sp
                    ),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
        
        // Calendar days
        var dayCounter = 1
        for (week in 0..5) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (day in 0..6) {
                    val showDay = if (week == 0) day >= startDay else dayCounter <= lastDayOfMonth.dayOfMonth
                    
                    if (showDay && dayCounter <= lastDayOfMonth.dayOfMonth) {
                        CalendarDayCell(
                            day = dayCounter,
                            isToday = dayCounter == LocalDate.now().dayOfMonth && 
                                     selectedDate.month == LocalDate.now().month,
                            usageLevel = 0, // TODO: Calculate real usage level
                            modifier = Modifier.weight(1f)
                        )
                        dayCounter++
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            
            if (dayCounter > lastDayOfMonth.dayOfMonth) break
        }
    }
}

@Composable
private fun CalendarDayCell(
    day: Int,
    isToday: Boolean,
    usageLevel: Int,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (usageLevel) {
        0 -> MinimalistWhite
        1 -> MinimalistBlack.copy(alpha = 0.2f)
        2 -> MinimalistBlack.copy(alpha = 0.5f)
        3 -> MinimalistBlack.copy(alpha = 0.8f)
        else -> MinimalistBlack
    }
    
    Surface(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp),
        color = backgroundColor,
        shape = RoundedCornerShape(0.dp),
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                    color = if (usageLevel < 2) MinimalistBlack else MinimalistWhite,
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Composable
private fun ErrorMessage(error: String) {
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
                text = error,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MinimalistWhite
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun getPeriodSubtitle(period: AnalyticsPeriod, context: String): String {
    return when (period) {
        AnalyticsPeriod.DAILY -> "Today's $context"
        AnalyticsPeriod.WEEKLY -> "This week's $context"
        AnalyticsPeriod.MONTHLY -> "This month's $context"
    }
}

private fun getPeakSubtitle(period: AnalyticsPeriod): String {
    return when (period) {
        AnalyticsPeriod.DAILY -> "Time of day with highest usage"
        AnalyticsPeriod.WEEKLY -> "Day of week with highest usage"
        AnalyticsPeriod.MONTHLY -> "Day of month with highest usage"
    }
}

private fun formatDateRange(date: LocalDate, period: AnalyticsPeriod): String {
    return when (period) {
        AnalyticsPeriod.WEEKLY -> {
            val startOfWeek = date.minusDays(date.dayOfWeek.value - 1L)
            val endOfWeek = startOfWeek.plusDays(6)
            "${startOfWeek.format(DateTimeFormatter.ofPattern("MMM d"))} - ${endOfWeek.format(DateTimeFormatter.ofPattern("MMM d"))}"
        }
        AnalyticsPeriod.MONTHLY -> {
            date.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
        }
        else -> {
            date.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
        }
    }
}