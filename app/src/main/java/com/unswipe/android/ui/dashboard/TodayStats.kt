package com.unswipe.android.ui.dashboard

// UI representation of today's stats
data class TodayStats(
    val swipes: Int,
    val unlocks: Int,
    val timeUsedMillis: Long
) 