package com.unswipe.android.ui.insights

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.unswipe.android.domain.model.*
import com.unswipe.android.ui.theme.*

@Composable
fun InsightsScreen(
    viewModel: InsightsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is InsightsUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = UnswipePrimary)
            }
        }
        
        is InsightsUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Failed to load insights",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = UnswipeTextPrimary
                        )
                    )
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = UnswipeTextSecondary
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { viewModel.refreshInsights() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = UnswipePrimary,
                            contentColor = UnswipeBlack
                        )
                    ) {
                        Text("Retry")
                    }
                }
            }
        }
        
        is InsightsUiState.Success -> {
            InsightsSuccessContent(
                uiState = state,
                viewModel = viewModel
            )
        }
    }
}

@Composable
private fun InsightsSuccessContent(
    uiState: InsightsUiState.Success,
    viewModel: InsightsViewModel
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    InsightsHeader()
                }
                
                IconButton(
                    onClick = { viewModel.refreshInsights() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh insights",
                        tint = UnswipePrimary
                    )
                }
            }
        }

        // Emotional Wellness Overview
        item {
            EmotionalWellnessCard(uiState.emotionalInsights)
        }

        // Usage Patterns
        if (uiState.usagePatterns.isNotEmpty()) {
            item {
                SectionTitle("Detected Patterns")
            }
            
            items(uiState.usagePatterns) { pattern ->
                UsagePatternCard(pattern)
            }
        }

        // Personalized Recommendations
        if (uiState.recommendations.isNotEmpty()) {
            item {
                SectionTitle("Personalized Recommendations")
            }
            
            items(uiState.recommendations) { recommendation ->
                RecommendationCard(recommendation) {
                    viewModel.implementRecommendation(recommendation)
                }
            }
        }

        // Risk Assessment
        uiState.riskAssessment?.let { risk ->
            item {
                SectionTitle("Risk Assessment")
            }
            
            item {
                RiskAssessmentCard(risk)
            }
        }

        // Trends
        if (uiState.trends.isNotEmpty()) {
            item {
                SectionTitle("Your Trends")
            }
            
            items(uiState.trends) { trend ->
                TrendCard(trend)
            }
        }

        // Bottom padding
        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
private fun InsightsHeader() {
    Column {
        Text(
            text = "Your Digital Wellness Insights",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = UnswipeTextPrimary
            )
        )
        Text(
            text = "Understand your patterns and improve your digital habits",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = UnswipeTextSecondary
            )
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            color = UnswipeTextPrimary
        ),
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun EmotionalWellnessCard(insights: EmotionalUsageInsights?) {
    insights?.let { data ->
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
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = getEmotionalIcon(data.dominantEmotion),
                        contentDescription = null,
                        tint = getEmotionalColor(data.dominantEmotion),
                        modifier = Modifier.size(32.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Wellness Score",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = UnswipeTextPrimary
                            )
                        )
                        Text(
                            text = "${(data.overallWellnessScore * 100).toInt()}%",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = getWellnessColor(data.overallWellnessScore),
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    
                    // Wellness indicator
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                color = getWellnessColor(data.overallWellnessScore).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(30.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = getWellnessEmoji(data.overallWellnessScore),
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Emotional breakdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    EmotionalScoreItem("Stress", data.stressScore, UnswipeRed)
                    EmotionalScoreItem("Boredom", data.boredomScore, UnswipeYellow)
                    EmotionalScoreItem("Anxiety", data.anxietyScore, UnswipeOrange)
                }
                
                if (data.recommendations.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = UnswipeGray.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Quick Tips:",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = UnswipeTextPrimary
                        )
                    )
                    
                    data.recommendations.take(2).forEach { tip ->
                        Text(
                            text = "• $tip",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = UnswipeTextSecondary
                            ),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmotionalScoreItem(
    label: String,
    score: Float,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                color = UnswipeTextSecondary
            )
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "${(score * 100).toInt()}%",
            style = MaterialTheme.typography.titleMedium.copy(
                color = color,
                fontWeight = FontWeight.Bold
            )
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        LinearProgressIndicator(
            progress = score,
            modifier = Modifier.width(60.dp),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}

@Composable
private fun UsagePatternCard(pattern: UsagePattern) {
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
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = getPatternIcon(pattern.type),
                    contentDescription = null,
                    tint = getSeverityColor(pattern.severity),
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = getPatternTitle(pattern.type),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = UnswipeTextPrimary
                        )
                    )
                    Text(
                        text = pattern.getSeverityDescription(),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = getSeverityColor(pattern.severity)
                        )
                    )
                }
                
                // Confidence badge
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = UnswipePrimary.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${pattern.getConfidencePercentage()}%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = UnswipePrimary,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = pattern.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = UnswipeTextSecondary
                )
            )
            
            if (pattern.suggestion.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = UnswipePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = pattern.suggestion,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = UnswipeTextPrimary
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun RecommendationCard(
    recommendation: PersonalizedRecommendation,
    onImplement: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = UnswipeCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = getRecommendationIcon(recommendation.actionType),
                    contentDescription = null,
                    tint = getPriorityColor(recommendation.priority),
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = recommendation.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = UnswipeTextPrimary
                        )
                    )
                    Text(
                        text = recommendation.priority.name.lowercase().replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = getPriorityColor(recommendation.priority)
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = recommendation.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = UnswipeTextSecondary
                )
            )
            
            if (recommendation.implementationSteps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Button(
                    onClick = onImplement,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = UnswipePrimary,
                        contentColor = UnswipeBlack
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Implement")
                }
            }
        }
    }
}

