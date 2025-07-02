package com.unswipe.android.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usage_events")
data class UsageEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long, // UTC timestamp in milliseconds
    val packageName: String,
    val eventType: String, // OPEN, CLOSE, SWIPE, UNLOCK
    val additionalData: String? = null // Additional context data
)
/*
enum class EventType {
    APP_OPEN, APP_CLOSE, SWIPE, SCREEN_UNLOCK
}

*/