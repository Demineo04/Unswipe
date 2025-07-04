package com.unswipe.android.ui.confirmation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unswipe.android.domain.model.InterventionDecision
import com.unswipe.android.domain.model.InterventionUrgency
import com.unswipe.android.domain.model.InterventionAction
import com.unswipe.android.domain.model.ContextType
import com.unswipe.android.domain.repository.UsageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Enhanced Confirmation Activity
 */
@HiltViewModel
class EnhancedConfirmationViewModel @Inject constructor(
    private val usageRepository: UsageRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<EnhancedConfirmationUiState>(EnhancedConfirmationUiState.Loading)
    val uiState: StateFlow<EnhancedConfirmationUiState> = _uiState.asStateFlow()
    
    /**
     * Initialize the confirmation screen with app package name
     */
    fun initialize(packageName: String) {
        viewModelScope.launch {
            try {
                // Get current usage stats for the app
                val usageStats = usageRepository.getTodayUsageForApp(packageName)
                
                // Create intervention decision based on context
                val interventionDecision = createInterventionDecision(packageName, usageStats)
                
                _uiState.value = EnhancedConfirmationUiState.Ready(
                    interventionDecision = interventionDecision,
                    usageStats = usageStats
                )
            } catch (e: Exception) {
                _uiState.value = EnhancedConfirmationUiState.Error("Failed to load confirmation data: ${e.message}")
            }
        }
    }
    
    /**
     * Record user's decision and update learning data
     */
    fun recordDecision(allowed: Boolean, packageName: String) {
        viewModelScope.launch {
            try {
                // Record the decision for learning purposes
                usageRepository.recordInterventionOutcome(
                    packageName = packageName,
                    wasAllowed = allowed,
                    timestamp = System.currentTimeMillis()
                )
            } catch (e: Exception) {
                // Log error but don't block the user
            }
        }
    }
    
    private fun createInterventionDecision(packageName: String, usageTimeToday: Long): InterventionDecision {
        val currentHour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        val usageMinutes = usageTimeToday / 60000
        
        // Determine context based on time of day
        val context = when (currentHour) {
            in 9..17 -> ContextType.WORK_HOURS
            in 21..23 -> ContextType.BEDTIME
            in 19..21 -> ContextType.EVENING
            else -> ContextType.PERSONAL_TIME
        }
        
        // Determine urgency based on usage and context
        val urgency = when {
            context == ContextType.BEDTIME -> InterventionUrgency.HIGH
            context == ContextType.WORK_HOURS && usageMinutes > 120 -> InterventionUrgency.MEDIUM
            usageMinutes > 180 -> InterventionUrgency.HIGH
            usageMinutes > 60 -> InterventionUrgency.MEDIUM
            else -> InterventionUrgency.LOW
        }
        
        // Create contextual message
        val message = when (context) {
            ContextType.WORK_HOURS -> "You've been using social media during work hours. Consider focusing on your tasks."
            ContextType.BEDTIME -> "It's bedtime. Screen time before sleep can affect your rest quality."
            ContextType.EVENING -> "You've had significant screen time today. Consider a relaxing offline activity."
            else -> "Take a moment to consider if you really want to open this app right now."
        }
        
        return InterventionDecision(
            shouldIntervene = true,
            urgency = urgency,
            message = message,
            suggestedAction = InterventionAction.CONFIRMATION,
            contextualTip = getContextualTip(context, usageMinutes),
            alternativeActivity = getAlternativeActivity(context),
            confidence = 0.8f,
            relatedContext = context,
            bypassAllowed = urgency != InterventionUrgency.CRITICAL
        )
    }
    
    private fun getContextualTip(context: ContextType, usageMinutes: Long): String {
        return when (context) {
            ContextType.WORK_HOURS -> "Try the Pomodoro technique: 25 minutes of focused work, then a 5-minute break."
            ContextType.BEDTIME -> "Consider reading a book or practicing meditation instead."
            ContextType.EVENING -> "You've used this app for ${usageMinutes}min today. How about some offline time?"
            else -> "Remember your digital wellness goals."
        }
    }
    
    private fun getAlternativeActivity(context: ContextType): String {
        return when (context) {
            ContextType.WORK_HOURS -> "Focus on your current work task"
            ContextType.BEDTIME -> "Try reading or listening to calming music"
            ContextType.EVENING -> "Take a walk or call a friend"
            else -> "Take a few deep breaths and stretch"
        }
    }
}

/**
 * UI State for Enhanced Confirmation Screen
 */
sealed class EnhancedConfirmationUiState {
    object Loading : EnhancedConfirmationUiState()
    
    data class Ready(
        val interventionDecision: InterventionDecision,
        val usageStats: Long // Usage time in milliseconds
    ) : EnhancedConfirmationUiState()
    
    data class Error(val message: String) : EnhancedConfirmationUiState()
} 