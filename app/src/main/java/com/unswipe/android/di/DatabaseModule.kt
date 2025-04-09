package com.unswipe.android.di

import android.content.Context
import androidx.room.Room
import com.unswipe.android.data.local.AppDatabase
import com.unswipe.android.data.local.dao.UsageDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "unswipe_database"
        )
        // .addMigrations(...) // Add migrations if schema changes
        .fallbackToDestructiveMigration() // Use only during development! Replace with proper migrations.
        .build()
    }

    @Provides
    @Singleton
    fun provideUsageDao(appDatabase: AppDatabase): UsageDao {
        return appDatabase.usageDao()
    }
} 