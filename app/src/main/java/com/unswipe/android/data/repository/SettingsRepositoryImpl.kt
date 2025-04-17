package com.unswipe.android.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.* // Import core keys etc.
import androidx.datastore.preferences.core.edit // Import edit
import com.unswipe.android.domain.model.UserSettings
import com.unswipe.android.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf // For placeholder flow
import kotlinx.coroutines.flow.map // For reading data
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    // Define Preference Keys (example)
    companion object {
        val DAILY_LIMIT_KEY = longPreferencesKey("daily_limit_millis")
        val CURRENT_STREAK_KEY = intPreferencesKey("current_streak")
        val IS_PREMIUM_KEY = booleanPreferencesKey("is_premium")
        val BLOCKED_APPS_KEY = stringSetPreferencesKey("blocked_apps")
        // Define others as needed
    }

    // --- ADD Stubs or Basic Implementations for ALL interface methods ---

    override fun getUserSettings(): Flow<UserSettings> {
        return dataStore.data.map { prefs ->
            UserSettings(
                dailyUsageLimitMillis = prefs[DAILY_LIMIT_KEY] ?: 10800000L, // Default 3 hours
                currentStreak = prefs[CURRENT_STREAK_KEY] ?: 0,
                isPremium = prefs[IS_PREMIUM_KEY] ?: false,
                blockedApps = prefs[BLOCKED_APPS_KEY] ?: emptySet()
            )
        }
    }

    override suspend fun updateDailyLimit(limitMillis: Long) {
        dataStore.edit { settings ->
            settings[DAILY_LIMIT_KEY] = limitMillis
        }
    }

    override fun getDailyLimitFlow(): Flow<Long> {
        return dataStore.data.map { prefs ->
            prefs[DAILY_LIMIT_KEY] ?: 10800000L // Default 3 hours
        }
    }

    override suspend fun updateStreak(streak: Int) {
        dataStore.edit { settings ->
            settings[CURRENT_STREAK_KEY] = streak
        }
    }

    override suspend fun resetStreak() {
        updateStreak(0)
    }

    override suspend fun incrementStreak() {
        dataStore.edit { settings ->
            val currentStreak = settings[CURRENT_STREAK_KEY] ?: 0
            settings[CURRENT_STREAK_KEY] = currentStreak + 1
        }
    }

    override fun getStreakFlow(): Flow<Int> {
        return dataStore.data.map { prefs ->
            prefs[CURRENT_STREAK_KEY] ?: 0
        }
    }

    override suspend fun setPremiumStatus(isPremium: Boolean) {
        dataStore.edit { settings ->
            settings[IS_PREMIUM_KEY] = isPremium
        }
    }

    override fun getBlockedApps(): Flow<Set<String>> {
        return dataStore.data.map { prefs ->
            prefs[BLOCKED_APPS_KEY] ?: emptySet()
        }
    }

    override suspend fun addBlockedApp(packageName: String) {
        dataStore.edit { settings ->
            val currentBlocked = settings[BLOCKED_APPS_KEY] ?: emptySet()
            settings[BLOCKED_APPS_KEY] = currentBlocked + packageName
        }
    }

    override suspend fun removeBlockedApp(packageName: String) {
        dataStore.edit { settings ->
            val currentBlocked = settings[BLOCKED_APPS_KEY] ?: emptySet()
            settings[BLOCKED_APPS_KEY] = currentBlocked - packageName
        }
    }

    override suspend fun isAppBlocked(packageName: String): Boolean {
        // This could be implemented more efficiently if needed often,
        // but reading from the flow is also possible.
        // For a suspend fun, reading once might be okay:
        var isBlocked = false
        dataStore.data.map { prefs -> prefs[BLOCKED_APPS_KEY] ?: emptySet() }.collect { blockedSet ->
            isBlocked = blockedSet.contains(packageName)
        }
        return isBlocked
        // Or throw NotImplementedError("Implement if needed directly")
    }

    override suspend fun getTimeLimitMillis(): Long {
        TODO("Not yet implemented")
    }
}