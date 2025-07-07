package com.unswipe.android.domain.model

import java.time.LocalDateTime

/**
 * Premium subscription tiers
 */
enum class PremiumTier {
    FREE,
    PREMIUM_INDIVIDUAL,
    PREMIUM_FAMILY,
    PREMIUM_PRO
}

/**
 * Premium feature flags
 */
enum class PremiumFeature {
    // Analytics & Insights
    EXTENDED_HISTORY,           // 1 year vs 30 days
    ADVANCED_ANALYTICS,         // Detailed pattern analysis
    TREND_ANALYSIS,            // Weekly/monthly trends
    COMPARATIVE_INSIGHTS,       // Compare to average users
    DATA_EXPORT,               // CSV/PDF export
    
    // Smart Scheduling & Automation
    SMART_FOCUS_MODES,         // Auto-enable based on context
    CALENDAR_INTEGRATION,       // Sync with Google/Outlook
    SCHEDULED_INTERVENTIONS,    // Time-based blocking rules
    SMART_BREAKS,              // Proactive wellness breaks
    BEDTIME_ENFORCEMENT,       // Gradual sleep-time restrictions
    
    // Advanced Bypass & Flexibility
    SMART_BYPASS_CREDITS,      // Earn and spend bypass credits
    EMERGENCY_OVERRIDE,        // Instant bypass with tracking
    TEMPORARY_ADJUSTMENTS,     // Travel/work late modes
    CONTEXT_AWARE_LIMITS,      // Different limits per context
    CUSTOM_INTERVENTION_MESSAGES, // Personalized blocking messages
    
    // Social & Accountability
    FAMILY_CONTROLS,           // Parent dashboards
    ACCOUNTABILITY_PARTNERS,    // Share progress with friends
    TEAM_CHALLENGES,           // Workplace competitions
    COACH_INTEGRATION,         // Share data with coaches
    
    // Wellness Integrations
    HEALTH_APP_SYNC,           // Correlate with health data
    MEDITATION_INTEGRATION,     // Mindfulness suggestions
    MOOD_TRACKING,             // Mood correlation analysis
    SLEEP_QUALITY_ANALYSIS,    // Deep sleep integration
    
    // Multi-Device & Cross-Platform
    MULTI_DEVICE_SYNC,         // Sync across devices
    BROWSER_EXTENSION,         // Web blocking
    WEARABLE_INTEGRATION,      // Apple Watch/Wear OS
    
    // Professional Features
    TEAM_ANALYTICS,            // Manager dashboards
    CORPORATE_REPORTING,       // Aggregate insights
    API_ACCESS,                // Integration API
    PRIORITY_SUPPORT           // Premium customer support
}

/**
 * Premium subscription information
 */
data class PremiumSubscription(
    val tier: PremiumTier,
    val isActive: Boolean,
    val expirationDate: LocalDateTime?,
    val purchaseDate: LocalDateTime?,
    val autoRenew: Boolean = true,
    val familyMemberCount: Int = 1,
    val availableFeatures: Set<PremiumFeature>
) {
    
    fun hasFeature(feature: PremiumFeature): Boolean {
        return availableFeatures.contains(feature)
    }
    
    fun isExpired(): Boolean {
        return expirationDate?.isBefore(LocalDateTime.now()) == true
    }
    
    companion object {
        val Free = PremiumSubscription(
            tier = PremiumTier.FREE,
            isActive = true,
            expirationDate = null,
            purchaseDate = null,
            availableFeatures = emptySet()
        )
        
        fun getPremiumIndividual(purchaseDate: LocalDateTime): PremiumSubscription {
            return PremiumSubscription(
                tier = PremiumTier.PREMIUM_INDIVIDUAL,
                isActive = true,
                expirationDate = purchaseDate.plusMonths(1),
                purchaseDate = purchaseDate,
                availableFeatures = setOf(
                    PremiumFeature.EXTENDED_HISTORY,
                    PremiumFeature.ADVANCED_ANALYTICS,
                    PremiumFeature.TREND_ANALYSIS,
                    PremiumFeature.SMART_FOCUS_MODES,
                    PremiumFeature.CALENDAR_INTEGRATION,
                    PremiumFeature.SMART_BYPASS_CREDITS,
                    PremiumFeature.EMERGENCY_OVERRIDE,
                    PremiumFeature.TEMPORARY_ADJUSTMENTS,
                    PremiumFeature.CUSTOM_INTERVENTION_MESSAGES,
                    PremiumFeature.HEALTH_APP_SYNC,
                    PremiumFeature.MEDITATION_INTEGRATION
                )
            )
        }
        
        fun getPremiumFamily(purchaseDate: LocalDateTime): PremiumSubscription {
            return PremiumSubscription(
                tier = PremiumTier.PREMIUM_FAMILY,
                isActive = true,
                expirationDate = purchaseDate.plusMonths(1),
                purchaseDate = purchaseDate,
                familyMemberCount = 6,
                availableFeatures = getPremiumIndividual(purchaseDate).availableFeatures + setOf(
                    PremiumFeature.FAMILY_CONTROLS,
                    PremiumFeature.ACCOUNTABILITY_PARTNERS,
                    PremiumFeature.MULTI_DEVICE_SYNC
                )
            )
        }
        
        fun getPremiumPro(purchaseDate: LocalDateTime): PremiumSubscription {
            return PremiumSubscription(
                tier = PremiumTier.PREMIUM_PRO,
                isActive = true,
                expirationDate = purchaseDate.plusMonths(1),
                purchaseDate = purchaseDate,
                availableFeatures = PremiumFeature.values().toSet() // All features
            )
        }
    }
}

