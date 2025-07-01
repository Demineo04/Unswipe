package com.unswipe.android.data.premium

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import com.unswipe.android.data.context.ContextDetectionEngine
import com.unswipe.android.domain.model.*
import com.unswipe.android.domain.repository.PremiumRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmartFocusModeManager @Inject constructor(
    private val context: Context,
    private val premiumRepository: PremiumRepository,
    private val contextEngine: ContextDetectionEngine
) {
    
    /**
     * Monitors triggers and automatically activates/deactivates focus modes
     */
    fun monitorFocusModeTriggers(): Flow<SmartFocusMode?> {
        return combine(
            premiumRepository.getActiveFocusModeFlow(),
            contextEngine.getCurrentDeviceStateFlow(),
            getCurrentTimeFlow()
        ) { activeFocusMode, deviceState, currentTime ->
            
            // Check if current focus mode should be deactivated
            activeFocusMode?.let { focusMode ->
                if (shouldDeactivateFocusMode(focusMode, deviceState, currentTime)) {
                    premiumRepository.deactivateFocusMode()
                    return@combine null
                }
            }
            
            // If no active focus mode, check if any should be activated
            if (activeFocusMode == null) {
                val focusModeToActivate = findFocusModeToActivate(deviceState, currentTime)
                focusModeToActivate?.let { focusMode ->
                    premiumRepository.activateFocusMode(focusMode.id)
                    return@combine focusMode
                }
            }
            
            activeFocusMode
        }.distinctUntilChanged()
    }
    
    /**
     * Creates predefined focus modes for common scenarios
     */
    suspend fun createDefaultFocusModes() {
        if (!premiumRepository.hasFeature(PremiumFeature.SMART_FOCUS_MODES)) return
        
        val defaultModes = listOf(
            createWorkFocusMode(),
            createSleepFocusMode(),
            createStudyFocusMode(),
            createMeetingFocusMode(),
            createExerciseFocusMode()
        )
        
        defaultModes.forEach { focusMode ->
            premiumRepository.saveSmartFocusMode(focusMode)
        }
    }
    
    /**
     * Checks if an app is blocked by the current focus mode
     */
    suspend fun isAppBlockedByFocusMode(packageName: String): Boolean {
        val activeFocusMode = premiumRepository.getActiveFocusMode()
        return activeFocusMode?.blockedApps?.contains(packageName) == true
    }
    
    /**
     * Gets focus mode specific intervention message
     */
    suspend fun getFocusModeInterventionMessage(packageName: String): String? {
        val activeFocusMode = premiumRepository.getActiveFocusMode() ?: return null
        
        if (!activeFocusMode.blockedApps.contains(packageName)) return null
        
        return activeFocusMode.customMessage ?: generateDefaultFocusMessage(activeFocusMode.name, packageName)
    }
    
    /**
     * Allows emergency bypass if enabled for the focus mode
     */
    suspend fun canUseEmergencyBypass(): Boolean {
        val activeFocusMode = premiumRepository.getActiveFocusMode()
        return activeFocusMode?.allowEmergencyBypass == true
    }
    
    /**
     * Manually activates a focus mode
     */
    suspend fun activateFocusMode(focusModeId: String) {
        premiumRepository.activateFocusMode(focusModeId)
    }
    
    /**
     * Manually deactivates the current focus mode
     */
    suspend fun deactivateFocusMode() {
        premiumRepository.deactivateFocusMode()
    }
    
    /**
     * Creates a custom focus mode
     */
    suspend fun createCustomFocusMode(
        name: String,
        blockedApps: Set<String>,
        triggers: Set<FocusTrigger>,
        duration: Long? = null,
        customMessage: String? = null
    ): SmartFocusMode {
        val focusMode = SmartFocusMode(
            id = UUID.randomUUID().toString(),
            name = name,
            isEnabled = true,
            blockedApps = blockedApps,
            triggers = triggers,
            duration = duration,
            allowEmergencyBypass = true,
            customMessage = customMessage
        )
        
        premiumRepository.saveSmartFocusMode(focusMode)
        return focusMode
    }
    
    // Private helper methods
    
    private suspend fun shouldDeactivateFocusMode(
        focusMode: SmartFocusMode,
        deviceState: DeviceState,
        currentTime: LocalDateTime
    ): Boolean {
        // Check duration-based deactivation
        focusMode.duration?.let { duration ->
            // This would need activation timestamp tracking
            // For now, simplified logic
            return false
        }
        
        // Check if triggers are no longer active
        return !focusMode.triggers.any { trigger ->
            isTriggerActive(trigger, deviceState, currentTime)
        }
    }
    
    private suspend fun findFocusModeToActivate(
        deviceState: DeviceState,
        currentTime: LocalDateTime
    ): SmartFocusMode? {
        val allFocusModes = premiumRepository.getSmartFocusModes()
        
        return allFocusModes
            .filter { it.isEnabled }
            .find { focusMode ->
                focusMode.triggers.any { trigger ->
                    isTriggerActive(trigger, deviceState, currentTime)
                }
            }
    }
    
    private fun isTriggerActive(
        trigger: FocusTrigger,
        deviceState: DeviceState,
        currentTime: LocalDateTime
    ): Boolean {
        return when (trigger) {
            is FocusTrigger.Location -> {
                val currentSSID = getCurrentWifiSSID()
                currentSSID != null && trigger.wifiSSIDs.contains(currentSSID)
            }
            
            is FocusTrigger.TimeRange -> {
                val currentHour = currentTime.hour
                val currentDay = getCurrentDayOfWeek()
                
                trigger.days.contains(currentDay) &&
                currentHour >= trigger.startHour &&
                currentHour < trigger.endHour
            }
            
            is FocusTrigger.CalendarEvent -> {
                // TODO: Implement calendar event checking
                false
            }
            
            is FocusTrigger.UsageThreshold -> {
                // TODO: Implement usage threshold checking
                false
            }
        }
    }
    
    private fun getCurrentWifiSSID(): String? {
        if (!isConnectedToWifi()) return null
        
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        return wifiInfo.ssid?.replace("\"", "")
    }
    
    private fun isConnectedToWifi(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo?.type == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected
    }
    
    private fun getCurrentDayOfWeek(): Int {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
    }
    
    private fun getCurrentTimeFlow(): Flow<LocalDateTime> {
        // This would ideally be a flow that emits every minute
        // For now, simplified implementation
        return kotlinx.coroutines.flow.flow {
            while (true) {
                emit(LocalDateTime.now())
                kotlinx.coroutines.delay(60000) // Check every minute
            }
        }
    }
    
    private fun generateDefaultFocusMessage(focusModeName: String, packageName: String): String {
        val appName = getAppName(packageName)
        
        return when (focusModeName.lowercase()) {
            "work focus", "deep work" -> "You're in Work Focus mode. $appName can wait until your focus session is complete. 💼"
            "sleep mode", "bedtime" -> "Sleep mode is active. Using $appName now could disrupt your sleep cycle. 🌙"
            "study focus" -> "Study mode is on. Stay focused on your learning goals instead of $appName. 📚"
            "meeting focus" -> "You're in a meeting. $appName notifications are paused to help you stay present. 🤝"
            "exercise focus" -> "Workout time! Focus on your fitness goals instead of $appName. 💪"
            else -> "$focusModeName is active. Consider staying focused on your current activity. ✨"
        }
    }
    
    private fun getAppName(packageName: String): String {
        return try {
            val packageManager = context.packageManager
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            packageName.split(".").lastOrNull()?.replaceFirstChar { 
                if (it.isLowerCase()) it.titlecase() else it.toString() 
            } ?: "App"
        }
    }
    
    // Predefined focus mode creators
    
    private fun createWorkFocusMode(): SmartFocusMode {
        return SmartFocusMode(
            id = "work_focus_default",
            name = "Work Focus",
            isEnabled = true,
            blockedApps = setOf(
                "com.instagram.android",
                "com.zhiliaoapp.musically", // TikTok
                "com.snapchat.android",
                "com.facebook.katana",
                "com.twitter.android"
            ),
            triggers = setOf(
                FocusTrigger.TimeRange(
                    startHour = 9,
                    endHour = 17,
                    days = setOf(2, 3, 4, 5, 6) // Monday to Friday
                )
            ),
            allowEmergencyBypass = true,
            customMessage = "Work Focus is active. Stay productive and save social media for break time! 💼"
        )
    }
    
    private fun createSleepFocusMode(): SmartFocusMode {
        return SmartFocusMode(
            id = "sleep_focus_default",
            name = "Sleep Mode",
            isEnabled = true,
            blockedApps = setOf(
                "com.instagram.android",
                "com.zhiliaoapp.musically",
                "com.google.android.youtube",
                "com.netflix.mediaclient",
                "com.snapchat.android"
            ),
            triggers = setOf(
                FocusTrigger.TimeRange(
                    startHour = 22,
                    endHour = 7,
                    days = setOf(1, 2, 3, 4, 5, 6, 7) // All days
                )
            ),
            allowEmergencyBypass = false,
            customMessage = "Sleep mode is protecting your rest. Your brain needs this time to recharge! 🌙"
        )
    }
    
    private fun createStudyFocusMode(): SmartFocusMode {
        return SmartFocusMode(
            id = "study_focus_default",
            name = "Study Focus",
            isEnabled = false, // Disabled by default, user can enable
            blockedApps = setOf(
                "com.instagram.android",
                "com.zhiliaoapp.musically",
                "com.google.android.youtube",
                "com.snapchat.android",
                "com.facebook.katana",
                "com.twitter.android",
                "com.reddit.frontpage"
            ),
            triggers = emptySet(), // Manual activation only
            allowEmergencyBypass = true,
            customMessage = "Study Focus is on. Your future self will thank you for staying focused! 📚"
        )
    }
    
    private fun createMeetingFocusMode(): SmartFocusMode {
        return SmartFocusMode(
            id = "meeting_focus_default",
            name = "Meeting Focus",
            isEnabled = false,
            blockedApps = setOf(
                "com.instagram.android",
                "com.zhiliaoapp.musically",
                "com.snapchat.android",
                "com.facebook.katana",
                "com.twitter.android",
                "com.reddit.frontpage"
            ),
            triggers = setOf(
                FocusTrigger.CalendarEvent(
                    keywords = setOf("meeting", "call", "conference", "standup", "sync")
                )
            ),
            duration = 3600000, // 1 hour
            allowEmergencyBypass = true,
            customMessage = "Meeting in progress. Stay present and engaged with your colleagues! 🤝"
        )
    }
    
    private fun createExerciseFocusMode(): SmartFocusMode {
        return SmartFocusMode(
            id = "exercise_focus_default",
            name = "Exercise Focus",
            isEnabled = false,
            blockedApps = setOf(
                "com.instagram.android",
                "com.zhiliaoapp.musically",
                "com.snapchat.android",
                "com.facebook.katana",
                "com.twitter.android"
            ),
            triggers = emptySet(), // Manual activation
            duration = 3600000, // 1 hour
            allowEmergencyBypass = true,
            customMessage = "Workout time! Focus on your fitness goals and feel the endorphins! 💪"
        )
    }
}

// Extension function for device state flow (placeholder)
private fun ContextDetectionEngine.getCurrentDeviceStateFlow(): Flow<DeviceState> {
    return kotlinx.coroutines.flow.flow {
        while (true) {
            emit(getCurrentDeviceState())
            kotlinx.coroutines.delay(30000) // Check every 30 seconds
        }
    }
}