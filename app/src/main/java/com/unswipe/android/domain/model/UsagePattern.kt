package com.unswipe.android.domain.model

/**
 * Represents a detected usage pattern with confidence score and actionable insights
 */
data class UsagePattern(
    val type: PatternType,
    val description: String,
    val apps: List<String> = emptyList(),
    val confidence: Double,
    val severity: Severity = Severity.MEDIUM,
    val suggestion: String,
    val actionable: Boolean = true,
    val relatedContexts: List<ContextType> = emptyList(),
    val detectedAt: Long = System.currentTimeMillis(),
    val metadata: Map<String, Any> = emptyMap()
) {
    enum class Severity {
        LOW, MEDIUM, HIGH
    }
    
    /**
     * Returns a user-friendly severity description
     */
    fun getSeverityDescription(): String = when (severity) {
        Severity.LOW -> "Minor concern"
        Severity.MEDIUM -> "Moderate concern"
        Severity.HIGH -> "Significant concern"
    }
    
    /**
     * Returns a confidence percentage for display
     */
    fun getConfidencePercentage(): Int = (confidence * 100).toInt().coerceIn(0, 100)
}

/**
 * Types of usage patterns that can be detected
 */
enum class PatternType {
    BINGE_USAGE,           // Long continuous usage sessions
    STRESS_USAGE,          // Usage patterns indicating stress/anxiety
    PROCRASTINATION,       // Social media during work hours
    IMPULSE_USAGE,         // Frequent short app checks
    WORK_INTERRUPTION,     // Frequent interruptions during work
    SLEEP_DISRUPTION,      // Usage affecting sleep hygiene
    WEEKEND_BINGE,         // Excessive weekend usage
    MORNING_SCROLL,        // Excessive morning social media
    EVENING_ESCAPE,        // Evening usage as stress relief
    SOCIAL_COMPARISON,     // Patterns suggesting social comparison
    HABIT_LOOP,            // Repetitive usage patterns
    NOTIFICATION_DRIVEN,   // Usage driven by notifications
    LATE_NIGHT_USAGE,      // Usage during late night hours
    MORNING_RUSH,          // Excessive morning usage
    WEEKEND_EXCESS,        // Weekend usage excess
    MINDLESS_SCROLLING,    // Unconscious scrolling behavior
    NOTIFICATION_ADDICTION // Addiction to notifications

}

/**
 * Emotional insights derived from usage patterns
 */
data class EmotionalUsageInsights(
    val stressScore: Float,              // 0.0 to 1.0
    val boredomScore: Float,             // 0.0 to 1.0
    val anxietyScore: Float,             // 0.0 to 1.0
    val hasEveningSpikes: Boolean,       // High usage in evening
    val hasLateNightUsage: Boolean,      // Usage after 11 PM
    val hasRapidSwitching: Boolean,      // Frequent app switching
    val hasWeekendSpikes: Boolean,       // Higher weekend usage
    val recommendations: List<String>,    // Actionable recommendations
    val dominantEmotion: EmotionalState = getDominantEmotion(stressScore, boredomScore, anxietyScore),
    val overallWellnessScore: Float = calculateOverallWellness(stressScore, boredomScore, anxietyScore),
    val analysisTimestamp: Long = System.currentTimeMillis()

) {
    companion object {
        private fun getDominantEmotion(stress: Float, boredom: Float, anxiety: Float): EmotionalState {
            return when {
                stress > 0.7f && boredom > 0.7f && anxiety > 0.7f -> EmotionalState.OVERWHELMED
                stress > boredom && stress > anxiety -> EmotionalState.STRESSED
                boredom > stress && boredom > anxiety -> EmotionalState.BORED
                anxiety > stress && anxiety > boredom -> EmotionalState.ANXIOUS
                else -> EmotionalState.BALANCED
            }
        }
        
        private fun calculateOverallWellness(stress: Float, boredom: Float, anxiety: Float): Float {
            // Lower scores indicate better wellness
            val combinedScore = (stress + boredom + anxiety) / 3f
            return 1f - combinedScore // Invert so higher = better wellness
        }
    }
}

/**
 * Emotional states that can be inferred from usage patterns
 */
