package com.unswipe.android.di

import android.app.Application
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.android.billingclient.api.BillingClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context = application.applicationContext

    @Provides
    @Singleton
    fun provideUsageStatsManager(@ApplicationContext context: Context): UsageStatsManager =
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    @Provides
    @Singleton
    fun providePackageManager(@ApplicationContext context: Context): PackageManager =
        context.packageManager

    // DataStore instance
    // NOTE: Name should match the one used in SettingsRepositoryImpl
    private val Context.dataStoreInstance: DataStore<Preferences> by preferencesDataStore(name = "unswipe_settings")

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.dataStoreInstance

    @Provides
    @Singleton
    fun provideBillingClient(@ApplicationContext context: Context): BillingClient =
        BillingClient.newBuilder(context)
            .setListener { _, _ -> /* Purchases Updated Listener - Handle in BillingRepository */ }
            .enablePendingPurchases()
            .build()

    @DefaultDispatcher
    @Provides
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @IoDispatcher
    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @MainDispatcher
    @Provides
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
}

// Qualifier annotations for CoroutineDispatchers
@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DefaultDispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IoDispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class MainDispatcher 