/**
 * Bypass credits system for premium users
 */
data class BypassCredits(
    val available: Int,
    val used: Int,
    val earnedToday: Int,
    val maxDaily: Int = 5,
    val lastResetDate: LocalDateTime
) {
    val canUseBypass: Boolean
        get() = available > 0
    
    fun useCredit(): BypassCredits {
        return if (available > 0) {
            copy(available = available - 1, used = used + 1)
        } else {
            this
        }
    }
    
    fun earnCredit(): BypassCredits {
        return if (earnedToday < maxDaily) {
            copy(available = available + 1, earnedToday = earnedToday + 1)
        } else {
            this
        }
    }
}

/**
 * Smart focus mode configuration
 */
data class SmartFocusMode(
    val id: String,
    val name: String,
    val isEnabled: Boolean,
    val blockedApps: Set<String>,
    val triggers: Set<FocusTrigger>,
    val duration: Long? = null, // null = until manually disabled
    val allowEmergencyBypass: Boolean = true,
    val customMessage: String? = null
)

/**
 * Focus mode triggers
 */
sealed class FocusTrigger {
    data class CalendarEvent(val keywords: Set<String>) : FocusTrigger()
    data class Location(val wifiSSIDs: Set<String>) : FocusTrigger()
    data class TimeRange(val startHour: Int, val endHour: Int, val days: Set<Int>) : FocusTrigger()
    data class UsageThreshold(val packageName: String, val thresholdMinutes: Int) : FocusTrigger()
}

/**
 * Family member profile for family plans
 */
data class FamilyMember(
    val id: String,
    val name: String,
    val email: String?,
    val role: FamilyRole,
    val ageGroup: AgeGroup,
    val customLimits: Map<String, Long> = emptyMap(),
    val allowedBypassCount: Int = 0,
    val isActive: Boolean = true
)

enum class FamilyRole {
    PARENT,
    CHILD,
    TEEN
}

enum class AgeGroup {
    CHILD_6_9,
    CHILD_10_12,
    TEEN_13_15,
    TEEN_16_17,
    ADULT
}

/**
 * Advanced analytics data
 */
data class AdvancedAnalytics(
    val productivityScore: Float, // 0.0 to 1.0
    val focusQuality: Float,
    val digitalWellnessScore: Float,
    val weeklyTrends: List<TrendData>,
    val monthlyComparison: ComparisonData,
    val patternInsights: List<PatternInsight>,
    val recommendations: List<PersonalizedRecommendation>
)

data class TrendData(
    val date: LocalDateTime,
    val usageMinutes: Int,
    val sessionCount: Int,
    val productivityScore: Float,
    val focusInterruptions: Int
)

data class ComparisonData(
    val userAverage: Float,
    val globalAverage: Float,
    val percentile: Int, // 0-100
    val improvement: Float // % change from last period
)

data class PatternInsight(
    val type: String,
    val description: String,
    val confidence: Float,
    val actionable: Boolean,
    val suggestedAction: String?
)