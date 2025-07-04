package com.unswipe.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.unswipe.android.ui.theme.*
import kotlin.math.abs

@Composable
fun ModernSlidingTimePicker(
    modifier: Modifier = Modifier,
    hour: Int,
    minute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
    label: String = "Select Time"
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = UnswipeCard),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = UnswipeTextPrimary,
                    fontSize = 20.sp
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Selected time display
            Text(
                text = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = UnswipePrimary,
                    fontSize = 48.sp
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hour picker
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Hour",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = UnswipeTextSecondary,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    SlidingNumberPicker(
                        value = hour,
                        onValueChange = onHourChange,
                        range = 0..23,
                        modifier = Modifier.width(80.dp)
                    )
                }
                
                Text(
                    text = ":",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = UnswipeTextSecondary,
                        fontSize = 32.sp
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                // Minute picker
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Minute",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = UnswipeTextSecondary,
                            fontWeight = FontWeight.SemiBold
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    SlidingNumberPicker(
                        value = minute,
                        onValueChange = onMinuteChange,
                        range = 0..59,
                        step = 5, // 5-minute increments for better UX
                        modifier = Modifier.width(80.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SlidingNumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    step: Int = 1,
    modifier: Modifier = Modifier
) {
    val items = if (step == 1) {
        range.toList()
    } else {
        range.filter { it % step == 0 }
    }
    
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = maxOf(0, items.indexOf(value) - 1)
    )
    
    LaunchedEffect(value) {
        val index = items.indexOf(value)
        if (index >= 0) {
            listState.animateScrollToItem(maxOf(0, index - 1))
        }
    }
    
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            delay(100) // Small delay to ensure scroll has settled
            val firstVisibleIndex = listState.firstVisibleItemIndex
            val centerIndex = firstVisibleIndex + 1
            if (centerIndex in items.indices) {
                onValueChange(items[centerIndex])
            }
        }
    }
    
    Box(
        modifier = modifier.height(160.dp),
        contentAlignment = Alignment.Center
    ) {
        // Selection indicator background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            UnswipePrimary.copy(alpha = 0.1f),
                            UnswipePrimary.copy(alpha = 0.2f),
                            UnswipePrimary.copy(alpha = 0.1f)
                        )
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
        )
        
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = 52.dp) // Space for fade effect
        ) {
            items(items.size) { index ->
                val item = items[index]
                val centerIndex = listState.firstVisibleItemIndex + 1
                val isSelected = index == centerIndex
                val distance = abs(index - centerIndex)
                
                val alpha = when (distance) {
                    0 -> 1f
                    1 -> 0.6f
                    else -> 0.3f
                }
                
                val scale = when (distance) {
                    0 -> 1.0f
                    1 -> 0.8f
                    else -> 0.6f
                }
                
                Text(
                    text = item.toString().padStart(2, '0'),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) UnswipePrimary else UnswipeTextSecondary,
                        fontSize = (24 * scale).sp
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .alpha(alpha)
                )
            }
        }
        
        // Top fade overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .align(Alignment.TopCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            UnswipeCard,
                            Color.Transparent
                        )
                    )
                )
        )
        
        // Bottom fade overlay  
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            UnswipeCard
                        )
                    )
                )
        )
    }
}

// Legacy component for backward compatibility
@Composable
fun CustomTimePicker(
    modifier: Modifier = Modifier,
    hour: Int,
    minute: Int,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit
) {
    ModernSlidingTimePicker(
        modifier = modifier,
        hour = hour,
        minute = minute,
        onHourChange = onHourChange,
        onMinuteChange = onMinuteChange
    )
} 