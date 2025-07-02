package com.unswipe.android.domain.model

/**
 * Decision made by the intervention engine about whether and how to intervene
 */
data class InterventionDecision(
    val shouldIntervene: Boolean,
    val urgency: InterventionUrgency,
    val suggestedAction: InterventionAction,
    val message: String,
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
    MEDIUM, // Warning
    HIGH    // Strong intervention
}

/**
 * Types of intervention actions
 */
enum class InterventionAction {
    SHOW_DIALOG,        // Show confirmation dialog
    SHOW_NOTIFICATION,  // Show notification
    BLOCK_COMPLETELY,   // Block access entirely
    DELAY_ACCESS,       // Add delay before access
    SUGGEST_ALTERNATIVE, // Suggest alternative activity
    STRONG_BLOCK,       // Strong blocking intervention
    FIRM_BLOCK,         // Firm blocking intervention
    GENTLE_REMINDER,    // Gentle reminder intervention
    SUGGEST_BREAK       // Suggest taking a break
} 