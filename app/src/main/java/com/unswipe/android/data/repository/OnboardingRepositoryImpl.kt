package com.unswipe.android.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.unswipe.android.domain.repository.OnboardingRepository
import com.unswipe.android.domain.repository.UserSchedule
import com.unswipe.android.domain.repository.CriticalPeriodType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnboardingRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : OnboardingRepository {

    companion object {
        private val ONBOARDING_COMPLETE_KEY = booleanPreferencesKey("onboarding_complete")
        private val WAKEUP_TIME_KEY = stringPreferencesKey("wakeup_time")
        private val WORK_START_TIME_KEY = stringPreferencesKey("work_start_time")
        private val WORK_END_TIME_KEY = stringPreferencesKey("work_end_time")
        private val SLEEP_TIME_KEY = stringPreferencesKey("sleep_time")
        
        private val TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")
    }

    override suspend fun isOnboardingComplete(): Boolean {
        return dataStore.data.map { preferences ->
            preferences[ONBOARDING_COMPLETE_KEY] ?: false
        }.first()
    }

    override suspend fun markOnboardingComplete() {
        dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETE_KEY] = true
        }
    }

    override suspend fun saveWakeupTime(time: LocalTime) {
        dataStore.edit { preferences ->
            preferences[WAKEUP_TIME_KEY] = time.format(TIME_FORMATTER)
        }
    }

    override suspend fun saveWorkSchedule(startTime: LocalTime, endTime: LocalTime) {
        dataStore.edit { preferences ->
            preferences[WORK_START_TIME_KEY] = startTime.format(TIME_FORMATTER)
            preferences[WORK_END_TIME_KEY] = endTime.format(TIME_FORMATTER)
        }
    }

    override suspend fun saveSleepTime(time: LocalTime) {
        dataStore.edit { preferences ->
            preferences[SLEEP_TIME_KEY] = time.format(TIME_FORMATTER)
        }
    }

    override suspend fun getUserSchedule(): UserSchedule? {
        return dataStore.data.map { preferences ->
            val wakeupTimeStr = preferences[WAKEUP_TIME_KEY]
            val workStartTimeStr = preferences[WORK_START_TIME_KEY]
            val workEndTimeStr = preferences[WORK_END_TIME_KEY]
            val sleepTimeStr = preferences[SLEEP_TIME_KEY]
            val isComplete = preferences[ONBOARDING_COMPLETE_KEY] ?: false

            if (wakeupTimeStr != null && workStartTimeStr != null && 
                workEndTimeStr != null && sleepTimeStr != null) {
                UserSchedule(
                    wakeupTime = LocalTime.parse(wakeupTimeStr, TIME_FORMATTER),
                    workStartTime = LocalTime.parse(workStartTimeStr, TIME_FORMATTER),
                    workEndTime = LocalTime.parse(workEndTimeStr, TIME_FORMATTER),
                    sleepTime = LocalTime.parse(sleepTimeStr, TIME_FORMATTER),
                    isOnboardingComplete = isComplete
                )
            } else null
        }.first()
    }

    override fun getUserScheduleFlow(): Flow<UserSchedule?> {
        return dataStore.data.map { preferences ->
            val wakeupTimeStr = preferences[WAKEUP_TIME_KEY]
            val workStartTimeStr = preferences[WORK_START_TIME_KEY]
            val workEndTimeStr = preferences[WORK_END_TIME_KEY]
            val sleepTimeStr = preferences[SLEEP_TIME_KEY]
            val isComplete = preferences[ONBOARDING_COMPLETE_KEY] ?: false

            if (wakeupTimeStr != null && workStartTimeStr != null && 
                workEndTimeStr != null && sleepTimeStr != null) {
                UserSchedule(
                    wakeupTime = LocalTime.parse(wakeupTimeStr, TIME_FORMATTER),
                    workStartTime = LocalTime.parse(workStartTimeStr, TIME_FORMATTER),
                    workEndTime = LocalTime.parse(workEndTimeStr, TIME_FORMATTER),
                    sleepTime = LocalTime.parse(sleepTimeStr, TIME_FORMATTER),
                    isOnboardingComplete = isComplete
                )
            } else null
        }
    }

    override suspend fun isCurrentlyCriticalPeriod(): Boolean {
        return getCurrentCriticalPeriodType() != null
    }

    override suspend fun getCurrentCriticalPeriodType(): CriticalPeriodType? {
        val schedule = getUserSchedule() ?: return null
        val currentTime = LocalTime.now()

        return when {
            // Work hours
            isTimeBetween(currentTime, schedule.workStartTime, schedule.workEndTime) -> {
                CriticalPeriodType.WORK_HOURS
            }
            
            // Sleep time (handle overnight period)
            isTimeBetween(currentTime, schedule.sleepTime, schedule.wakeupTime) -> {
                CriticalPeriodType.SLEEP_TIME
            }
            
            // Early morning (first 2 hours after wakeup)
            isTimeBetween(currentTime, schedule.wakeupTime, schedule.wakeupTime.plusHours(2)) -> {
                CriticalPeriodType.EARLY_MORNING
            }
            
            // Late evening (2 hours before sleep)
            isTimeBetween(currentTime, schedule.sleepTime.minusHours(2), schedule.sleepTime) -> {
                CriticalPeriodType.LATE_EVENING
            }
            
            else -> null
        }
    }

    private fun isTimeBetween(current: LocalTime, start: LocalTime, end: LocalTime): Boolean {
        return if (start.isBefore(end)) {
            // Normal case: start and end on same day
            current.isAfter(start) && current.isBefore(end)
        } else {
            // Overnight case: start is before midnight, end is after midnight
            current.isAfter(start) || current.isBefore(end)
        }
    }
}