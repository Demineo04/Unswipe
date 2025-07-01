package com.unswipe.android.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class MonthlyUsageSummary(
    val date: LocalDate,
    val usageMinutes: Int,
    val usagePercentage: Float, // 0.0 to 1.0+ (can exceed 1.0 if over limit)
    val sessionCount: Int,
    val isToday: Boolean,
    val dayOfMonth: Int,
    val isWeekend: Boolean
)

@Composable
fun MonthlyUsageChart(
    monthlyProgress: List<MonthlyUsageSummary>,
    modifier: Modifier = Modifier,
    onDaySelected: ((MonthlyUsageSummary) -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Monthly Progress",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                if (monthlyProgress.isNotEmpty()) {
                    val currentMonth = monthlyProgress.first().date.format(
                        DateTimeFormatter.ofPattern("MMMM yyyy")
                    )
                    Text(
                        text = currentMonth,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (monthlyProgress.isNotEmpty()) {
                // Monthly stats summary
                val totalUsage = monthlyProgress.sumOf { it.usageMinutes }
                val avgDailyUsage = totalUsage / monthlyProgress.size
                val daysOverLimit = monthlyProgress.count { it.usagePercentage >= 1.0f }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MonthlyStatItem(
                        label = "Total",
                        value = formatHoursMinutes(totalUsage)
                    )
                    MonthlyStatItem(
                        label = "Daily Avg",
                        value = formatHoursMinutes(avgDailyUsage)
                    )
                    MonthlyStatItem(
                        label = "Over Limit",
                        value = "$daysOverLimit days"
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Monthly chart - scrollable for longer months
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    items(monthlyProgress) { day ->
                        MonthlyDayBar(
                            day = day,
                            maxUsage = monthlyProgress.maxOfOrNull { it.usagePercentage } ?: 1f,
                            onClick = { onDaySelected?.invoke(day) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Legend
                MonthlyChartLegend()
                
            } else {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No data available yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Text(
                            text = "Start using the app to see your monthly progress",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthlyStatItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun MonthlyDayBar(
    day: MonthlyUsageSummary,
    maxUsage: Float,
    onClick: () -> Unit
) {
    val normalizedHeight = (day.usagePercentage / max(maxUsage, 0.1f)).coerceIn(0f, 1f)
    val barHeight = (80 * normalizedHeight).dp
    
    // Determine bar color based on usage level
    val barColor = when {
        day.usagePercentage >= 1.0f -> Color(0xFFE53E3E) // Red - over limit
        day.usagePercentage >= 0.8f -> Color(0xFFF56500) // Orange - approaching limit
        day.usagePercentage >= 0.5f -> Color(0xFFD69E2E) // Yellow - moderate usage
        else -> Color(0xFF38A169) // Green - good usage
    }
    
    val backgroundColor = if (day.isWeekend) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    } else {
        Color.Transparent
    }
    
    Column(
        modifier = Modifier
            .width(24.dp)
            .height(120.dp)
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Usage bar
        Box(
            modifier = Modifier
                .width(16.dp)
                .height(barHeight.coerceAtLeast(2.dp))
                .background(
                    color = barColor,
                    shape = RoundedCornerShape(
                        topStart = 3.dp,
                        topEnd = 3.dp,
                        bottomStart = 1.dp,
                        bottomEnd = 1.dp
                    )
                )
        )
        
        // Today indicator
        if (day.isToday) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(2.dp)
                    )
            )
        } else {
            Spacer(modifier = Modifier.height(4.dp))
        }
        
        // Day number
        Text(
            text = day.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
            color = if (day.isToday) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            },
            fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun MonthlyChartLegend() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LegendItem(color = Color(0xFF38A169), label = "Good")
        LegendItem(color = Color(0xFFD69E2E), label = "Moderate")
        LegendItem(color = Color(0xFFF56500), label = "High")
        LegendItem(color = Color(0xFFE53E3E), label = "Over Limit")
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

private fun formatHoursMinutes(minutes: Int): String {
    val hours = minutes / 60
    val remainingMinutes = minutes % 60
    
    return when {
        hours > 0 && remainingMinutes > 0 -> "${hours}h ${remainingMinutes}m"
        hours > 0 -> "${hours}h"
        else -> "${remainingMinutes}m"
    }
}