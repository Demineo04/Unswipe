package com.unswipe.android.data.local // Or wherever you placed it

/*
import androidx.room.TypeConverter
import com.unswipe.android.data.model.EventType // Import your Enum

class Converters {
    @TypeConverter
    fun fromEventType(value: EventType?): String? {
        // Converts the Enum to a String for storing in the database
        return value?.name // Store the enum's name as a String
    }

    @TypeConverter
    fun toEventType(value: String?): EventType? {
        // Converts the String from the database back to the Enum
        // Use valueOf carefully, handle potential null or invalid strings
        return value?.let {
            try {
                EventType.valueOf(it)
            } catch (e: IllegalArgumentException) {
                null // Handle cases where the stored string isn't a valid enum name
            }
        }
    }

    // Add other converters here if needed for other types
}

 */