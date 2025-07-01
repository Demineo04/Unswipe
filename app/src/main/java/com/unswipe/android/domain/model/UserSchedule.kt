package com.unswipe.android.domain.model

import java.time.LocalTime

/**
 * User's daily schedule for context-aware analytics
 */
data class UserSchedule(
    val wakeupTime: LocalTime = LocalTime.of(7, 0), // 7:00 AM
    val workStartTime: LocalTime = LocalTime.of(9, 0), // 9:00 AM
    val workEndTime: LocalTime = LocalTime.of(17, 0), // 5:00 PM
    val sleepTime: LocalTime = LocalTime.of(23, 0), // 11:00 PM
    val workDays: Set<Int> = setOf(1, 2, 3, 4, 5), // Monday-Friday (Calendar.MONDAY = 2)
    val isWorkScheduleEnabled: Boolean = true,
    val isSleepScheduleEnabled: Boolean = true
) {
    /**
     * Gets sleep preparation time (2 hours before bedtime)
     */
    val sleepPreparationTime: LocalTime
        get() = sleepTime.minusHours(2)
    
    /**
     * Gets morning routine end time (2 hours after wakeup)
     */
    val morningRoutineEndTime: LocalTime
        get() = wakeupTime.plusHours(2)
    
    companion object {
        val Default = UserSchedule()
    }
}