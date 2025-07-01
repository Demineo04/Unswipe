package com.unswipe.android.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.core.edit
import com.unswipe.android.domain.model.UserSettings
import com.unswipe.android.domain.model.UserSchedule
import com.unswipe.android.domain.model.InterventionPreferences
import com.unswipe.android.domain.model.InterventionStyle
import com.unswipe.android.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import java.time.LocalTime
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    // Define Preference Keys
    companion object {
        val DAILY_LIMIT_KEY = longPreferencesKey("daily_limit_millis")
        val IS_PREMIUM_KEY = booleanPreferencesKey("is_premium")
        val BLOCKED_APPS_KEY = stringSetPreferencesKey("blocked_apps")
        val CURRENT_STREAK_KEY = intPreferencesKey("current_streak")
        
        // User Schedule Keys
        val WAKEUP_HOUR_KEY = intPreferencesKey("wakeup_hour")
        val WAKEUP_MINUTE_KEY = intPreferencesKey("wakeup_minute")
        val WORK_START_HOUR_KEY = intPreferencesKey("work_start_hour")
        val WORK_START_MINUTE_KEY = intPreferencesKey("work_start_minute")
        val WORK_END_HOUR_KEY = intPreferencesKey("work_end_hour")
        val WORK_END_MINUTE_KEY = intPreferencesKey("work_end_minute")
        val SLEEP_HOUR_KEY = intPreferencesKey("sleep_hour")
        val SLEEP_MINUTE_KEY = intPreferencesKey("sleep_minute")
        val WORK_DAYS_KEY = stringPreferencesKey("work_days")
        val WORK_SCHEDULE_ENABLED_KEY = booleanPreferencesKey("work_schedule_enabled")
        val SLEEP_SCHEDULE_ENABLED_KEY = booleanPreferencesKey("sleep_schedule_enabled")
        
        // Intervention Preferences Keys
        val WORK_TIME_LIMIT_KEY = longPreferencesKey("work_time_limit")
        val SLEEP_TIME_STRICT_KEY = booleanPreferencesKey("sleep_time_strict")
        val ENABLE_WORK_INTERVENTIONS_KEY = booleanPreferencesKey("enable_work_interventions")
        val ENABLE_SLEEP_INTERVENTIONS_KEY = booleanPreferencesKey("enable_sleep_interventions")
        val ENABLE_STRESS_DETECTION_KEY = booleanPreferencesKey("enable_stress_detection")
        val INTERVENTION_STYLE_KEY = stringPreferencesKey("intervention_style")
        
        // Work WiFi Keys
        val WORK_WIFI_SSIDS_KEY = stringSetPreferencesKey("work_wifi_ssids")
        
        // Default blocked apps for social media tracking
        val DEFAULT_BLOCKED_APPS = setOf(
            "com.zhiliaoapp.musically", // TikTok
            "com.instagram.android",    // Instagram
            "com.google.android.youtube" // YouTube
        )
    }

    // --- ADD Stubs or Basic Implementations for ALL interface methods ---

    override fun getUserSettings(): Flow<UserSettings> {
        return dataStore.data.map { prefs ->
            UserSettings(
                dailyUsageLimitMillis = prefs[DAILY_LIMIT_KEY] ?: 10800000L, // Default 3 hours
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


    override suspend fun setPremiumStatus(isPremium: Boolean) {
        dataStore.edit { settings ->
            settings[IS_PREMIUM_KEY] = isPremium
        }
    }

    override fun getBlockedApps(): Flow<Set<String>> {
        return dataStore.data.map { prefs ->
            prefs[BLOCKED_APPS_KEY] ?: DEFAULT_BLOCKED_APPS
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
        // Read the current blocked set once from DataStore. Using `first()`
        // avoids collecting the flow indefinitely.
        val blockedSet = dataStore.data
            .map { prefs -> prefs[BLOCKED_APPS_KEY] ?: DEFAULT_BLOCKED_APPS }
            .first()
        return blockedSet.contains(packageName)
    }

    override suspend fun getTimeLimitMillis(): Long {
        // Provide the stored daily limit or a default value if absent.
        val prefs = dataStore.data.first()
        return prefs[DAILY_LIMIT_KEY] ?: 10800000L // Default 3 hours
    }

    // Implement the new suspend function
    override suspend fun getCurrentStreak(): Int {
        // Obtain the streak value from DataStore once.
        val prefs = dataStore.data.first()
        return prefs[CURRENT_STREAK_KEY] ?: 0
    }

    // Context-aware methods implementation
    
    override suspend fun getUserSchedule(): UserSchedule {
        val prefs = dataStore.data.first()
        
        val wakeupTime = LocalTime.of(
            prefs[WAKEUP_HOUR_KEY] ?: 7,
            prefs[WAKEUP_MINUTE_KEY] ?: 0
        )
        
        val workStartTime = LocalTime.of(
            prefs[WORK_START_HOUR_KEY] ?: 9,
            prefs[WORK_START_MINUTE_KEY] ?: 0
        )
        
        val workEndTime = LocalTime.of(
            prefs[WORK_END_HOUR_KEY] ?: 17,
            prefs[WORK_END_MINUTE_KEY] ?: 0
        )
        
        val sleepTime = LocalTime.of(
            prefs[SLEEP_HOUR_KEY] ?: 23,
            prefs[SLEEP_MINUTE_KEY] ?: 0
        )
        
        val workDaysString = prefs[WORK_DAYS_KEY] ?: "1,2,3,4,5"
        val workDays = workDaysString.split(",").mapNotNull { it.toIntOrNull() }.toSet()
        
        return UserSchedule(
            wakeupTime = wakeupTime,
            workStartTime = workStartTime,
            workEndTime = workEndTime,
            sleepTime = sleepTime,
            workDays = workDays,
            isWorkScheduleEnabled = prefs[WORK_SCHEDULE_ENABLED_KEY] ?: true,
            isSleepScheduleEnabled = prefs[SLEEP_SCHEDULE_ENABLED_KEY] ?: true
        )
    }
    
    override suspend fun updateUserSchedule(schedule: UserSchedule) {
        dataStore.edit { prefs ->
            prefs[WAKEUP_HOUR_KEY] = schedule.wakeupTime.hour
            prefs[WAKEUP_MINUTE_KEY] = schedule.wakeupTime.minute
            prefs[WORK_START_HOUR_KEY] = schedule.workStartTime.hour
            prefs[WORK_START_MINUTE_KEY] = schedule.workStartTime.minute
            prefs[WORK_END_HOUR_KEY] = schedule.workEndTime.hour
            prefs[WORK_END_MINUTE_KEY] = schedule.workEndTime.minute
            prefs[SLEEP_HOUR_KEY] = schedule.sleepTime.hour
            prefs[SLEEP_MINUTE_KEY] = schedule.sleepTime.minute
            prefs[WORK_DAYS_KEY] = schedule.workDays.joinToString(",")
            prefs[WORK_SCHEDULE_ENABLED_KEY] = schedule.isWorkScheduleEnabled
            prefs[SLEEP_SCHEDULE_ENABLED_KEY] = schedule.isSleepScheduleEnabled
        }
    }
    
    override suspend fun getInterventionPreferences(): InterventionPreferences {
        val prefs = dataStore.data.first()
        
        val styleString = prefs[INTERVENTION_STYLE_KEY] ?: "BALANCED"
        val interventionStyle = try {
            InterventionStyle.valueOf(styleString)
        } catch (e: IllegalArgumentException) {
            InterventionStyle.BALANCED
        }
        
        return InterventionPreferences(
            workTimeLimit = prefs[WORK_TIME_LIMIT_KEY] ?: 1800000L, // 30 minutes
            sleepTimeStrict = prefs[SLEEP_TIME_STRICT_KEY] ?: true,
            enableWorkInterventions = prefs[ENABLE_WORK_INTERVENTIONS_KEY] ?: true,
            enableSleepInterventions = prefs[ENABLE_SLEEP_INTERVENTIONS_KEY] ?: true,
            enableStressDetection = prefs[ENABLE_STRESS_DETECTION_KEY] ?: true,
            interventionStyle = interventionStyle
        )
    }
    
    override suspend fun updateInterventionPreferences(preferences: InterventionPreferences) {
        dataStore.edit { prefs ->
            prefs[WORK_TIME_LIMIT_KEY] = preferences.workTimeLimit
            prefs[SLEEP_TIME_STRICT_KEY] = preferences.sleepTimeStrict
            prefs[ENABLE_WORK_INTERVENTIONS_KEY] = preferences.enableWorkInterventions
            prefs[ENABLE_SLEEP_INTERVENTIONS_KEY] = preferences.enableSleepInterventions
            prefs[ENABLE_STRESS_DETECTION_KEY] = preferences.enableStressDetection
            prefs[INTERVENTION_STYLE_KEY] = preferences.interventionStyle.name
        }
    }
    
    override suspend fun getWorkWifiSSIDs(): Set<String> {
        val prefs = dataStore.data.first()
        return prefs[WORK_WIFI_SSIDS_KEY] ?: emptySet()
    }
    
    override suspend fun addWorkWifiSSID(ssid: String) {
        dataStore.edit { prefs ->
            val currentSSIDs = prefs[WORK_WIFI_SSIDS_KEY] ?: emptySet()
            prefs[WORK_WIFI_SSIDS_KEY] = currentSSIDs + ssid
        }
    }
    
    override suspend fun removeWorkWifiSSID(ssid: String) {
        dataStore.edit { prefs ->
            val currentSSIDs = prefs[WORK_WIFI_SSIDS_KEY] ?: emptySet()
            prefs[WORK_WIFI_SSIDS_KEY] = currentSSIDs - ssid
        }
    }
}