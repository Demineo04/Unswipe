package com.unswipe.android.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unswipe.android.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnlocksDetailScreen(
    onNavigateBack: () -> Unit
) {
    // Use consistent mock data
    val unlockEvents = MockDataManager.mockUnlockEvents
    val hourlyUnlocks = MockDataManager.hourlyUnlockPattern

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
                        text = "Unlocks Today",
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
                    UnlocksSummaryCard(
                        totalUnlocks = unlockEvents.size,
                        averagePerHour = unlockEvents.size / 24f,
                        peakHour = hourlyUnlocks.maxByOrNull { it.value }?.key ?: 0
                    )
                }

                item {
                    HourlyUnlocksChart(hourlyUnlocks = hourlyUnlocks)
                }

                item {
                    Text(
                        text = "Unlock Timeline",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = UnswipeTextPrimary
                        ),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(unlockEvents.reversed()) { unlock ->
                    UnlockEventCard(unlock = unlock)
                }
            }
        }
    }
}

@Composable
private fun UnlocksSummaryCard(
    totalUnlocks: Int,
    averagePerHour: Float,
    peakHour: Int
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
                    imageVector = Icons.Default.Phone,
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
                SummaryItem(
                    label = "Total Unlocks",
                    value = totalUnlocks.toString(),
                    modifier = Modifier.weight(1f)
                )
                SummaryItem(
                    label = "Per Hour Avg",
                    value = String.format("%.1f", averagePerHour),
                    modifier = Modifier.weight(1f)
                )
                SummaryItem(
                    label = "Peak Hour",
                    value = String.format("%02d:00", peakHour),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SummaryItem(
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
private fun HourlyUnlocksChart(hourlyUnlocks: Map<Int, Int>) {
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
                text = "Hourly Pattern",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = UnswipeTextPrimary
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val maxUnlocks = hourlyUnlocks.values.maxOrNull() ?: 1
            val maxBarHeight = 60.dp

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                (0..23).forEach { hour ->
                    val unlocks = hourlyUnlocks[hour] ?: 0
                    val barHeight = if (maxUnlocks > 0) {
                        maxBarHeight * (unlocks.toFloat() / maxUnlocks)
                    } else 0.dp

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(8.dp)
                                .height(barHeight)
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            UnswipePrimary,
                                            UnswipePrimary.copy(alpha = 0.6f)
                                        )
                                    ),
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        if (hour % 4 == 0) {
                            Text(
                                text = String.format("%02d", hour),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = UnswipeTextSecondary,
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

@Composable
private fun UnlockEventCard(unlock: com.unswipe.android.ui.details.UnlockEvent) {
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
                        color = UnswipePrimary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    tint = UnswipePrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = unlock.context,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = UnswipeTextPrimary
                    )
                )
                Text(
                    text = unlock.time.format(DateTimeFormatter.ofPattern("HH:mm")),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = UnswipeTextSecondary
                    )
                )
            }
        }
    }
}

 