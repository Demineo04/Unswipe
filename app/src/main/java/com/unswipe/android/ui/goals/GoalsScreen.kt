package com.unswipe.android.ui.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unswipe.android.ui.theme.*

data class Goal(
    val icon: ImageVector,
    val iconEmoji: String, // For emoji display
    val title: String,
    val frequency: String,
    val backgroundColor: Color,
    val progress: Float = 0f
)

@Composable
fun GoalsPlaceholderScreen() {
    val goals = listOf(
        Goal(
            icon = Icons.Default.Timer,
            iconEmoji = "⏰",
            title = "Be Active",
            frequency = "2 times a day",
            backgroundColor = Color(0xFF9B7EF7)
        ),
        Goal(
            icon = Icons.Default.DirectionsWalk,
            iconEmoji = "👟",
            title = "Go for a walk",
            frequency = "5 times a week",
            backgroundColor = Color(0xFFF7A593)
        ),
        Goal(
            icon = Icons.Default.RemoveRedEye,
            iconEmoji = "👓",
            title = "Read at night",
            frequency = "3 times a week",
            backgroundColor = Color(0xFFF7D573)
        ),
        Goal(
            icon = Icons.Default.Restaurant,
            iconEmoji = "🍽️",
            title = "Cook dinner",
            frequency = "1 times a day",
            backgroundColor = Color(0xFF7CC7F7)
        ),
        Goal(
            icon = Icons.Default.Work,
            iconEmoji = "📁",
            title = "Organize Work",
            frequency = "1 times a day",
            backgroundColor = Color(0xFF7EF7B3)
        ),
        Goal(
            icon = Icons.Default.Book,
            iconEmoji = "📖",
            title = "Practice French",
            frequency = "2 times a week",
            backgroundColor = Color(0xFF7EF7D5)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(UnswipeBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Simplified Header
            GoalsHeader()
            
            // Goals Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(bottom = 100.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(goals) { goal ->
                    GoalCard(goal = goal)
                }
            }
        }
    }
}

@Composable
private fun GoalsHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Goals",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = UnswipeTextPrimary,
                fontSize = 28.sp
            )
        )
        
        // Optional: Add button on the right
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = { /* More options */ }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = UnswipeTextPrimary
                )
            }
        }
    }
}

@Composable
private fun GoalCard(goal: Goal) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        colors = CardDefaults.cardColors(
            containerColor = goal.backgroundColor
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top section with icon and menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Icon background
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = goal.iconEmoji,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = 24.sp
                        )
                    )
                }
                
                // Menu icon
                IconButton(
                    onClick = { /* Goal options */ },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            // Bottom section with title and frequency
            Column {
                Text(
                    text = goal.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 18.sp
                    )
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = goal.frequency,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp
                    )
                )
            }
        }
    }
}