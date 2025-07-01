package com.unswipe.android.di

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.unswipe.android.data.analytics.UsagePatternAnalyzer
import com.unswipe.android.data.context.ContextDetectionEngine
import com.unswipe.android.data.interventions.ContextualInterventionEngine
import com.unswipe.android.data.notifications.ContextAwareNotificationEngine
import com.unswipe.android.domain.repository.SettingsRepository
import com.unswipe.android.domain.repository.UsageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ContextAwareModule {
    
    @Provides
    @Singleton
    fun provideNotificationManagerCompat(
        @ApplicationContext context: Context
    ): NotificationManagerCompat {
        return NotificationManagerCompat.from(context)
    }
    
    @Provides
    @Singleton
    fun provideContextDetectionEngine(
        @ApplicationContext context: Context,
        settingsRepository: SettingsRepository
    ): ContextDetectionEngine {
        return ContextDetectionEngine(context, settingsRepository)
    }
    
    @Provides
    @Singleton
    fun provideUsagePatternAnalyzer(
        usageRepository: UsageRepository
    ): UsagePatternAnalyzer {
        return UsagePatternAnalyzer(usageRepository)
    }
    
    @Provides
    @Singleton
    fun provideContextAwareNotificationEngine(
        @ApplicationContext context: Context,
        contextEngine: ContextDetectionEngine,
        patternAnalyzer: UsagePatternAnalyzer,
        usageRepository: UsageRepository,
        settingsRepository: SettingsRepository,
        notificationManager: NotificationManagerCompat
    ): ContextAwareNotificationEngine {
        return ContextAwareNotificationEngine(
            context,
            contextEngine,
            patternAnalyzer,
            usageRepository,
            settingsRepository,
            notificationManager
        )
    }
    
    @Provides
    @Singleton
    fun provideContextualInterventionEngine(
        contextEngine: ContextDetectionEngine,
        patternAnalyzer: UsagePatternAnalyzer,
        settingsRepository: SettingsRepository,
        usageRepository: UsageRepository
    ): ContextualInterventionEngine {
        return ContextualInterventionEngine(
            contextEngine,
            patternAnalyzer,
            settingsRepository,
            usageRepository
        )
    }
}