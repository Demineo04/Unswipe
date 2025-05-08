package com.unswipe.android.domain.model

data class TodayStats(
    val totalUsageMillis: Long,
    val swipeCount: Int,
    val unlockCount: Int
) 