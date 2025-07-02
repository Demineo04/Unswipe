package com.unswipe.android.ui.confirmation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unswipe.android.domain.repository.UsageRepository
import com.unswipe.android.domain.repository.SettingsRepository
import com.unswipe.android.domain.repository.PremiumRepository
import com.unswipe.android.data.interventions.ContextualInterventionEngine
import com.unswipe.android.data.notifications.ContextAwareNotificationEngine
import com.unswipe.android.data.premium.SmartFocusModeManager
import com.unswipe.android.domain.model.InterventionDecision
import com.unswipe.android.domain.model.InterventionUrgency
import com.unswipe.android.domain.model.InterventionAction
import com.unswipe.android.domain.model.PremiumFeature
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class ConfirmationUiState(
    val isLoading: Boolean = true,
    val appName: String = "",
    val packageName: String = "",
    val todayUsageMillis: Long = 0L,
    val dailyLimitMillis: Long = 0L,
    val isOverLimit: Boolean = false,
    val isPremium: Boolean = false,
    val canBypass: Boolean = false,
    val usageMessage: String = "",
    val motivationalMessage: String = "",
    val contextualTip: String = "",
    val alternativeActivity: String = "",
    val interventionUrgency: InterventionUrgency = InterventionUrgency.LOW,
    val interventionAction: InterventionAction = InterventionAction.GENTLE_REMINDER,
    val bypassReason: String = "",
    val error: String? = null,
    // Premium features
    val bypassCreditsAvailable: Int = 0,
    val canUseBypassCredit: Boolean = false,
    val focusModeActive: String? = null,
    val canUseEmergencyBypass: Boolean = false,
    val customMessage: String? = null,
    val showUpgradePrompt: Boolean = false
) {
    val todayUsageFormatted: String
        get() = TimeUtils.formatTime(todayUsageMillis)
    
    val dailyLimitFormatted: String
        get() = TimeUtils.formatTime(dailyLimitMillis)
    
    val usagePercentage: Float
        get() = if (dailyLimitMillis > 0) {
            (todayUsageMillis.toFloat() / dailyLimitMillis.toFloat()).coerceIn(0f, 2f)
        } else 0f
}

