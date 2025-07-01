package com.unswipe.android.domain.model

/**
 * User preferences for intervention behavior
 */
data class InterventionPreferences(
    val workTimeLimit: Long = 1800000L, // 30 minutes in milliseconds
    val sleepTimeStrict: Boolean = true,
    val enableWorkInterventions: Boolean = true,
    val enableSleepInterventions: Boolean = true,
    val enableStressDetection: Boolean = true,
    val interventionStyle: InterventionStyle = InterventionStyle.BALANCED
) {
    companion object {
        val Default = InterventionPreferences()
    }
}

enum class InterventionStyle {
    GENTLE,     // Minimal interruption, gentle nudges
    BALANCED,   // Moderate intervention with educational content
    STRICT      // Strong interventions with immediate blocking
}