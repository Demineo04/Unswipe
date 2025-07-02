package com.unswipe.android.di;

import com.unswipe.android.data.premium.AdvancedAnalyticsEngine;
import com.unswipe.android.domain.repository.PremiumRepository;
import com.unswipe.android.domain.repository.SettingsRepository;
import com.unswipe.android.domain.repository.UsageRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class ContextAwareModule_ProvideAdvancedAnalyticsEngineFactory implements Factory<AdvancedAnalyticsEngine> {
  private final Provider<PremiumRepository> premiumRepositoryProvider;

  private final Provider<UsageRepository> usageRepositoryProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public ContextAwareModule_ProvideAdvancedAnalyticsEngineFactory(
      Provider<PremiumRepository> premiumRepositoryProvider,
      Provider<UsageRepository> usageRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    this.premiumRepositoryProvider = premiumRepositoryProvider;
    this.usageRepositoryProvider = usageRepositoryProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public AdvancedAnalyticsEngine get() {
    return provideAdvancedAnalyticsEngine(premiumRepositoryProvider.get(), usageRepositoryProvider.get(), settingsRepositoryProvider.get());
  }

  public static ContextAwareModule_ProvideAdvancedAnalyticsEngineFactory create(
      Provider<PremiumRepository> premiumRepositoryProvider,
      Provider<UsageRepository> usageRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new ContextAwareModule_ProvideAdvancedAnalyticsEngineFactory(premiumRepositoryProvider, usageRepositoryProvider, settingsRepositoryProvider);
  }

  public static AdvancedAnalyticsEngine provideAdvancedAnalyticsEngine(
      PremiumRepository premiumRepository, UsageRepository usageRepository,
      SettingsRepository settingsRepository) {
    return Preconditions.checkNotNullFromProvides(ContextAwareModule.INSTANCE.provideAdvancedAnalyticsEngine(premiumRepository, usageRepository, settingsRepository));
  }
}
