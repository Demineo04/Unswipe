package com.unswipe.android.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unswipe.android.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLaunchesDetailScreen(
    onNavigateBack: () -> Unit
) {
    // Use consistent mock data
    val appLaunches = MockDataManager.mockAppLaunches
    val categoryBreakdown = MockDataManager.categoryBreakdown
    val totalDuration = MockDataManager.totalScreenTime.toLong()
    
    val categoryStats = categoryBreakdown.mapValues { (category, minutes) ->
        CategoryStats(
            launches = appLaunches.count { it.category == category },
            totalDuration = minutes.toLong(),
            percentage = minutes.toFloat() / totalDuration
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        UnswipeBlack,
                        UnswipeSurface
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar
            TopAppBar(
                title = {
                    Text(
                        text = "App Launches",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = UnswipeTextPrimary
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = UnswipeTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = UnswipeBlack
                )
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    AppLaunchesSummaryCard(
                        totalLaunches = appLaunches.size,
                        totalDuration = totalDuration,
                        mostUsedApp = appLaunches.groupBy { it.appName }
                            .maxByOrNull { it.value.sumOf { app -> app.durationMinutes } }?.key ?: "None"
                    )
                }

                item {
                    CategoryBreakdownCard(categoryStats = categoryStats)
                }

                item {
                    Text(
                        text = "Launch Timeline",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = UnswipeTextPrimary
                        ),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(appLaunches.reversed()) { launch ->
                    AppLaunchEventCard(launch = launch)
                }
            }
        }
    }
}

data class CategoryStats(
    val launches: Int,
    val totalDuration: Long,
    val percentage: Float
)

@Composable
private fun AppSummaryItem(
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
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = UnswipePrimary
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = UnswipeTextSecondary
            )
        )
    }
}

@Composable
private fun AppLaunchesSummaryCard(
    totalLaunches: Int,
    totalDuration: Long,
    mostUsedApp: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = UnswipeCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Apps,
                    contentDescription = null,
                    tint = UnswipePrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Today's Summary",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = UnswipeTextPrimary
                    )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AppSummaryItem(
                    label = "App Launches",
                    value = totalLaunches.toString(),
                    modifier = Modifier.weight(1f)
                )
                AppSummaryItem(
                    label = "Total Time",
                    value = formatDuration(totalDuration),
                    modifier = Modifier.weight(1f)
                )
                AppSummaryItem(
                    label = "Most Used",
                    value = mostUsedApp.take(8),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun CategoryBreakdownCard(categoryStats: Map<String, CategoryStats>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = UnswipeCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Category Breakdown",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = UnswipeTextPrimary
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            categoryStats.forEach { (category, stats) ->
                CategoryBreakdownItem(
                    category = category,
                    stats = stats,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun CategoryBreakdownItem(
    category: String,
    stats: CategoryStats,
    modifier: Modifier = Modifier
) {
    val categoryColor = when (category) {
        "Social" -> Color(0xFF1DA1F2)
        "Entertainment" -> Color(0xFFFF0000)
        "Productivity" -> Color(0xFF34A853)
        "Communication" -> Color(0xFF4CAF50)
        else -> Color(0xFF757575)
    }
    
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = categoryColor,
                            shape = RoundedCornerShape(2.dp)
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = category,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = UnswipeTextPrimary
                    )
                )
            }
            
            Text(
                text = "${(stats.percentage * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = UnswipeTextSecondary
                )
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        LinearProgressIndicator(
            progress = { stats.percentage },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = categoryColor,
            trackColor = UnswipeGray.copy(alpha = 0.2f),
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "${stats.launches} launches • ${stats.totalDuration}m",
            style = MaterialTheme.typography.bodySmall.copy(
                color = UnswipeTextSecondary
            )
        )
    }
}

@Composable
private fun AppLaunchEventCard(launch: com.unswipe.android.ui.details.AppLaunchEvent) {
    val categoryColor = when (launch.category) {
        "Social" -> Color(0xFF1DA1F2)
        "Entertainment" -> Color(0xFFFF0000)
        "Productivity" -> Color(0xFF34A853)
        "Communication" -> Color(0xFF4CAF50)
        else -> Color(0xFF757575)
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = UnswipeCard.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = categoryColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = launch.appName.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = categoryColor
                    )
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = launch.appName,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = UnswipeTextPrimary
                    )
                )
                Text(
                    text = "${launch.time.format(DateTimeFormatter.ofPattern("HH:mm"))} • ${launch.durationMinutes}m",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = UnswipeTextSecondary
                    )
                )
            }
            
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = categoryColor.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = launch.category,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = categoryColor,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

private fun formatDuration(minutes: Long): String {
    return when {
        minutes < 60 -> "${minutes}m"
        minutes < 1440 -> "${minutes / 60}h ${minutes % 60}m"
        else -> "${minutes / 1440}d ${(minutes % 1440) / 60}h"
    }
}

 