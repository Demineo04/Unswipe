package com.unswipe.android.ui.analytics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Target
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unswipe.android.ui.components.MonthlyStatsCard
import com.unswipe.android.ui.components.MonthlyUsageChart
import com.unswipe.android.ui.components.MonthlyUsageSummary
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyAnalyticsScreen(
    onNavigateBack: () -> Unit,
    viewModel: MonthlyAnalyticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Monthly Analytics") },
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                
                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error: ${uiState.error}",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                
                else -> {
                    MonthlyAnalyticsContent(
                        state = uiState,
                        onDaySelected = { day ->
                            viewModel.selectDay(day)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthlyAnalyticsContent(
    state: MonthlyAnalyticsUiState,
    onDaySelected: (MonthlyUsageSummary) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Month selector and overview
        MonthlyOverviewCard(state = state)
        
        // Monthly stats cards
        MonthlyStatsSection(state = state)
        
        // Monthly usage chart
        MonthlyUsageChart(
            monthlyProgress = state.monthlyData,
            onDaySelected = onDaySelected
        )
        
        // Monthly insights and achievements
        MonthlyInsightsSection(state = state)
        
        // Comparison with previous month
        MonthlyComparisonSection(state = state)
        
        // Monthly goals progress
        MonthlyGoalsSection(state = state)
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun MonthlyOverviewCard(
    state: MonthlyAnalyticsUiState
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = state.currentMonth,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "${state.daysInMonth} days • ${state.daysCompleted} completed",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = state.monthlyTotal,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "total usage",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun MonthlyStatsSection(
    state: MonthlyAnalyticsUiState
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MonthlyStatsCard(
            modifier = Modifier.weight(1f),
            label = "Daily Average",
            currentValue = state.dailyAverage,
            previousValue = state.previousDailyAverage,
            percentageChange = state.dailyAverageChange,
            isImprovement = false // Less usage is better
        )
        
        MonthlyStatsCard(
            modifier = Modifier.weight(1f),
            label = "Best Day",
            currentValue = state.bestDay,
            previousValue = state.previousBestDay,
            percentageChange = state.bestDayChange,
            isImprovement = false
        )
    }
    
    Spacer(modifier = Modifier.height(8.dp))
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MonthlyStatsCard(
            modifier = Modifier.weight(1f),
            label = "Sessions",
            currentValue = state.totalSessions.toString(),
            previousValue = state.previousTotalSessions?.toString(),
            percentageChange = state.sessionsChange,
            isImprovement = false
        )
        
        MonthlyStatsCard(
            modifier = Modifier.weight(1f),
            label = "Goal Days",
            currentValue = "${state.goalDays}/${state.daysCompleted}",
            previousValue = state.previousGoalDays?.let { "${it}/${state.previousDaysCompleted}" },
            percentageChange = state.goalDaysChange,
            isImprovement = true // More goal days is better
        )
    }
}

@Composable
private fun MonthlyInsightsSection(
    state: MonthlyAnalyticsUiState
) {
    if (state.insights.isNotEmpty()) {
        Text(
            text = "Monthly Insights",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        
        state.insights.forEach { insight ->
            MonthlyInsightCard(insight = insight)
        }
    }
}

@Composable
private fun MonthlyInsightCard(
    insight: MonthlyInsight
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (insight.type) {
                MonthlyInsightType.ACHIEVEMENT -> MaterialTheme.colorScheme.secondaryContainer
                MonthlyInsightType.IMPROVEMENT -> MaterialTheme.colorScheme.tertiaryContainer
                MonthlyInsightType.PATTERN -> MaterialTheme.colorScheme.surfaceVariant
                MonthlyInsightType.CHALLENGE -> MaterialTheme.colorScheme.errorContainer
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = when (insight.type) {
                    MonthlyInsightType.ACHIEVEMENT -> Icons.Default.Star
                    MonthlyInsightType.IMPROVEMENT -> Icons.Default.TrendingUp
                    MonthlyInsightType.PATTERN -> Icons.Default.CalendarMonth
                    MonthlyInsightType.CHALLENGE -> Icons.Default.Target
                },
                contentDescription = null,
                tint = when (insight.type) {
                    MonthlyInsightType.ACHIEVEMENT -> MaterialTheme.colorScheme.onSecondaryContainer
                    MonthlyInsightType.IMPROVEMENT -> MaterialTheme.colorScheme.onTertiaryContainer
                    MonthlyInsightType.PATTERN -> MaterialTheme.colorScheme.onSurfaceVariant
                    MonthlyInsightType.CHALLENGE -> MaterialTheme.colorScheme.onErrorContainer
                },
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column {
                Text(
                    text = insight.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = when (insight.type) {
                        MonthlyInsightType.ACHIEVEMENT -> MaterialTheme.colorScheme.onSecondaryContainer
                        MonthlyInsightType.IMPROVEMENT -> MaterialTheme.colorScheme.onTertiaryContainer
                        MonthlyInsightType.PATTERN -> MaterialTheme.colorScheme.onSurfaceVariant
                        MonthlyInsightType.CHALLENGE -> MaterialTheme.colorScheme.onErrorContainer
                    }
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = insight.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = when (insight.type) {
                        MonthlyInsightType.ACHIEVEMENT -> MaterialTheme.colorScheme.onSecondaryContainer
                        MonthlyInsightType.IMPROVEMENT -> MaterialTheme.colorScheme.onTertiaryContainer
                        MonthlyInsightType.PATTERN -> MaterialTheme.colorScheme.onSurfaceVariant
                        MonthlyInsightType.CHALLENGE -> MaterialTheme.colorScheme.onErrorContainer
                    }.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun MonthlyComparisonSection(
    state: MonthlyAnalyticsUiState
) {
    if (state.hasComparisonData) {
        Text(
            text = "Month-over-Month Comparison",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                ComparisonItem(
                    label = "Total Usage",
                    currentValue = state.monthlyTotal,
                    previousValue = state.previousMonthTotal ?: "N/A",
                    change = state.totalUsageChange
                )
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                ComparisonItem(
                    label = "Daily Average",
                    currentValue = state.dailyAverage,
                    previousValue = state.previousDailyAverage ?: "N/A",
                    change = state.dailyAverageChange
                )
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                ComparisonItem(
                    label = "Days Over Limit",
                    currentValue = "${state.daysOverLimit}",
                    previousValue = "${state.previousDaysOverLimit ?: 0}",
                    change = state.daysOverLimitChange
                )
            }
        }
    }
}

@Composable
private fun ComparisonItem(
    label: String,
    currentValue: String,
    previousValue: String,
    change: Float?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = "Current: $currentValue",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Previous: $previousValue",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        
        if (change != null) {
            val isImprovement = change < 0 // Negative change is improvement for usage
            val changeColor = if (isImprovement) Color(0xFF4CAF50) else Color(0xFFF44336)
            
            Text(
                text = "${if (change > 0) "+" else ""}${change.toInt()}%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = changeColor
            )
        }
    }
}

@Composable
private fun MonthlyGoalsSection(
    state: MonthlyAnalyticsUiState
) {
    Text(
        text = "Monthly Goals Progress",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 4.dp)
    )
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Monthly usage goal progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Usage Goal",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${state.monthlyTotal} of ${state.monthlyGoal}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                
                Text(
                    text = "${(state.goalProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (state.goalProgress <= 1.0f) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = state.goalProgress.coerceAtMost(1.0f),
                modifier = Modifier.fillMaxWidth(),
                color = if (state.goalProgress <= 1.0f) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = if (state.goalProgress <= 1.0f) {
                    "Great progress! You're ${((1.0f - state.goalProgress) * 100).toInt()}% under your monthly goal."
                } else {
                    "You've exceeded your monthly goal by ${((state.goalProgress - 1.0f) * 100).toInt()}%."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// Data classes for the monthly analytics
data class MonthlyAnalyticsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentMonth: String = "",
    val daysInMonth: Int = 0,
    val daysCompleted: Int = 0,
    val monthlyTotal: String = "",
    val dailyAverage: String = "",
    val bestDay: String = "",
    val totalSessions: Int = 0,
    val goalDays: Int = 0,
    val daysOverLimit: Int = 0,
    val monthlyData: List<MonthlyUsageSummary> = emptyList(),
    val insights: List<MonthlyInsight> = emptyList(),
    val hasComparisonData: Boolean = false,
    val previousMonthTotal: String? = null,
    val previousDailyAverage: String? = null,
    val previousBestDay: String? = null,
    val previousTotalSessions: Int? = null,
    val previousGoalDays: Int? = null,
    val previousDaysCompleted: Int = 0,
    val previousDaysOverLimit: Int? = null,
    val totalUsageChange: Float? = null,
    val dailyAverageChange: Float? = null,
    val bestDayChange: Float? = null,
    val sessionsChange: Float? = null,
    val goalDaysChange: Float? = null,
    val daysOverLimitChange: Float? = null,
    val monthlyGoal: String = "",
    val goalProgress: Float = 0f
)

data class MonthlyInsight(
    val type: MonthlyInsightType,
    val title: String,
    val description: String
)

enum class MonthlyInsightType {
    ACHIEVEMENT,
    IMPROVEMENT,
    PATTERN,
    CHALLENGE
}