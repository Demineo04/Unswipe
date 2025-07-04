package com.unswipe.android.domain.model

/**
 * Represents a usage pattern that can be detected from user behavior
 */
data class UsagePattern(
    val type: PatternType,
    val confidence: Double,
    val severity: Severity,
    val description: String,
    val suggestion: String,
    val relatedContexts: List<ContextType> = emptyList(),
    val detectedAt: Long = System.currentTimeMillis()
) {
    fun getConfidencePercentage(): Int = (confidence * 100).toInt()
    
    fun getSeverityDescription(): String = when (severity) {
        Severity.LOW -> "Low concern"
        Severity.MEDIUM -> "Moderate concern"
        Severity.HIGH -> "High concern"
    }
    
    enum class Severity {
        LOW,
        MEDIUM,
        HIGH
    }
}

enum class PatternType {
    BINGE_USAGE,
    STRESS_USAGE,
    PROCRASTINATION,
    IMPULSE_USAGE,
    WORK_INTERRUPTION,
    SLEEP_DISRUPTION,
    SOCIAL_COMPARISON,
    MINDLESS_SCROLLING,
    NOTIFICATION_ADDICTION,
    LATE_NIGHT_USAGE,
    MORNING_RUSH,
    WEEKEND_EXCESS
}

/**
 * Emotional usage insights derived from usage patterns
 */
data class EmotionalUsageInsights(
    val dominantEmotion: EmotionalState,
    val stressScore: Float, // 0.0 to 1.0
    val boredomScore: Float, // 0.0 to 1.0
    val anxietyScore: Float, // 0.0 to 1.0
    val overallWellnessScore: Float, // 0.0 to 1.0
    val recommendations: List<String> = emptyList(),
    val analysisTimestamp: Long = System.currentTimeMillis()
)

enum class EmotionalState {
    BALANCED,
    STRESSED,
    BORED,
    ANXIOUS,
    OVERWHELMED
}

/**
 * Personalized recommendation based on usage patterns
 */
data class PersonalizedRecommendation(
    val title: String,
    val description: String,
    val actionType: ActionType,
    val priority: Priority,
    val estimatedImpact: ImpactLevel,
    val relatedPatterns: List<PatternType> = emptyList(),
    val implementationSteps: List<String> = emptyList()
) {
    enum class ActionType {
        ENABLE_FEATURE,
        CHANGE_SETTING,
        BEHAVIORAL_CHANGE,
        SCHEDULE_ACTIVITY,
        SEEK_SUPPORT
    }
    
    enum class Priority {
        LOW,
        MEDIUM,
        HIGH,
        URGENT
    }
    
    enum class ImpactLevel {
        MINIMAL,
        MODERATE,
        SIGNIFICANT,
        TRANSFORMATIVE
    }
}

/**
 * Risk assessment for digital wellness
 */
data class RiskAssessment(
    val riskLevel: RiskLevel,
    val riskFactors: List<RiskFactor>,
    val confidence: Float,
    val mitigationStrategies: List<String>
) {
    enum class RiskLevel {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }
    
    enum class RiskFactor {
        BINGE_HISTORY,
        STRESS_INDICATORS,
        POOR_SLEEP_PATTERN,
        WORK_INTERRUPTIONS,
        EMOTIONAL_TRIGGERS,
        SOCIAL_PRESSURE
    }
}

/**
 * Usage trend information
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
        APP_LAUNCHES,
        INTERRUPTION_FREQUENCY,
        PRODUCTIVITY_SCORE
    }
    
    enum class TrendDirection {
        IMPROVING,
        DECLINING,
        STABLE
    }
    
    enum class TrendTimeframe {
        DAILY,
        WEEKLY,
        MONTHLY
    }
    
    enum class TrendSignificance {
        MINOR,
        MODERATE,
        MAJOR
    }
}