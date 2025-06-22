package com.unswipe.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.unswipe.android.ui.dashboard.DailyUsageSummary

@Composable
fun HourlyUsageChart(
    modifier: Modifier = Modifier,
    data: List<DailyUsageSummary> // Using this for now, will create a specific model later
) {
    val maxUsage = data.maxOfOrNull { it.usagePercentage } ?: 1f

    Column(modifier = modifier) {
        Text(
            text = "Breakdown",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 24.dp, bottom = 16.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            data.forEach { summary ->
                Bar(
                    usagePercentage = summary.usagePercentage,
                    maxPercentage = maxUsage,
                    isToday = summary.isToday,
                    label = summary.dayLabel
                )
            }
        }
    }
}

@Composable
private fun RowScope.Bar(
    usagePercentage: Float,
    maxPercentage: Float,
    isToday: Boolean,
    label: String
) {
    val barHeight = (usagePercentage / maxPercentage).coerceIn(0f, 1f)
    val barColor = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary

    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .fillMaxHeight(barHeight)
                .clip(MaterialTheme.shapes.small)
                .background(barColor)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall
        )
    }
} 