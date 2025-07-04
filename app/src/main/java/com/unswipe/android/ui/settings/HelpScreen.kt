package com.unswipe.android.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unswipe.android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    onNavigateBack: () -> Unit
) {
    val faqItems = listOf(
        FaqItem(
            question = "How does Unswipe help me?",
            answer = "Unswipe helps you manage your social media usage by tracking time spent on apps like Instagram, TikTok, and YouTube. It provides gentle reminders when you're approaching your daily limits."
        ),
        FaqItem(
            question = "What permissions does Unswipe need?",
            answer = "Unswipe requires Usage Stats permission to track app usage and Accessibility permission to show confirmation dialogs when you open monitored apps."
        ),
        FaqItem(
            question = "How do I set my daily limits?",
            answer = "Go to Settings → Daily Limit to set your preferred usage time. You can adjust this anytime based on your goals."
        ),
        FaqItem(
            question = "What is Premium?",
            answer = "Premium offers advanced features like AI-powered smart nudges, detailed analytics, custom app blocking, and more for $4.99/month with a 7-day free trial."
        ),
        FaqItem(
            question = "How do I block additional apps?",
            answer = "Go to Settings → App Blocker to manage which apps you want to monitor. Premium users can block any app beyond the default social media apps."
        ),
        FaqItem(
            question = "Is my data private?",
            answer = "Yes! Your usage data is stored locally on your device. We don't track or sell your personal information."
        )
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help & FAQ", color = UnswipeTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = UnswipeTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = UnswipeBlack
                )
            )
        },
        containerColor = UnswipeBlack
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(UnswipeBlack, UnswipeSurface)
                    )
                )
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = UnswipePrimary.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = UnswipePrimary,
                                modifier = Modifier.size(48.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Column {
                                Text(
                                    text = "Welcome to Unswipe!",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        color = UnswipeTextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "Find answers to common questions below",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = UnswipeTextSecondary
                                    )
                                )
                            }
                        }
                    }
                }
                
                items(faqItems) { item ->
                    FaqCard(item)
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = UnswipeCard
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = UnswipePrimary,
                                modifier = Modifier.size(32.dp)
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Text(
                                text = "Still need help?",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = UnswipeTextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            
                            Text(
                                text = "Contact us at support@unswipe.app",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = UnswipeTextSecondary
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
private fun FaqCard(item: FaqItem) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = UnswipeCard
        ),
        shape = RoundedCornerShape(12.dp),
        onClick = { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.question,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = UnswipeTextPrimary,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.weight(1f)
                )
                
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = UnswipeTextSecondary
                )
            }
            
            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = item.answer,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = UnswipeTextSecondary
                    )
                )
            }
        }
    }
}

data class FaqItem(
    val question: String,
    val answer: String
)