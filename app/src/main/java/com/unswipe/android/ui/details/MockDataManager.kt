package com.unswipe.android.ui.details

import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object MockDataManager {
    
    // Consistent mock data for unlocks
    val mockUnlockEvents = listOf(
        UnlockEvent(LocalTime.of(7, 30), "Morning routine"),
        UnlockEvent(LocalTime.of(8, 15), "Commute"),
        UnlockEvent(LocalTime.of(9, 0), "Work start"),
        UnlockEvent(LocalTime.of(10, 30), "Coffee break"),
        UnlockEvent(LocalTime.of(12, 0), "Lunch break"),
        UnlockEvent(LocalTime.of(14, 30), "Afternoon check"),
        UnlockEvent(LocalTime.of(17, 0), "Work end"),
        UnlockEvent(LocalTime.of(18, 30), "Dinner time"),
        UnlockEvent(LocalTime.of(20, 15), "Evening leisure"),
        UnlockEvent(LocalTime.of(22, 0), "Before bed")
    )
    
    // Consistent mock data for app launches
    val mockAppLaunches = listOf(
        AppLaunchEvent("Instagram", LocalTime.of(7, 35), 8, "Social"),
        AppLaunchEvent("WhatsApp", LocalTime.of(8, 20), 5, "Communication"),
        AppLaunchEvent("Gmail", LocalTime.of(9, 10), 12, "Productivity"),
        AppLaunchEvent("Slack", LocalTime.of(9, 25), 45, "Productivity"),
        AppLaunchEvent("Chrome", LocalTime.of(10, 15), 20, "Productivity"),
        AppLaunchEvent("YouTube", LocalTime.of(12, 30), 25, "Entertainment"),
        AppLaunchEvent("Spotify", LocalTime.of(14, 0), 120, "Entertainment"),
        AppLaunchEvent("Twitter", LocalTime.of(16, 45), 15, "Social"),
        AppLaunchEvent("Netflix", LocalTime.of(19, 30), 90, "Entertainment"),
        AppLaunchEvent("Reddit", LocalTime.of(21, 15), 30, "Social")
    )
    
    // Category breakdown
    val categoryBreakdown = mapOf(
        "Social" to 53, // minutes
        "Entertainment" to 235,
        "Productivity" to 77,
        "Communication" to 5
    )
    
    val totalScreenTime = categoryBreakdown.values.sum()
    
    // Hourly pattern for unlocks (consistent data)
    val hourlyUnlockPattern = mapOf(
        7 to 1, 8 to 1, 9 to 1, 10 to 1, 11 to 0, 12 to 1,
        13 to 0, 14 to 1, 15 to 0, 16 to 0, 17 to 1, 18 to 1,
        19 to 0, 20 to 1, 21 to 0, 22 to 1, 23 to 0
    )
}

data class UnlockEvent(
    val time: LocalTime,
    val context: String
)

data class AppLaunchEvent(
    val appName: String,
    val time: LocalTime,
    val durationMinutes: Int,
    val category: String
) 