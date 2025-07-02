package com.unswipe.android.data.interventions

import com.unswipe.android.data.context.ContextDetectionEngine
import com.unswipe.android.data.analytics.UsagePatternAnalyzer
import com.unswipe.android.domain.model.*
import com.unswipe.android.domain.repository.SettingsRepository
import com.unswipe.android.domain.repository.UsageRepository
import kotlinx.coroutines.flow.first
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContextualInterventionEngine @Inject constructor(
    private val contextEngine: ContextDetectionEngine,
    private val patternAnalyzer: UsagePatternAnalyzer,
    private val settingsRepository: SettingsRepository,
    private val usageRepository: UsageRepository
) {
    
    /**
     * Determines if and how to intervene when user tries to launch an app
     */
    suspend fun shouldTriggerIntervention(
        packageName: String,
        currentUsage: Long,
        sessionCount: Int
    ): InterventionDecision {
        
        val context = contextEngine.detectCurrentContext()
        val patterns = patternAnalyzer.detectUsagePatterns()
        val userPreferences = getInterventionPreferences()
        val userSettings = settingsRepository.getUserSettings().first()
        
        // Check if app is blocked
        if (!userSettings.blockedApps.contains(packageName)) {
            return InterventionDecision(
                shouldIntervene = false,
                urgency = InterventionUrgency.LOW,
                suggestedAction = InterventionAction.SHOW_DIALOG,
                message = "App is not blocked"
            )
        }
        
        return when (context) {
            ContextType.WORK_HOURS -> evaluateWorkIntervention(
                packageName, currentUsage, sessionCount, userPreferences
            )
            ContextType.SLEEP_PREPARATION -> evaluateSleepPreparationIntervention(
                packageName, currentUsage, userPreferences
            )
            ContextType.BEDTIME -> evaluateBedtimeIntervention(
                packageName, currentUsage, userPreferences
            )
            ContextType.MORNING_ROUTINE -> evaluateMorningRoutineIntervention(
                packageName, currentUsage, userPreferences
            )
            else -> evaluateGeneralIntervention(
                packageName, currentUsage, sessionCount, patterns, userPreferences
            )
        }
    }
    
    /**
     * Evaluates intervention during work hours
     */
    private suspend fun evaluateWorkIntervention(
        packageName: String,
        currentUsage: Long,
        sessionCount: Int,
        preferences: InterventionPreferences
    ): InterventionDecision {
        
        if (!preferences.enableWorkInterventions) {
            return InterventionDecision(
                shouldIntervene = false,
                urgency = InterventionUrgency.LOW,
                suggestedAction = InterventionAction.SHOW_DIALOG,
                message = "Work interventions disabled"
            )
        }
        
        val workTimeLimit = preferences.workTimeLimit
        val usagePercentage = currentUsage.toFloat() / workTimeLimit
        val appName = getAppName(packageName)
        
        return when {
            // Strong intervention for excessive usage
            currentUsage > workTimeLimit * 2 -> InterventionDecision(
                shouldIntervene = true,
                urgency = InterventionUrgency.HIGH,
                message = "You've used $appName for ${formatTime(currentUsage)} during work hours today - that's ${formatTime(currentUsage - workTimeLimit)} over your work limit.",
                suggestedAction = InterventionAction.STRONG_BLOCK,
                contextualTip = "Deep work requires sustained focus. Consider blocking social media until after work hours.",
                alternativeActivity = "Take a 5-minute productivity break: stretch, hydrate, or organize your workspace."
            )
            
            // Medium intervention for approaching limits with frequent usage
            usagePercentage > 0.8 && sessionCount > 5 -> InterventionDecision(
                shouldIntervene = true,
                urgency = InterventionUrgency.MEDIUM,
                message = "You've opened $appName $sessionCount times during work hours (${formatTime(currentUsage)} total).",
                suggestedAction = InterventionAction.FIRM_BLOCK,
                contextualTip = "Frequent app switching can reduce productivity by up to 25%. Try batching your social media time.",
                alternativeActivity = "Schedule a 10-minute social media break for later instead."
            )
            
            // Gentle intervention for moderate usage
            usagePercentage > 0.6 -> InterventionDecision(
                shouldIntervene = true,
                urgency = InterventionUrgency.LOW,
                message = "You're at ${(usagePercentage * 100).toInt()}% of your work-time limit for $appName.",
                suggestedAction = InterventionAction.GENTLE_REMINDER,
                contextualTip = "Consider wrapping up and returning to your work tasks.",
                canBypass = true,
                bypassReason = "I'll limit my usage"
            )
            
            else -> InterventionDecision(
                shouldIntervene = false,
                urgency = InterventionUrgency.LOW,
                suggestedAction = InterventionAction.SHOW_DIALOG,
                message = "No work intervention needed"
            )
        }
    }
    
    /**
     * Evaluates intervention during sleep preparation time
     */
    private suspend fun evaluateSleepPreparationIntervention(
        packageName: String,
        currentUsage: Long,
        preferences: InterventionPreferences
    ): InterventionDecision {
        
        if (!preferences.enableSleepInterventions) {
            return InterventionDecision(
                shouldIntervene = false,
                urgency = InterventionUrgency.LOW,
                suggestedAction = InterventionAction.SHOW_DIALOG,
                message = "Sleep interventions disabled"
            )
        }
        
        val appName = getAppName(packageName)
        val timeUntilBed = getTimeUntilBedtime()
        
        return when {
            // Strong intervention if very close to bedtime
            timeUntilBed < 30 && currentUsage > TimeUnit.MINUTES.toMillis(15) -> InterventionDecision(
                shouldIntervene = true,
                urgency = InterventionUrgency.HIGH,
                message = "You have only ${timeUntilBed}min until bedtime and you've already spent ${formatTime(currentUsage)} on $appName.",
                suggestedAction = InterventionAction.STRONG_BLOCK,
                contextualTip = "Blue light and stimulating content can delay sleep by 30+ minutes.",
                alternativeActivity = "Try reading, meditation, or gentle stretching to prepare for better sleep."
            )
            
            // Medium intervention during wind-down time
            currentUsage > TimeUnit.MINUTES.toMillis(10) -> InterventionDecision(
                shouldIntervene = true,
                urgency = InterventionUrgency.MEDIUM,
                message = "Wind-down time! You've spent ${formatTime(currentUsage)} on $appName with ${timeUntilBed}min until bed.",
                suggestedAction = InterventionAction.SUGGEST_BREAK,
                contextualTip = "Good sleep hygiene starts 1-2 hours before bedtime.",
                alternativeActivity = "Consider switching to calming activities like reading or listening to soft music."
            )
            
            else -> InterventionDecision(
                shouldIntervene = true,
                urgency = InterventionUrgency.LOW,
                message = "Sleep preparation time. Consider limiting screen time for better sleep quality.",
                suggestedAction = InterventionAction.GENTLE_REMINDER,
                canBypass = true,
                bypassReason = "Just a quick check"
            )
        }
    }
    
    /**
     * Evaluates intervention during bedtime
     */
    private suspend fun evaluateBedtimeIntervention(
        packageName: String,
        currentUsage: Long,
        preferences: InterventionPreferences
    ): InterventionDecision {
        
        if (!preferences.enableSleepInterventions) {
            return InterventionDecision(
                shouldIntervene = false,
                urgency = InterventionUrgency.LOW,
                suggestedAction = InterventionAction.SHOW_DIALOG,
                message = "Sleep interventions disabled"
            )
        }
        
        val appName = getAppName(packageName)
        
        return if (preferences.sleepTimeStrict) {
            InterventionDecision(
                shouldIntervene = true,
                urgency = InterventionUrgency.HIGH,
                message = "It's past your bedtime. Using screens now can seriously disrupt your sleep cycle.",
                suggestedAction = InterventionAction.STRONG_BLOCK,
                contextualTip = "Your brain needs darkness and calm to produce melatonin for quality sleep.",
                alternativeActivity = "Try deep breathing exercises, progressive muscle relaxation, or listening to a sleep story."
            )
        } else {
            InterventionDecision(
                shouldIntervene = true,
                urgency = InterventionUrgency.MEDIUM,
                message = "It's bedtime. Consider prioritizing rest for tomorrow's energy and focus.",
                suggestedAction = InterventionAction.FIRM_BLOCK,
                contextualTip = "Quality sleep improves mood, memory, and decision-making.",
                canBypass = true,
                bypassReason = "Important message to check"
            )
        }
    }
    
    /**
     * Evaluates intervention during morning routine
     */
    private suspend fun evaluateMorningRoutineIntervention(
        packageName: String,
        currentUsage: Long,
        preferences: InterventionPreferences
    ): InterventionDecision {
        
        val appName = getAppName(packageName)
        
        return when {
            currentUsage > TimeUnit.MINUTES.toMillis(30) -> InterventionDecision(
                shouldIntervene = true,
                urgency = InterventionUrgency.MEDIUM,
                message = "Good morning! You've spent ${formatTime(currentUsage)} on $appName already.",
                suggestedAction = InterventionAction.SUGGEST_BREAK,
                contextualTip = "Starting your day with intention can improve mood and productivity.",
                alternativeActivity = "Consider morning activities like exercise, meditation, or planning your day."
            )
            
            currentUsage > TimeUnit.MINUTES.toMillis(15) -> InterventionDecision(
                shouldIntervene = true,
                urgency = InterventionUrgency.LOW,
                message = "Morning time! Consider setting positive intentions for your day.",
                suggestedAction = InterventionAction.GENTLE_REMINDER,
                contextualTip = "Morning routines that avoid screens can boost energy and focus.",
                canBypass = true,
                bypassReason = "Checking important updates"
            )
            
            else -> InterventionDecision(
                shouldIntervene = false,
                urgency = InterventionUrgency.LOW,
                suggestedAction = InterventionAction.SHOW_DIALOG,
                message = "No morning intervention needed"
            )
        }
    }
    
    /**
     * Evaluates general intervention based on usage patterns
     */
    private suspend fun evaluateGeneralIntervention(
        packageName: String,
        currentUsage: Long,
        sessionCount: Int,
        patterns: List<UsagePattern>,
        preferences: InterventionPreferences
    ): InterventionDecision {
        
        val appName = getAppName(packageName)
        val dailyLimit = settingsRepository.getDailyLimitFlow().first()
        val usagePercentage = currentUsage.toFloat() / dailyLimit
        
        // Check for pattern-based interventions
        val relevantPatterns = patterns.filter { pattern ->
            pattern.apps.isEmpty() || pattern.apps.contains(packageName)
        }
        
        val bingePattern = relevantPatterns.find { it.type == PatternType.BINGE_USAGE }
        val stressPattern = relevantPatterns.find { it.type == PatternType.STRESS_USAGE }
        
        return when {
            // Strong intervention for way over daily limit
            currentUsage > dailyLimit * 1.5 -> InterventionDecision(
                shouldIntervene = true,
                urgency = InterventionUrgency.HIGH,
                message = "You've used $appName for ${formatTime(currentUsage)} today - that's ${formatTime(currentUsage - dailyLimit)} over your daily limit.",
                suggestedAction = InterventionAction.STRONG_BLOCK,
                contextualTip = "Excessive screen time can impact mental health and real-world relationships.",
                alternativeActivity = "Try going for a walk, calling a friend, or pursuing a hobby you enjoy."
            )
            
            // Medium intervention for over limit
            currentUsage > dailyLimit -> InterventionDecision(
                shouldIntervene = true,
                urgency = InterventionUrgency.MEDIUM,
                message = "You've exceeded your daily limit for $appName by ${formatTime(currentUsage - dailyLimit)}.",
                suggestedAction = InterventionAction.FIRM_BLOCK,
                contextualTip = "Taking breaks from social media can improve focus and well-being.",
                alternativeActivity = "Consider doing something offline that brings you joy."
            )
            
            // Binge pattern intervention
            bingePattern != null && bingePattern.confidence > 0.8 -> InterventionDecision(
                shouldIntervene = true,
                urgency = InterventionUrgency.MEDIUM,
                message = "You've been having longer $appName sessions lately. Current session: ${formatTime(currentUsage)}.",
                suggestedAction = InterventionAction.SUGGEST_BREAK,
                contextualTip = "Taking regular breaks can help maintain a healthy relationship with technology.",
                alternativeActivity = "Set a timer for 10 more minutes, then take a break."
            )
            
            // Stress pattern intervention
            stressPattern != null && sessionCount > 15 -> InterventionDecision(
                shouldIntervene = true,
                urgency = InterventionUrgency.LOW,
                message = "You've checked $appName $sessionCount times today. This might indicate stress or restlessness.",
                suggestedAction = InterventionAction.GENTLE_REMINDER,
                contextualTip = "Frequent checking can be a sign of anxiety. Consider grounding techniques.",
                alternativeActivity = "Try the 5-4-3-2-1 technique: 5 things you see, 4 you touch, 3 you hear, 2 you smell, 1 you taste."
            )
            
            // Approaching daily limit
            usagePercentage > 0.8 -> InterventionDecision(
                shouldIntervene = true,
                urgency = InterventionUrgency.LOW,
                message = "You're at ${(usagePercentage * 100).toInt()}% of your daily limit for $appName.",
                suggestedAction = InterventionAction.GENTLE_REMINDER,
                contextualTip = "Consider saving some time for later or other activities.",
                canBypass = true,
                bypassReason = "I'll be mindful of my usage"
            )
            
            else -> InterventionDecision(
                shouldIntervene = false,
                urgency = InterventionUrgency.LOW,
                suggestedAction = InterventionAction.SHOW_DIALOG,
                message = "No general intervention needed"
            )
        }
    }
    
    // Helper methods
    
    private suspend fun getInterventionPreferences(): InterventionPreferences {
        // This would be implemented in SettingsRepository
        // For now, return default preferences
        return InterventionPreferences()
    }
    
    private fun getAppName(packageName: String): String {
        // Simplified app name extraction
        return when (packageName) {
            "com.zhiliaoapp.musically" -> "TikTok"
            "com.instagram.android" -> "Instagram"
            "com.google.android.youtube" -> "YouTube"
            "com.facebook.katana" -> "Facebook"
            "com.snapchat.android" -> "Snapchat"
            "com.twitter.android" -> "Twitter"
            else -> packageName.split(".").lastOrNull()?.capitalize() ?: "App"
        }
    }
    
    private suspend fun getTimeUntilBedtime(): Long {
        // This would get the user's actual bedtime from settings
        val sleepTime = LocalTime.of(23, 0) // Default 11 PM
        val now = LocalTime.now()
        
        return if (now.isBefore(sleepTime)) {
            java.time.Duration.between(now, sleepTime).toMinutes()
        } else {
            0L
        }
    }
    
    private fun formatTime(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        
        return when {
            hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
            hours > 0 -> "${hours}h"
            minutes > 0 -> "${minutes}m"
            else -> "< 1m"
        }
    }
}