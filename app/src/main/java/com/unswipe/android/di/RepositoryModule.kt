package com.unswipe.android.di

import com.unswipe.android.data.repository.AuthRepositoryImpl
import com.unswipe.android.data.repository.BillingRepositoryImpl
import com.unswipe.android.data.repository.SettingsRepositoryImpl
import com.unswipe.android.data.repository.UsageRepositoryImpl
import com.unswipe.android.domain.repository.AuthRepository
import com.unswipe.android.domain.repository.BillingRepository
import com.unswipe.android.domain.repository.SettingsRepository
import com.unswipe.android.domain.repository.UsageRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUsageRepository(impl: UsageRepositoryImpl): UsageRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindBillingRepository(impl: BillingRepositoryImpl): BillingRepository

} 