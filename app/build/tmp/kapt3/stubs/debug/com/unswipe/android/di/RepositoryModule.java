package com.unswipe.android.di;

@dagger.Module()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\'\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\'J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0005\u001a\u00020\tH\'J\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\u0005\u001a\u00020\fH\'J\u0010\u0010\r\u001a\u00020\u000e2\u0006\u0010\u0005\u001a\u00020\u000fH\'\u00a8\u0006\u0010"}, d2 = {"Lcom/unswipe/android/di/RepositoryModule;", "", "()V", "bindAuthRepository", "Lcom/unswipe/android/domain/repository/AuthRepository;", "impl", "Lcom/unswipe/android/data/repository/AuthRepositoryImpl;", "bindBillingRepository", "Lcom/unswipe/android/domain/repository/BillingRepository;", "Lcom/unswipe/android/data/repository/BillingRepositoryImpl;", "bindSettingsRepository", "Lcom/unswipe/android/domain/repository/SettingsRepository;", "Lcom/unswipe/android/data/repository/SettingsRepositoryImpl;", "bindUsageRepository", "Lcom/unswipe/android/domain/repository/UsageRepository;", "Lcom/unswipe/android/data/repository/UsageRepositoryImpl;", "app_debug"})
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
public abstract class RepositoryModule {
    
    public RepositoryModule() {
        super();
    }
    
    @dagger.Binds()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public abstract com.unswipe.android.domain.repository.AuthRepository bindAuthRepository(@org.jetbrains.annotations.NotNull()
    com.unswipe.android.data.repository.AuthRepositoryImpl impl);
    
    @dagger.Binds()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public abstract com.unswipe.android.domain.repository.UsageRepository bindUsageRepository(@org.jetbrains.annotations.NotNull()
    com.unswipe.android.data.repository.UsageRepositoryImpl impl);
    
    @dagger.Binds()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public abstract com.unswipe.android.domain.repository.SettingsRepository bindSettingsRepository(@org.jetbrains.annotations.NotNull()
    com.unswipe.android.data.repository.SettingsRepositoryImpl impl);
    
    @dagger.Binds()
    @javax.inject.Singleton()
    @org.jetbrains.annotations.NotNull()
    public abstract com.unswipe.android.domain.repository.BillingRepository bindBillingRepository(@org.jetbrains.annotations.NotNull()
    com.unswipe.android.data.repository.BillingRepositoryImpl impl);
}