@Composable
private fun RiskAssessmentCard(risk: RiskAssessment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (risk.riskLevel) {
                RiskAssessment.RiskLevel.HIGH, RiskAssessment.RiskLevel.CRITICAL -> UnswipeRed.copy(alpha = 0.1f)
                RiskAssessment.RiskLevel.MEDIUM -> UnswipeYellow.copy(alpha = 0.1f)
                else -> UnswipeGreen.copy(alpha = 0.1f)
            }
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = when (risk.riskLevel) {
                        RiskAssessment.RiskLevel.CRITICAL -> Icons.Default.Warning
                        RiskAssessment.RiskLevel.HIGH -> Icons.Default.Error
                        RiskAssessment.RiskLevel.MEDIUM -> Icons.Default.Info
                        else -> Icons.Default.CheckCircle
                    },
                    contentDescription = null,
                    tint = getRiskColor(risk.riskLevel),
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "${risk.riskLevel.name.lowercase().replaceFirstChar { it.uppercase() }} Risk",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = getRiskColor(risk.riskLevel)
                    )
                )
            }
            
            if (risk.mitigationStrategies.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Mitigation strategies:",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = UnswipeTextPrimary
                    )
                )
                
                risk.mitigationStrategies.forEach { strategy ->
                    Text(
                        text = "• $strategy",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = UnswipeTextSecondary
                        ),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TrendCard(trend: UsageTrend) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = UnswipeCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (trend.direction) {
                    UsageTrend.TrendDirection.IMPROVING -> Icons.Default.TrendingUp
                    UsageTrend.TrendDirection.DECLINING -> Icons.Default.TrendingDown
                    else -> Icons.Default.TrendingFlat
                },
                contentDescription = null,
                tint = when (trend.direction) {
                    UsageTrend.TrendDirection.IMPROVING -> UnswipeGreen
                    UsageTrend.TrendDirection.DECLINING -> UnswipeRed
                    else -> UnswipeGray
                },
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = trend.metric.name.lowercase().replaceFirstChar { it.uppercase() }.replace("_", " "),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = UnswipeTextPrimary
                    )
                )
                Text(
                    text = trend.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = UnswipeTextSecondary
                    )
                )
            }
            
            Text(
                text = "${if (trend.magnitude > 0) "+" else ""}${trend.magnitude.toInt()}%",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = when (trend.direction) {
                        UsageTrend.TrendDirection.IMPROVING -> UnswipeGreen
                        UsageTrend.TrendDirection.DECLINING -> UnswipeRed
                        else -> UnswipeGray
                    }
                )
            )
        }
    }
}

