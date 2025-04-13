package com.unswipe.android.di

import android.content.Context
import androidx.room.Room
import com.unswipe.android.data.local.AppDatabase // Import DB class
import com.unswipe.android.data.local.dao.UsageDao // Import DAO interface
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase { // Provides the DB
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "unswipe_database"
        )
            .fallbackToDestructiveMigration() // DEV ONLY - Replace with real migrations
            .build()
    }

    @Provides
    @Singleton // DAOs provided by Hilt are typically Singletons scoped to the DB instance
    fun provideUsageDao(appDatabase: AppDatabase): UsageDao { // Provides the DAO from the DB
        return appDatabase.usageDao()
    }
}