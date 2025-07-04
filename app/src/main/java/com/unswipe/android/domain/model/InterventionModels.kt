package com.unswipe.android.domain.model

/**
 * Represents a decision about whether and how to intervene in app usage
 */
data class InterventionDecision(
    val shouldIntervene: Boolean,
    val urgency: InterventionUrgency = InterventionUrgency.LOW,
    val message: String,
    val suggestedAction: InterventionAction = InterventionAction.GENTLE_REMINDER,
    val contextualTip: String? = null,
    val alternativeActivity: String? = null,
    val confidence: Float = 1.0f,
    val relatedContext: ContextType? = null,
    val bypassAllowed: Boolean = true,
    val metadata: Map<String, Any> = emptyMap()
) {
    /**
     * Returns whether this intervention should be shown to the user
     */
    fun shouldShow(): Boolean = shouldIntervene && confidence > 0.5f
    
    /**
     * Returns a user-friendly urgency description
     */
    fun getUrgencyDescription(): String = when (urgency) {
        InterventionUrgency.LOW -> "Gentle reminder"
        InterventionUrgency.MEDIUM -> "Important notice"
        InterventionUrgency.HIGH -> "Strong recommendation"
        InterventionUrgency.CRITICAL -> "Critical intervention"
    }
}

/**
 * Levels of intervention urgency
 */
enum class InterventionUrgency {
    LOW,        // Gentle nudge, easily bypassed
    MEDIUM,     // Moderate intervention, requires confirmation
    HIGH,       // Strong intervention, requires deliberate action
    CRITICAL    // Emergency intervention, maximum friction
}

/**
 * Types of intervention actions that can be taken
 */
enum class InterventionAction {
    GENTLE_REMINDER,    // Show message, allow easy bypass
    CONFIRMATION,       // Require explicit confirmation
    DELAY,             // Add time delay before allowing access
    FIRM_BLOCK,        // Block with option to override
    STRONG_BLOCK,      // Block with significant friction to override
    ABSOLUTE_BLOCK     // Complete block with no override option
}

/**
 * Represents the outcome of an intervention
 */
data class InterventionOutcome(
    val interventionId: String,
    val wasShown: Boolean,
    val userAction: UserAction,
    val timestamp: Long = System.currentTimeMillis(),
    val contextAtTime: ContextType,
    val usageTimeAtIntervention: Long,
    val delayBeforeDecision: Long = 0L, // How long user took to decide
    val metadata: Map<String, Any> = emptyMap()
) {
    enum class UserAction {
        BYPASSED,           // User chose to continue despite intervention
        BLOCKED_SELF,       // User chose to go back/not use app
        DISMISSED,          // User dismissed without clear choice
        TIMED_OUT,          // Intervention timed out
        ACCEPTED_ALTERNATIVE // User chose suggested alternative activity
    }
    
    /**
     * Returns whether the intervention was effective
     */
    fun wasEffective(): Boolean = when (userAction) {
        UserAction.BLOCKED_SELF, UserAction.ACCEPTED_ALTERNATIVE -> true
        UserAction.BYPASSED, UserAction.DISMISSED, UserAction.TIMED_OUT -> false
    }
}

/**
 * Configuration for intervention behavior
 */
data class InterventionConfig(
    val enabledContexts: Set<ContextType> = setOf(
        ContextType.WORK_HOURS,
        ContextType.SLEEP_PREPARATION,
        ContextType.BEDTIME
    ),
    val maxInterventionsPerHour: Int = 3,
    val cooldownBetweenInterventions: Long = 300_000L, // 5 minutes
    val adaptiveThresholds: Boolean = true,
    val learningEnabled: Boolean = true,
    val customMessages: Map<String, String> = emptyMap(), // Package name to custom message
    val bypassCreditsEnabled: Boolean = false,
    val emergencyBypassEnabled: Boolean = true
) {
    /**
     * Returns whether interventions are allowed in the given context
     */
    fun isEnabledForContext(context: ContextType): Boolean {
        return context in enabledContexts
    }
    
    /**
     * Returns the custom message for a specific app, if any
     */
    fun getCustomMessage(packageName: String): String? {
        return customMessages[packageName]
    }
}

/**
 * Tracks intervention effectiveness and learning
 */
data class InterventionLearning(
    val packageName: String,
    val contextType: ContextType,
    val successRate: Float, // 0.0 to 1.0
    val averageDelayBeforeDecision: Long,
    val mostEffectiveUrgency: InterventionUrgency,
    val mostEffectiveAction: InterventionAction,
    val totalInterventions: Int,
    val successfulInterventions: Int,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    /**
     * Returns whether this intervention type is generally effective
     */
    fun isEffective(): Boolean = successRate > 0.6f && totalInterventions >= 5
    
    /**
     * Returns a confidence score for the learning data
     */
    fun getConfidence(): Float = when {
        totalInterventions >= 20 -> 1.0f
        totalInterventions >= 10 -> 0.8f
        totalInterventions >= 5 -> 0.6f
        else -> 0.3f
    }
}

