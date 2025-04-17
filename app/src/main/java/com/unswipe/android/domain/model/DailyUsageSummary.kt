package com.unswipe.android.domain.model // Correct package!

import java.time.LocalDate // Example: Use Java 8+ time if appropriate

data class DailyUsageSummary(
    val date: LocalDate,           // Example: Date for the summary
    val totalUsageMillis: Long,    // Example: Total time used
    val swipeCount: Int,           // Example: Swipes for that day
    val unlockCount: Int           // Example: Unlocks for that day
    // Add other properties relevant to the domain/UI for a daily summary
)