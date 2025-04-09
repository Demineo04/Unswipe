package com.unswipe.android.data.local

import androidx.room.TypeConverter
import com.unswipe.android.data.model.EventType

class Converters {
    @TypeConverter
    fun fromEventType(value: EventType?): String? {
        return value?.name
    }

    @TypeConverter
    fun toEventType(value: String?): EventType? {
        return value?.let { runCatching { EventType.valueOf(it) }.getOrNull() }
    }
} 