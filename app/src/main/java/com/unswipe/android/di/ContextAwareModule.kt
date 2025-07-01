package com.unswipe.android.di

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.unswipe.android.data.analytics.UsagePatternAnalyzer
import com.unswipe.android.data.context.ContextDetectionEngine
import com.unswipe.android.data.interventions.ContextualInterventionEngine
import com.unswipe.android.data.notifications.ContextAwareNotificationEngine
import com.unswipe.android.data.premium.AdvancedAnalyticsEngine
import com.unswipe.android.data.premium.SmartFocusModeManager
import com.unswipe.android.data.repository.PremiumRepositoryImpl
import com.unswipe.android.domain.repository.PremiumRepository
import com.unswipe.android.domain.repository.SettingsRepository
import com.unswipe.android.domain.repository.UsageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
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

    // Premium Features

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
    }

    @Provides
    @Singleton
    fun providePremiumRepository(
        dataStore: DataStore<Preferences>,
        usageRepository: UsageRepository,
        json: Json
    ): PremiumRepository {
        return PremiumRepositoryImpl(dataStore, usageRepository, json)
    }

    @Provides
    @Singleton
    fun provideSmartFocusModeManager(
        @ApplicationContext context: Context,
        premiumRepository: PremiumRepository,
        contextEngine: ContextDetectionEngine
    ): SmartFocusModeManager {
        return SmartFocusModeManager(context, premiumRepository, contextEngine)
    }

    @Provides
    @Singleton
    fun provideAdvancedAnalyticsEngine(
        premiumRepository: PremiumRepository,
        usageRepository: UsageRepository,
        settingsRepository: SettingsRepository
    ): AdvancedAnalyticsEngine {
        return AdvancedAnalyticsEngine(premiumRepository, usageRepository, settingsRepository)
    }
}