// Helper functions for UI styling
private fun getEmotionalIcon(emotion: EmotionalState): ImageVector = when (emotion) {
    EmotionalState.BALANCED -> Icons.Default.Mood
    EmotionalState.STRESSED -> Icons.Default.MoodBad
    EmotionalState.BORED -> Icons.Default.SentimentNeutral
    EmotionalState.ANXIOUS -> Icons.Default.SentimentVeryDissatisfied
    EmotionalState.OVERWHELMED -> Icons.Default.SentimentVeryDissatisfied
}

private fun getEmotionalColor(emotion: EmotionalState): Color = when (emotion) {
    EmotionalState.BALANCED -> UnswipeGreen
    EmotionalState.STRESSED -> UnswipeRed
    EmotionalState.BORED -> UnswipeYellow
    EmotionalState.ANXIOUS -> UnswipeOrange
    EmotionalState.OVERWHELMED -> UnswipeRed
}

private fun getWellnessColor(score: Float): Color = when {
    score > 0.8f -> UnswipeGreen
    score > 0.6f -> UnswipeYellow
    score > 0.4f -> UnswipeOrange
    else -> UnswipeRed
}

private fun getWellnessEmoji(score: Float): String = when {
    score > 0.8f -> "😊"
    score > 0.6f -> "🙂"
    score > 0.4f -> "😐"
    score > 0.2f -> "😕"
    else -> "😰"
}

private fun getPatternIcon(type: PatternType): ImageVector = when (type) {
    PatternType.BINGE_USAGE -> Icons.Default.Timelapse
    PatternType.STRESS_USAGE -> Icons.Default.MoodBad
    PatternType.PROCRASTINATION -> Icons.Default.WorkOff
    PatternType.IMPULSE_USAGE -> Icons.Default.TouchApp
    PatternType.WORK_INTERRUPTION -> Icons.Default.NotificationsActive
    PatternType.SLEEP_DISRUPTION -> Icons.Default.Bedtime
    else -> Icons.Default.Analytics
}

private fun getPatternTitle(type: PatternType): String = when (type) {
    PatternType.BINGE_USAGE -> "Binge Usage"
    PatternType.STRESS_USAGE -> "Stress-Related Usage"
    PatternType.PROCRASTINATION -> "Work Procrastination"
    PatternType.IMPULSE_USAGE -> "Impulse Checking"
    PatternType.WORK_INTERRUPTION -> "Work Interruptions"
    PatternType.SLEEP_DISRUPTION -> "Sleep Disruption"
    else -> type.name.lowercase().replaceFirstChar { it.uppercase() }.replace("_", " ")
}

private fun getSeverityColor(severity: UsagePattern.Severity): Color = when (severity) {
    UsagePattern.Severity.HIGH -> UnswipeRed
    UsagePattern.Severity.MEDIUM -> UnswipeYellow
    UsagePattern.Severity.LOW -> UnswipeGreen
}

private fun getRecommendationIcon(actionType: PersonalizedRecommendation.ActionType): ImageVector = when (actionType) {
    PersonalizedRecommendation.ActionType.ENABLE_FEATURE -> Icons.Default.ToggleOn
    PersonalizedRecommendation.ActionType.CHANGE_SETTING -> Icons.Default.Settings
    PersonalizedRecommendation.ActionType.BEHAVIORAL_CHANGE -> Icons.Default.Psychology
    PersonalizedRecommendation.ActionType.SCHEDULE_ACTIVITY -> Icons.Default.Schedule
    PersonalizedRecommendation.ActionType.SEEK_SUPPORT -> Icons.Default.Support
}

private fun getPriorityColor(priority: PersonalizedRecommendation.Priority): Color = when (priority) {
    PersonalizedRecommendation.Priority.URGENT -> UnswipeRed
    PersonalizedRecommendation.Priority.HIGH -> UnswipeOrange
    PersonalizedRecommendation.Priority.MEDIUM -> UnswipeYellow
    PersonalizedRecommendation.Priority.LOW -> UnswipeGreen
}

private fun getRiskColor(riskLevel: RiskAssessment.RiskLevel): Color = when (riskLevel) {
    RiskAssessment.RiskLevel.CRITICAL -> UnswipeRed
    RiskAssessment.RiskLevel.HIGH -> UnswipeOrange
    RiskAssessment.RiskLevel.MEDIUM -> UnswipeYellow
    RiskAssessment.RiskLevel.LOW -> UnswipeGreen
} 