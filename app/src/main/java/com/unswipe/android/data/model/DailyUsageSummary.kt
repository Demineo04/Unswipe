package com.unswipe.android.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_summaries")
data class DailyUsageSummary(
    @PrimaryKey val dateMillis: Long, // Start of the day (midnight) UTC
    val totalScreenTimeMillis: Long,
    val swipeCount: Int,
    val unlockCount: Int
) 