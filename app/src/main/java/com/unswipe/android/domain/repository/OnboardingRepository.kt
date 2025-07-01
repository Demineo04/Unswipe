package com.unswipe.android.domain.repository

import kotlinx.coroutines.flow.Flow
import java.time.LocalTime

data class UserSchedule(
    val wakeupTime: LocalTime,
    val workStartTime: LocalTime,
    val workEndTime: LocalTime,
    val sleepTime: LocalTime,
    val isOnboardingComplete: Boolean = false
)

interface OnboardingRepository {
    
    /**
     * Checks if the user has completed onboarding
     */
    suspend fun isOnboardingComplete(): Boolean
    
    /**
     * Marks onboarding as complete
     */
    suspend fun markOnboardingComplete()
    
    /**
     * Saves the user's wakeup time
     */
    suspend fun saveWakeupTime(time: LocalTime)
    
    /**
     * Saves the user's work schedule
     */
    suspend fun saveWorkSchedule(startTime: LocalTime, endTime: LocalTime)
    
    /**
     * Saves the user's sleep time
     */
    suspend fun saveSleepTime(time: LocalTime)
    
    /**
     * Gets the complete user schedule
     */
    suspend fun getUserSchedule(): UserSchedule?
    
    /**
     * Gets a flow of the user schedule for reactive updates
     */
    fun getUserScheduleFlow(): Flow<UserSchedule?>
    
    /**
     * Determines if current time is during a "critical period" 
     * (work hours, sleep time, etc.) when social media usage should be minimized
     */
    suspend fun isCurrentlyCriticalPeriod(): Boolean
    
    /**
     * Gets the current critical period type (work, sleep, etc.)
     */
    suspend fun getCurrentCriticalPeriodType(): CriticalPeriodType?
}

enum class CriticalPeriodType {
    WORK_HOURS,
    SLEEP_TIME,
    EARLY_MORNING, // First 2 hours after wakeup
    LATE_EVENING   // 2 hours before sleep
}