// @HiltViewModel
class ConfirmationViewModel @Inject constructor(
    private val usageRepository: UsageRepository,
    private val settingsRepository: SettingsRepository,
    private val premiumRepository: PremiumRepository,
    private val interventionEngine: ContextualInterventionEngine,
    private val notificationEngine: ContextAwareNotificationEngine,
    private val focusModeManager: SmartFocusModeManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConfirmationUiState())
    val uiState: StateFlow<ConfirmationUiState> = _uiState.asStateFlow()

    fun loadAppData(appName: String, packageName: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    appName = appName,
                    packageName = packageName
                )

                // Get today's usage for this specific app
                val todayUsage = usageRepository.getAppUsageToday(packageName)
                val dailyLimit = settingsRepository.getDailyLimitFlow().first()
                val userSettings = settingsRepository.getUserSettings().first()
                
                // Get session count from repository
                val sessionCount = usageRepository.getSessionCountToday(packageName)
                
                // Get premium subscription info
                val premiumSubscription = premiumRepository.getPremiumSubscription()
                val isPremium = premiumSubscription.isActive && !premiumSubscription.isExpired()
                
                // Get bypass credits if premium
                val bypassCredits = if (isPremium) premiumRepository.getBypassCredits() else null
                
                // Check focus mode status
                val activeFocusMode = null // focusModeManager.getActiveFocusMode() // TODO: Implement this method
                val isBlockedByFocusMode = focusModeManager.isAppBlockedByFocusMode(packageName)
                val focusModeMessage = if (isBlockedByFocusMode) {
                    focusModeManager.getFocusModeInterventionMessage(packageName)
                } else null
                
                // Get custom intervention message if premium
                val customMessages = if (isPremium) {
                    premiumRepository.getCustomInterventionMessages()
                } else emptyMap()
                
                // Get contextual intervention decision
                val interventionDecision = interventionEngine.shouldTriggerIntervention(
                    packageName = packageName,
                    currentUsage = todayUsage,
                    sessionCount = sessionCount
                )
                
                // Trigger notification if needed
                notificationEngine.analyzeAndNotify(packageName, todayUsage, sessionCount)

                val isOverLimit = todayUsage >= dailyLimit

                // Determine final intervention message
                val finalMessage = when {
                    focusModeMessage != null -> focusModeMessage
                    customMessages.containsKey(packageName) -> customMessages[packageName]!!
                    interventionDecision.message.isNotEmpty() -> interventionDecision.message
                    else -> generateUsageMessage(appName, todayUsage, dailyLimit, isOverLimit)
                }
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    todayUsageMillis = todayUsage,
                    dailyLimitMillis = dailyLimit,
                    isOverLimit = isOverLimit,
                    isPremium = isPremium,
                    canBypass = interventionDecision.canBypass && !isBlockedByFocusMode,
                    usageMessage = finalMessage,
                    motivationalMessage = generateMotivationalMessage(appName, todayUsage, dailyLimit),
                    contextualTip = interventionDecision.contextualTip ?: "",
                    alternativeActivity = interventionDecision.alternativeActivity ?: "",
                    interventionUrgency = interventionDecision.urgency,
                    interventionAction = interventionDecision.suggestedAction,
                    bypassReason = interventionDecision.bypassReason ?: "",
                    // Premium features
                    bypassCreditsAvailable = bypassCredits?.available ?: 0,
                    canUseBypassCredit = bypassCredits?.canUseBypass == true && isPremium,
                    focusModeActive = null, // activeFocusMode?.name // TODO: Implement focus mode
                    canUseEmergencyBypass = focusModeManager.canUseEmergencyBypass() && isPremium,
                    customMessage = customMessages[packageName],
                    showUpgradePrompt = !isPremium && (isOverLimit || sessionCount > 10),
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.localizedMessage ?: "Failed to load app data"
                )
            }
        }
    }

    private fun generateUsageMessage(appName: String, todayUsage: Long, dailyLimit: Long, isOverLimit: Boolean): String {
        val usageFormatted = TimeUtils.formatTime(todayUsage)
        val limitFormatted = TimeUtils.formatTime(dailyLimit)
        
        return when {
            isOverLimit -> "You've already used $appName for $usageFormatted today (limit: $limitFormatted)"
            todayUsage > dailyLimit * 0.8 -> "You've used $appName for $usageFormatted today, approaching your $limitFormatted limit"
            todayUsage > dailyLimit * 0.5 -> "You've used $appName for $usageFormatted today (limit: $limitFormatted)"
            else -> "You've used $appName for $usageFormatted today"
        }
    }

    private fun generateMotivationalMessage(appName: String, todayUsage: Long, dailyLimit: Long): String {
        val remainingTime = (dailyLimit - todayUsage).coerceAtLeast(0)
        
        return when {
            todayUsage >= dailyLimit -> "Consider taking a break and doing something offline instead 🌱"
            remainingTime < TimeUnit.MINUTES.toMillis(30) -> "You have ${TimeUtils.formatTime(remainingTime)} left today. Make it count! ⏰"
            todayUsage > dailyLimit * 0.8 -> "You're doing great managing your screen time! 💪"
            else -> "Remember your digital wellness goals 🎯"
        }
    }

    fun recordUserDecision(didProceed: Boolean) {
        viewModelScope.launch {
            try {
                // Record the user's decision for analytics/learning
                usageRepository.recordAppLaunchAttempt(
                    packageName = _uiState.value.packageName,
                    wasBlocked = !didProceed,
                    usageAtTime = _uiState.value.todayUsageMillis
                )
            } catch (e: Exception) {
                // Don't block the user flow if recording fails
            }
        }
    }
    
    fun useBypassCredit() {
        viewModelScope.launch {
            try {
                val success = premiumRepository.useBypassCredit()
                if (success) {
                    val updatedCredits = premiumRepository.getBypassCredits()
                    _uiState.value = _uiState.value.copy(
                        bypassCreditsAvailable = updatedCredits.available,
                        canUseBypassCredit = updatedCredits.canUseBypass,
                        canBypass = true
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to use bypass credit: ${e.localizedMessage}"
                )
            }
        }
    }
    
    fun useEmergencyBypass() {
        viewModelScope.launch {
            try {
                if (focusModeManager.canUseEmergencyBypass()) {
                    _uiState.value = _uiState.value.copy(
                        canBypass = true,
                        bypassReason = "Emergency bypass used"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to use emergency bypass: ${e.localizedMessage}"
                )
            }
        }
    }
    
    fun deactivateFocusMode() {
        viewModelScope.launch {
            try {
                focusModeManager.deactivateFocusMode()
                _uiState.value = _uiState.value.copy(
                    focusModeActive = null,
                    canBypass = true
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to deactivate focus mode: ${e.localizedMessage}"
                )
            }
        }
    }
    
    fun dismissUpgradePrompt() {
        _uiState.value = _uiState.value.copy(showUpgradePrompt = false)
    }



}

object TimeUtils {
    fun formatTime(millis: Long): String {
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

