package com.unswipe.android.domain.repository

import com.unswipe.android.domain.model.UserSettings // Assuming you created this in domain/model
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
     * Updates the user's current streak count directly (use with caution).
     */
    suspend fun updateStreak(streak: Int)

    /**
     * Resets the user's streak count to zero.
     */
    suspend fun resetStreak()

    /**
     * Increments the user's streak count by one.
     */
    suspend fun incrementStreak()

    /**
     * Gets a Flow that emits the current streak count whenever it changes.
     */
    fun getStreakFlow(): Flow<Int>

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


}