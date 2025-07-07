package com.unswipe.android.ui.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unswipe.android.data.analytics.UsagePatternAnalyzer
import com.unswipe.android.domain.model.*
import com.unswipe.android.domain.repository.UsageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val usagePatternAnalyzer: UsagePatternAnalyzer,
    private val usageRepository: UsageRepository
) : ViewModel() {

    private val _uiState: MutableStateFlow<InsightsUiState> = MutableStateFlow(InsightsUiState.Loading)
    val uiState: StateFlow<InsightsUiState> = _uiState.asStateFlow()

    init {
        loadInsights()
    }

    fun refreshInsights() {
        loadInsights()
    }

    fun implementRecommendation(recommendation: PersonalizedRecommendation) {
        viewModelScope.launch {
            // Implement the recommendation based on its action type
            when (recommendation.actionType) {
                PersonalizedRecommendation.ActionType.ENABLE_FEATURE -> {
                    // Enable specific features like Focus Mode, Bedtime Mode, etc.
                    handleFeatureEnabling(recommendation)
                }
                PersonalizedRecommendation.ActionType.CHANGE_SETTING -> {
                    // Change app settings like daily limits, blocked apps, etc.
                    handleSettingChange(recommendation)
                }
                PersonalizedRecommendation.ActionType.BEHAVIORAL_CHANGE -> {
                    // Log behavioral change recommendation acceptance
                    logBehavioralChangeAcceptance(recommendation)
                }
                PersonalizedRecommendation.ActionType.SCHEDULE_ACTIVITY -> {
                    // Help user schedule activities
                    handleActivityScheduling(recommendation)
                }
                PersonalizedRecommendation.ActionType.SEEK_SUPPORT -> {
                    // Provide resources for seeking support
                    handleSupportResources(recommendation)
                }
            }
            
            // Refresh insights after implementation
            loadInsights()
        }
    }

    private fun loadInsights() {
        viewModelScope.launch {
            try {
                _uiState.value = InsightsUiState.Loading
                
                // Load all insights in parallel
                val patterns = usagePatternAnalyzer.detectUsagePatterns()
                val emotionalInsights = usageRepository.getEmotionalUsageInsights().first()
                val recommendations = generatePersonalizedRecommendations(patterns, emotionalInsights)
                val riskAssessment = generateRiskAssessment(patterns, emotionalInsights)
                val trends = generateUsageTrends()
                
                _uiState.value = InsightsUiState.Success(
                    usagePatterns = patterns,
                    emotionalInsights = emotionalInsights,
                    recommendations = recommendations,
                    riskAssessment = riskAssessment,
                    trends = trends
                )
                
            } catch (e: Exception) {
                _uiState.value = InsightsUiState.Error(
                    message = e.localizedMessage ?: "Failed to load insights"
                )
            }
        }
    }

    private suspend fun generatePersonalizedRecommendations(
        patterns: List<UsagePattern>,
        emotionalInsights: EmotionalUsageInsights
    ): List<PersonalizedRecommendation> {
        val recommendations = mutableListOf<PersonalizedRecommendation>()
        
        // Generate recommendations based on detected patterns
        patterns.forEach { pattern ->
            when (pattern.type) {
                PatternType.BINGE_USAGE -> {
                    recommendations.add(
                        PersonalizedRecommendation(
                            title = "Enable Session Timers",
                            description = "Set 30-minute session timers to break up long usage periods and improve awareness",
                            actionType = PersonalizedRecommendation.ActionType.ENABLE_FEATURE,
                            priority = PersonalizedRecommendation.Priority.HIGH,
                            category = PersonalizedRecommendation.RecommendationCategory.FOCUS,
                            estimatedImpact = PersonalizedRecommendation.ImpactLevel.SIGNIFICANT,
                            relatedPatterns = listOf(PatternType.BINGE_USAGE),
                            implementationSteps = listOf(
                                "Go to Settings > Focus Mode",
                                "Enable Session Timers",
                                "Set timer to 30 minutes",
                                "Choose gentle reminder tone"
                            )
                        )
                    )
                }
                
                PatternType.STRESS_USAGE -> {
                    recommendations.add(
                        PersonalizedRecommendation(
                            title = "Practice Digital Mindfulness",
                            description = "Your usage patterns suggest stress. Try mindfulness exercises when reaching for your phone",
                            actionType = PersonalizedRecommendation.ActionType.BEHAVIORAL_CHANGE,
                            priority = PersonalizedRecommendation.Priority.HIGH,
                            category = PersonalizedRecommendation.RecommendationCategory.WELLNESS,
                            estimatedImpact = PersonalizedRecommendation.ImpactLevel.TRANSFORMATIVE,
                            relatedPatterns = listOf(PatternType.STRESS_USAGE),
                            implementationSteps = listOf(
                                "Before opening social media, take 3 deep breaths",
                                "Ask yourself: 'What am I feeling right now?'",
                                "Consider alternative activities: walk, call a friend, journal",
                                "Set a specific time limit before opening the app"
                            )
                        )
                    )
                }
                
                PatternType.WORK_INTERRUPTION -> {
                    recommendations.add(
                        PersonalizedRecommendation(
                            title = "Enable Work Focus Mode",
                            description = "Block distracting apps during work hours to improve productivity and focus",
                            actionType = PersonalizedRecommendation.ActionType.ENABLE_FEATURE,
                            priority = PersonalizedRecommendation.Priority.MEDIUM,
                            category = PersonalizedRecommendation.RecommendationCategory.PRODUCTIVITY,
                            estimatedImpact = PersonalizedRecommendation.ImpactLevel.SIGNIFICANT,
                            relatedPatterns = listOf(PatternType.WORK_INTERRUPTION, PatternType.PROCRASTINATION),
                            implementationSteps = listOf(
                                "Go to Settings > Focus Modes",
                                "Create 'Work Focus' mode",
                                "Set work hours (9 AM - 5 PM)",
                                "Block social media apps during these hours"
                            )
                        )
                    )
                }
                
                PatternType.SLEEP_DISRUPTION -> {
                    recommendations.add(
                        PersonalizedRecommendation(
                            title = "Enable Bedtime Mode",
                            description = "Protect your sleep by limiting screen time before bed",
                            actionType = PersonalizedRecommendation.ActionType.ENABLE_FEATURE,
                            priority = PersonalizedRecommendation.Priority.HIGH,
                            category = PersonalizedRecommendation.RecommendationCategory.SLEEP,
                            estimatedImpact = PersonalizedRecommendation.ImpactLevel.SIGNIFICANT,
                            relatedPatterns = listOf(PatternType.SLEEP_DISRUPTION),
                            implementationSteps = listOf(
                                "Go to Settings > Bedtime Mode",
                                "Set bedtime (e.g., 10 PM)",
                                "Enable 1-hour wind-down period",
                                "Block all social media during bedtime"
                            )
                        )
                    )
                }
                
                else -> {
                    // Generic recommendation for other patterns
                    recommendations.add(
                        PersonalizedRecommendation(
                            title = "Set Daily Usage Limits",
                            description = "Establish healthy boundaries with specific time limits for problematic apps",
                            actionType = PersonalizedRecommendation.ActionType.CHANGE_SETTING,
                            priority = PersonalizedRecommendation.Priority.MEDIUM,
                            category = PersonalizedRecommendation.RecommendationCategory.WELLNESS,
                            estimatedImpact = PersonalizedRecommendation.ImpactLevel.MODERATE,
                            relatedPatterns = listOf(pattern.type),
                            implementationSteps = listOf("Go to Settings > Daily Limit", "Set a limit for ${pattern.apps.firstOrNull() ?: "problematic apps"}")
                        )
                    )
                }
            }
        }
        
        // Generate recommendations based on emotional insights
        if (emotionalInsights.stressScore > 0.6f) {
            recommendations.add(
                PersonalizedRecommendation(
                    title = "Stress Management Resources",
                    description = "Your usage patterns suggest high stress levels. Consider professional support or stress management techniques",
                    actionType = PersonalizedRecommendation.ActionType.SEEK_SUPPORT,
                    priority = PersonalizedRecommendation.Priority.HIGH,
                    category = PersonalizedRecommendation.RecommendationCategory.WELLNESS,
                    estimatedImpact = PersonalizedRecommendation.ImpactLevel.TRANSFORMATIVE,
                    implementationSteps = listOf(
                        "Try meditation apps like Headspace or Calm",
                        "Practice deep breathing exercises",
                        "Consider talking to a counselor or therapist",
                        "Join stress management support groups"
                    )
                )
            )
        }
        
        if (emotionalInsights.boredomScore > 0.6f) {
            recommendations.add(
                PersonalizedRecommendation(
                    title = "Find Engaging Alternatives",
                    description = "Replace mindless scrolling with meaningful activities that bring you joy",
                    actionType = PersonalizedRecommendation.ActionType.SCHEDULE_ACTIVITY,
                    priority = PersonalizedRecommendation.Priority.MEDIUM,
                    category = PersonalizedRecommendation.RecommendationCategory.PRODUCTIVITY,
                    estimatedImpact = PersonalizedRecommendation.ImpactLevel.SIGNIFICANT,
                    implementationSteps = listOf(
                        "List 5 activities you enjoy (reading, exercise, cooking, etc.)",
                        "Schedule these activities in your calendar",
                        "When feeling bored, choose from your list instead of opening social media",
                        "Track which activities make you feel most fulfilled"
                    )
                )
            )
        }
        
        return recommendations.sortedByDescending { 
            it.priority.ordinal * 10 + it.estimatedImpact.ordinal 
        }.take(5) // Return top 5 recommendations
    }

    private suspend fun generateRiskAssessment(
        patterns: List<UsagePattern>,
        emotionalInsights: EmotionalUsageInsights
    ): RiskAssessment {
        val riskFactors = mutableListOf<RiskAssessment.RiskFactor>()
        
        // Analyze risk factors from patterns
        patterns.forEach { pattern ->
            when {
                pattern.type == PatternType.BINGE_USAGE && pattern.severity == UsagePattern.Severity.HIGH -> {
                    riskFactors.add(RiskAssessment.RiskFactor.BINGE_HISTORY)
                }
                pattern.type == PatternType.STRESS_USAGE -> {
                    riskFactors.add(RiskAssessment.RiskFactor.STRESS_INDICATORS)
                }
                pattern.type == PatternType.SLEEP_DISRUPTION -> {
                    riskFactors.add(RiskAssessment.RiskFactor.POOR_SLEEP_PATTERN)
                }
                pattern.type == PatternType.WORK_INTERRUPTION -> {
                    riskFactors.add(RiskAssessment.RiskFactor.WORK_INTERRUPTIONS)
                }
            }
        }
        
        // Analyze emotional risk factors
        if (emotionalInsights.stressScore > 0.7f || emotionalInsights.anxietyScore > 0.7f) {
            riskFactors.add(RiskAssessment.RiskFactor.EMOTIONAL_TRIGGERS)
        }
        
        // Calculate overall risk level
        val riskLevel = when {
            riskFactors.size >= 4 -> RiskAssessment.RiskLevel.CRITICAL
            riskFactors.size >= 3 -> RiskAssessment.RiskLevel.HIGH
            riskFactors.size >= 2 -> RiskAssessment.RiskLevel.MEDIUM
            riskFactors.size >= 1 -> RiskAssessment.RiskLevel.LOW
            else -> RiskAssessment.RiskLevel.LOW
        }
        
        val confidence = (riskFactors.size / 6f).coerceAtMost(1f)
        
        val mitigationStrategies = when (riskLevel) {
            RiskAssessment.RiskLevel.CRITICAL -> listOf(
                "Consider seeking professional support for digital wellness",
                "Implement strict app blocking during high-risk periods",
                "Establish phone-free zones and times",
                "Practice stress management techniques daily"
            )
            RiskAssessment.RiskLevel.HIGH -> listOf(
                "Enable all available focus modes",
                "Set strict daily usage limits",
                "Find accountability partner for digital wellness goals",
                "Practice mindfulness before phone use"
            )
            RiskAssessment.RiskLevel.MEDIUM -> listOf(
                "Set session timers for problematic apps",
                "Create phone-free morning and evening routines",
                "Use app confirmation dialogs to increase awareness"
            )
            else -> listOf(
                "Continue monitoring usage patterns",
                "Maintain healthy digital boundaries"
            )
        }
        
        return RiskAssessment(
            riskLevel = riskLevel,
            riskFactors = riskFactors,
            confidence = confidence,
            mitigationStrategies = mitigationStrategies
        )
    }

    private suspend fun generateUsageTrends(): List<UsageTrend> {
        // This would typically compare current period with previous periods
        // For now, we'll generate sample trends
        return listOf(
            UsageTrend(
                metric = UsageTrend.TrendMetric.TOTAL_USAGE_TIME,
                direction = UsageTrend.TrendDirection.IMPROVING,
                magnitude = -15f, // 15% decrease
                timeframe = UsageTrend.TrendTimeframe.WEEKLY,
                significance = UsageTrend.TrendSignificance.MODERATE,
                description = "Your total usage time has decreased by 15% this week"
            ),
            UsageTrend(
                metric = UsageTrend.TrendMetric.SESSION_COUNT,
                direction = UsageTrend.TrendDirection.STABLE,
                magnitude = 2f, // 2% increase
                timeframe = UsageTrend.TrendTimeframe.DAILY,
                significance = UsageTrend.TrendSignificance.MINOR,
                description = "App opening frequency remains relatively stable"
            )
        )
    }

    // Implementation handlers for recommendations
    private suspend fun handleFeatureEnabling(recommendation: PersonalizedRecommendation) {
        // This would integrate with settings repository to enable features
        // For now, just log the action
        println("Enabling feature based on recommendation: ${recommendation.title}")
    }

    private suspend fun handleSettingChange(recommendation: PersonalizedRecommendation) {
        // This would modify app settings
        println("Changing setting based on recommendation: ${recommendation.title}")
    }

    private suspend fun logBehavioralChangeAcceptance(recommendation: PersonalizedRecommendation) {
        // Log that user accepted a behavioral change recommendation
        println("User accepted behavioral change: ${recommendation.title}")
    }

    private suspend fun handleActivityScheduling(recommendation: PersonalizedRecommendation) {
        // This could integrate with calendar apps or create reminders
        println("Scheduling activity based on recommendation: ${recommendation.title}")
    }

    private suspend fun handleSupportResources(recommendation: PersonalizedRecommendation) {
        // This could open external resources or provide contact information
        println("Providing support resources for: ${recommendation.title}")
    }
}

sealed class InsightsUiState {
    object Loading : InsightsUiState()
    
    data class Success(
        val usagePatterns: List<UsagePattern> = emptyList(),
        val emotionalInsights: EmotionalUsageInsights? = null,
        val recommendations: List<PersonalizedRecommendation> = emptyList(),
        val riskAssessment: RiskAssessment? = null,
        val trends: List<UsageTrend> = emptyList()
    ) : InsightsUiState()
    
    data class Error(
        val message: String
    ) : InsightsUiState()
} 