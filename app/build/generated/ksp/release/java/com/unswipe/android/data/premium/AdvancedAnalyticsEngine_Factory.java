package com.unswipe.android.data.premium;

import com.unswipe.android.domain.repository.PremiumRepository;
import com.unswipe.android.domain.repository.SettingsRepository;
import com.unswipe.android.domain.repository.UsageRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class AdvancedAnalyticsEngine_Factory implements Factory<AdvancedAnalyticsEngine> {
  private final Provider<PremiumRepository> premiumRepositoryProvider;

  private final Provider<UsageRepository> usageRepositoryProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public AdvancedAnalyticsEngine_Factory(Provider<PremiumRepository> premiumRepositoryProvider,
      Provider<UsageRepository> usageRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    this.premiumRepositoryProvider = premiumRepositoryProvider;
    this.usageRepositoryProvider = usageRepositoryProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public AdvancedAnalyticsEngine get() {
    return newInstance(premiumRepositoryProvider.get(), usageRepositoryProvider.get(), settingsRepositoryProvider.get());
  }

  public static AdvancedAnalyticsEngine_Factory create(
      Provider<PremiumRepository> premiumRepositoryProvider,
      Provider<UsageRepository> usageRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new AdvancedAnalyticsEngine_Factory(premiumRepositoryProvider, usageRepositoryProvider, settingsRepositoryProvider);
  }

  public static AdvancedAnalyticsEngine newInstance(PremiumRepository premiumRepository,
      UsageRepository usageRepository, SettingsRepository settingsRepository) {
    return new AdvancedAnalyticsEngine(premiumRepository, usageRepository, settingsRepository);
  }
}