enum class EmotionalState {
    BALANCED,    // Healthy usage patterns
    STRESSED,    // Stress-driven usage
    BORED,       // Boredom-driven usage
    ANXIOUS,     // Anxiety-driven usage
    OVERWHELMED  // Multiple negative indicators
}

/**
 * Represents a usage session with context
 */
data class UsageSession(
    val packageName: String,
    val startTime: Long,
    val endTime: Long,
    val context: ContextType,
    val interruptionCount: Int = 0,
    val wasBlocked: Boolean = false
) {
    val duration: Long get() = endTime - startTime
    val durationMinutes: Int get() = (duration / 60000).toInt()
    
    fun isLongSession(thresholdMinutes: Int = 30): Boolean {
        return durationMinutes > thresholdMinutes
    }
    
    fun isMicroSession(thresholdMinutes: Int = 2): Boolean {
        return durationMinutes < thresholdMinutes
    }
}

/**
 * Risk assessment for potential problematic usage
 */
data class RiskAssessment(
    val riskLevel: RiskLevel,
    val riskFactors: List<RiskFactor>,
    val confidence: Float,
    val timeToRisk: Long? = null, // Milliseconds until predicted risk event
    val mitigationStrategies: List<String> = emptyList()
) {
    enum class RiskLevel {
        LOW, MEDIUM, HIGH, CRITICAL
    }
    
    enum class RiskFactor {
        APPROACHING_DAILY_LIMIT,
        STRESS_INDICATORS,
        BINGE_HISTORY,
        POOR_SLEEP_PATTERN,
        WORK_INTERRUPTIONS,
        EMOTIONAL_TRIGGERS,
        SOCIAL_PRESSURE

    }
}

/**
 * Personalized recommendations based on usage patterns
 */
data class PersonalizedRecommendation(
    val title: String,
    val description: String,
    val actionType: ActionType,
    val priority: Priority,
    val category: RecommendationCategory,
    val estimatedImpact: ImpactLevel,
    val relatedPatterns: List<PatternType> = emptyList(),
    val implementationSteps: List<String> = emptyList()
) {
    enum class ActionType {
        ENABLE_FEATURE,      // Enable a specific app feature
        CHANGE_SETTING,      // Modify app settings
        BEHAVIORAL_CHANGE,   // Suggest behavior modification
        SCHEDULE_ACTIVITY,   // Schedule specific activity
        SEEK_SUPPORT        // Recommend external support
    }
    
    enum class Priority {
        LOW, MEDIUM, HIGH, URGENT
    }
    
    enum class RecommendationCategory {
        PRODUCTIVITY, WELLNESS, SLEEP, FOCUS, SOCIAL
    }
    
    enum class ImpactLevel {
        MINIMAL, MODERATE, SIGNIFICANT, TRANSFORMATIVE
    }
}

/**
 * Trend analysis over time
 */
data class UsageTrend(
    val metric: TrendMetric,
    val direction: TrendDirection,
    val magnitude: Float, // Percentage change
    val timeframe: TrendTimeframe,
    val significance: TrendSignificance,
    val description: String
) {
    enum class TrendMetric {
        TOTAL_USAGE_TIME,
        SESSION_COUNT,
        AVERAGE_SESSION_LENGTH,
        APP_LAUNCHES,
        INTERRUPTION_FREQUENCY,
        STRESS_SCORE,
        WELLNESS_SCORE,
        PRODUCTIVITY_SCORE

    }
    
    enum class TrendDirection {
        IMPROVING, STABLE, DECLINING
    }
    
    enum class TrendTimeframe {
        DAILY, WEEKLY, MONTHLY
    }
    
    enum class TrendSignificance {
        INSIGNIFICANT, MINOR, MODERATE, MAJOR
    }
}

/**
 * Comparative insights against anonymous benchmarks
 */
data class ComparativeInsights(
    val userPercentile: Int, // 0-100, where 100 = least usage
    val categoryComparison: Map<String, Int>, // App category percentiles
    val wellnessRanking: WellnessRanking,
    val improvementPotential: Float, // 0.0 to 1.0
    val anonymizedBenchmarks: Map<String, Float>
) {
    enum class WellnessRanking {
        EXCELLENT,    // Top 10%
        GOOD,         // Top 25%
        AVERAGE,      // Middle 50%
        NEEDS_WORK,   // Bottom 25%
        CONCERNING    // Bottom 10%
    }
}

