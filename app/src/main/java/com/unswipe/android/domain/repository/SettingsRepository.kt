package com.unswipe.android.domain.repository

import com.unswipe.android.domain.model.UserSettings
import com.unswipe.android.domain.model.UserSchedule
import com.unswipe.android.domain.model.InterventionPreferences
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    /**
     * Provides a Flow of the user's complete settings.
     * Consider if this is needed or if individual flows are better.
     */
    fun getUserSettings(): Flow<UserSettings>

    /**
     * Updates the user's chosen daily screen time limit.
     */
    suspend fun updateDailyLimit(limitMillis: Long)

    /**
     * Gets a Flow that emits the current daily limit whenever it changes.
     */
    fun getDailyLimitFlow(): Flow<Long>


    /**
     * Updates the user's premium status (e.g., after a billing change).
     */
    suspend fun setPremiumStatus(isPremium: Boolean)

    /**
     * Gets a Flow that emits the user's premium status whenever it changes.
     * This might be redundant if BillingRepository provides the authoritative status.
     * Decide where the source of truth lies. BillingRepository is likely better.
     */
    // fun getPremiumStatusFlow(): Flow<Boolean> // Consider removing if BillingRepo handles it

    /**
     * Gets a Flow that emits the set of package names marked for blocking/confirmation.
     */
    fun getBlockedApps(): Flow<Set<String>>

    /**
     * Adds an app's package name to the blocked list.
     */
    suspend fun addBlockedApp(packageName: String)

    /**
     * Removes an app's package name from the blocked list.
     */
    suspend fun removeBlockedApp(packageName: String)

    /**
     * Checks if a specific app is currently in the blocked list.
     * (Optional, might be derived from getBlockedApps().first().contains(...))
     */
    suspend fun isAppBlocked(packageName: String): Boolean

    suspend fun getTimeLimitMillis(): Long

    /**
     * Gets the current streak of days meeting usage goals.
     */
    suspend fun getCurrentStreak(): Int

    // Context-aware methods
    
    /**
     * Gets the user's daily schedule for context detection.
     */
    suspend fun getUserSchedule(): UserSchedule
    
    /**
     * Updates the user's daily schedule.
     */
    suspend fun updateUserSchedule(schedule: UserSchedule)
    
    /**
     * Gets the user's intervention preferences.
     */
    suspend fun getInterventionPreferences(): InterventionPreferences
    
    /**
     * Updates the user's intervention preferences.
     */
    suspend fun updateInterventionPreferences(preferences: InterventionPreferences)
    
    /**
     * Gets work WiFi SSIDs for location context detection.
     */
    suspend fun getWorkWifiSSIDs(): Set<String>
    
    /**
     * Adds a work WiFi SSID for location detection.
     */
    suspend fun addWorkWifiSSID(ssid: String)
    
    /**
     * Removes a work WiFi SSID.
     */
    suspend fun removeWorkWifiSSID(ssid: String)
    
    /**
     * Clears all user data and settings.
     */
    suspend fun clearAllData()
    
    /**
     * Sets blocked apps
     */
    suspend fun setBlockedApps(apps: Set<String>)
    
    /**
     * Sets daily limit in milliseconds
     */
    suspend fun setDailyLimitMillis(limitMillis: Long)
    
    /**
     * Gets premium status
     */
    suspend fun isPremium(): Boolean

}