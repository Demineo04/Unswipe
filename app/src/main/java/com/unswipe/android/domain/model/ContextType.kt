package com.unswipe.android.domain.model

/**
 * Represents different life contexts for context-aware digital wellness insights
 */
enum class ContextType {
    /**
     * During user's defined work hours
     */
    WORK_HOURS,
    
    /**
     * 1-2 hours before user's bedtime - critical for sleep hygiene
     */
    SLEEP_PREPARATION,
    
    /**
     * Past user's bedtime - should discourage usage
     */
    BEDTIME,
    
    /**
     * First 1-2 hours after waking up
     */
    MORNING_ROUTINE,
    
    /**
     * Personal time outside work/sleep hours
     */
    PERSONAL_TIME,
    
    /**
     * Detected commute time (future enhancement)
     */
    COMMUTE,
    
    /**
     * Context could not be determined
     */
    UNKNOWN
}

/**
 * Device state information for context detection
 */
data class DeviceState(
    val isCharging: Boolean = false,
    val batteryLevel: Int = 100,
    val screenBrightness: Float = 1.0f,
    val isConnectedToWifi: Boolean = false,
    val wifiSSID: String? = null,
    val isHeadphonesConnected: Boolean = false
)

/**
 * Location context for privacy-friendly location awareness
 */
enum class LocationContext {
    HOME,
    WORK,
    OTHER,
    UNKNOWN
}