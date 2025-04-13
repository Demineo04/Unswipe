package com.unswipe.android.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.unswipe.android.data.local.dao.UsageDao
import com.unswipe.android.data.model.DailyUsageSummary
import com.unswipe.android.data.model.UsageEvent

@Database(
    entities = [UsageEvent::class, DailyUsageSummary::class],
    version = 1, // Increment version on schema changes
    exportSchema = false // Set to true for schema export during development/migration testing
)
// @TypeConverters(Converters::class) // Add if you need type converters (e.g., for Date, Enum)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usageDao(): UsageDao
} 