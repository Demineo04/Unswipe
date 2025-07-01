package com.unswipe.android.domain.model

/**
 * Decision model for contextual interventions
 */
data class InterventionDecision(
    val shouldIntervene: Boolean,
    val urgency: InterventionUrgency = InterventionUrgency.LOW,
    val message: String = "",
    val suggestedAction: InterventionAction = InterventionAction.GENTLE_REMINDER,
    val contextualTip: String? = null,
    val alternativeActivity: String? = null,
    val canBypass: Boolean = false,
    val bypassReason: String? = null
)

/**
 * Urgency levels for interventions
 */
enum class InterventionUrgency {
    LOW,    // Gentle nudge
    MEDIUM, // Clear warning
    HIGH    // Strong intervention
}

/**
 * Types of intervention actions
 */
enum class InterventionAction {
    GENTLE_REMINDER,    // Show subtle notification
    FIRM_BLOCK,        // Show blocking dialog
    STRONG_BLOCK,      // Show strong blocking with alternatives
    DELAY_ACCESS,      // Add delay before allowing access
    SUGGEST_BREAK,     // Suggest taking a break
    ENABLE_FOCUS_MODE  // Suggest enabling focus mode
}

/**
 * User preferences for interventions
 */
data class InterventionPreferences(
    val workTimeLimit: Long = 1800000L, // 30 minutes during work hours
    val sleepTimeStrict: Boolean = true,
    val enableWorkInterventions: Boolean = true,
    val enableSleepInterventions: Boolean = true,
    val enableStressDetection: Boolean = true,
    val interventionStyle: InterventionStyle = InterventionStyle.BALANCED
)

/**
 * Intervention style preferences
 */
enum class InterventionStyle {
    GENTLE,    // Minimal interruptions
    BALANCED,  // Moderate interventions
    STRICT     // Strong interventions
}