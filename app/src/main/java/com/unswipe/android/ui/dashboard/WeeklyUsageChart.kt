package com.unswipe.android.ui.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.unswipe.android.ui.theme.MinimalistBlack
import kotlin.math.max

@Composable
fun WeeklyUsageChart(
    weeklyProgress: List<DailyUsageSummary>,
    modifier: Modifier = Modifier
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
            Text(
                text = "Weekly Progress",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (weeklyProgress.isNotEmpty()) {
                UsageBarChart(
                    data = weeklyProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Day labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    weeklyProgress.forEach { day ->
                        Text(
                            text = day.dayLabel,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (day.isToday) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            },
                            fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            } else {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No data available yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
private fun UsageBarChart(
    data: List<DailyUsageSummary>,
    modifier: Modifier = Modifier
) {
    val maxUsage = data.maxOfOrNull { it.usagePercentage } ?: 1f
    val normalizedMax = max(maxUsage, 0.1f) // Ensure minimum height for visualization
    
    Canvas(modifier = modifier) {
        val barWidth = size.width / data.size
        val maxBarHeight = size.height * 0.8f // Leave space for padding
        
        data.forEachIndexed { index, day ->
            val barHeight = (day.usagePercentage / normalizedMax) * maxBarHeight
            val left = index * barWidth + barWidth * 0.2f
            val right = (index + 1) * barWidth - barWidth * 0.2f
            val top = size.height - barHeight
            val bottom = size.height
            
            // Determine bar color based on usage level
                    val barColor = MinimalistBlack // All bars use pure black for minimalism
            
            // Draw bar with rounded corners
            drawRoundedBar(
                left = left,
                top = top,
                right = right,
                bottom = bottom,
                color = barColor,
                cornerRadius = 4.dp.toPx()
            )
            
            // Highlight today's bar
            if (day.isToday) {
                drawRoundedBar(
                    left = left - 2.dp.toPx(),
                    top = top - 2.dp.toPx(),
                    right = right + 2.dp.toPx(),
                    bottom = bottom,
                    color = barColor.copy(alpha = 0.3f),
                    cornerRadius = 6.dp.toPx()
                )
            }
        }
    }
}

private fun DrawScope.drawRoundedBar(
    left: Float,
    top: Float,
    right: Float,
    bottom: Float,
    color: Color,
    cornerRadius: Float
) {
    val path = Path().apply {
        // Start from bottom-left
        moveTo(left, bottom)
        // Line to top-left, then arc to top-right
        lineTo(left, top + cornerRadius)
        arcTo(
            rect = androidx.compose.ui.geometry.Rect(
                left = left,
                top = top,
                right = left + cornerRadius * 2,
                bottom = top + cornerRadius * 2
            ),
            startAngleDegrees = 180f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )
        // Line across top
        lineTo(right - cornerRadius, top)
        // Arc to top-right corner
        arcTo(
            rect = androidx.compose.ui.geometry.Rect(
                left = right - cornerRadius * 2,
                top = top,
                right = right,
                bottom = top + cornerRadius * 2
            ),
            startAngleDegrees = 270f,
            sweepAngleDegrees = 90f,
            forceMoveTo = false
        )
        // Line down to bottom-right
        lineTo(right, bottom)
        // Close the path
        close()
    }
    
    drawPath(path, color)
}