/**
 * Smart intervention strategy that adapts based on context and learning
 */
data class AdaptiveInterventionStrategy(
    val baseStrategy: InterventionAction,
    val contextModifiers: Map<ContextType, InterventionModifier> = emptyMap(),
    val timeBasedModifiers: Map<TimeRange, InterventionModifier> = emptyMap(),
    val usageThresholdModifiers: Map<UsageThreshold, InterventionModifier> = emptyMap(),
    val learningWeight: Float = 0.3f // How much to weight learned preferences
) {
    /**
     * Calculates the appropriate intervention for given context
     */
    fun calculateIntervention(
        context: ContextType,
        currentTime: Long,
        usageStats: Map<String, Long>,
        learning: InterventionLearning?
    ): InterventionDecision {
        var urgency = InterventionUrgency.LOW
        var action = baseStrategy
        
        // Apply context modifiers
        contextModifiers[context]?.let { modifier ->
            urgency = modifier.adjustUrgency(urgency)
            action = modifier.adjustAction(action)
        }
        
        // Apply learning if available and reliable
        learning?.let { data ->
            if (data.isEffective() && data.getConfidence() > 0.6f) {
                urgency = data.mostEffectiveUrgency
                action = data.mostEffectiveAction
            }
        }
        
        return InterventionDecision(
            shouldIntervene = true,
            urgency = urgency,
            message = generateContextualMessage(context, action),
            suggestedAction = action,
            confidence = learning?.getConfidence() ?: 0.5f,
            relatedContext = context
        )
    }
    
    private fun generateContextualMessage(context: ContextType, action: InterventionAction): String {
        return when (context) {
            ContextType.WORK_HOURS -> when (action) {
                InterventionAction.STRONG_BLOCK -> "Focus time! Your productivity goals are important."
                InterventionAction.FIRM_BLOCK -> "Take a break from social media during work hours?"
                else -> "Quick work break? Consider setting a timer."
            }
            ContextType.SLEEP_PREPARATION -> when (action) {
                InterventionAction.STRONG_BLOCK -> "Wind down time. Your sleep quality matters."
                InterventionAction.FIRM_BLOCK -> "Screen time before bed can affect your sleep."
                else -> "Getting ready for bed? Consider a calming activity instead."
            }
            ContextType.BEDTIME -> "It's bedtime. Your body needs rest to function at its best."
            else -> "Take a moment to consider if you really want to open this app right now."
        }
    }
}

/**
 * Modifier for intervention behavior
 */
data class InterventionModifier(
    val urgencyAdjustment: Int = 0, // -2 to +2
    val actionAdjustment: Int = 0,  // -2 to +2
    val messageOverride: String? = null,
    val additionalDelay: Long = 0L,
    val bypassRestriction: Boolean = false
) {
    fun adjustUrgency(base: InterventionUrgency): InterventionUrgency {
        val newOrdinal = (base.ordinal + urgencyAdjustment).coerceIn(0, InterventionUrgency.values().size - 1)
        return InterventionUrgency.values()[newOrdinal]
    }
    
    fun adjustAction(base: InterventionAction): InterventionAction {
        val newOrdinal = (base.ordinal + actionAdjustment).coerceIn(0, InterventionAction.values().size - 1)
        return InterventionAction.values()[newOrdinal]
    }
}

/**
 * Time range for time-based intervention modifiers
 */
data class TimeRange(
    val startHour: Int, // 0-23
    val endHour: Int,   // 0-23
    val daysOfWeek: Set<Int> = setOf(1, 2, 3, 4, 5, 6, 7) // 1=Monday, 7=Sunday
) {
    fun contains(timestamp: Long): Boolean {
        val calendar = java.util.Calendar.getInstance().apply { timeInMillis = timestamp }
        val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
        val dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
        
        return hour in startHour..endHour && dayOfWeek in daysOfWeek
    }
}

/**
 * Usage threshold for usage-based intervention modifiers
 */
data class UsageThreshold(
    val dailyLimit: Long,
    val sessionLimit: Long,
    val weeklyLimit: Long
) {
    fun isExceeded(dailyUsage: Long, sessionUsage: Long, weeklyUsage: Long): Boolean {
        return dailyUsage > dailyLimit || sessionUsage > sessionLimit || weeklyUsage > weeklyLimit
    }
} 