package com.unswipe.android.ui.confirmation;

import com.unswipe.android.data.interventions.ContextualInterventionEngine;
import com.unswipe.android.data.notifications.ContextAwareNotificationEngine;
import com.unswipe.android.data.premium.SmartFocusModeManager;
import com.unswipe.android.domain.repository.PremiumRepository;
import com.unswipe.android.domain.repository.SettingsRepository;
import com.unswipe.android.domain.repository.UsageRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class ConfirmationViewModel_Factory implements Factory<ConfirmationViewModel> {
  private final Provider<UsageRepository> usageRepositoryProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<PremiumRepository> premiumRepositoryProvider;

  private final Provider<ContextualInterventionEngine> interventionEngineProvider;

  private final Provider<ContextAwareNotificationEngine> notificationEngineProvider;

  private final Provider<SmartFocusModeManager> focusModeManagerProvider;

  public ConfirmationViewModel_Factory(Provider<UsageRepository> usageRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<PremiumRepository> premiumRepositoryProvider,
      Provider<ContextualInterventionEngine> interventionEngineProvider,
      Provider<ContextAwareNotificationEngine> notificationEngineProvider,
      Provider<SmartFocusModeManager> focusModeManagerProvider) {
    this.usageRepositoryProvider = usageRepositoryProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.premiumRepositoryProvider = premiumRepositoryProvider;
    this.interventionEngineProvider = interventionEngineProvider;
    this.notificationEngineProvider = notificationEngineProvider;
    this.focusModeManagerProvider = focusModeManagerProvider;
  }

  @Override
  public ConfirmationViewModel get() {
    return newInstance(usageRepositoryProvider.get(), settingsRepositoryProvider.get(), premiumRepositoryProvider.get(), interventionEngineProvider.get(), notificationEngineProvider.get(), focusModeManagerProvider.get());
  }

  public static ConfirmationViewModel_Factory create(
      Provider<UsageRepository> usageRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<PremiumRepository> premiumRepositoryProvider,
      Provider<ContextualInterventionEngine> interventionEngineProvider,
      Provider<ContextAwareNotificationEngine> notificationEngineProvider,
      Provider<SmartFocusModeManager> focusModeManagerProvider) {
    return new ConfirmationViewModel_Factory(usageRepositoryProvider, settingsRepositoryProvider, premiumRepositoryProvider, interventionEngineProvider, notificationEngineProvider, focusModeManagerProvider);
  }

  public static ConfirmationViewModel newInstance(UsageRepository usageRepository,
      SettingsRepository settingsRepository, PremiumRepository premiumRepository,
      ContextualInterventionEngine interventionEngine,
      ContextAwareNotificationEngine notificationEngine, SmartFocusModeManager focusModeManager) {
    return new ConfirmationViewModel(usageRepository, settingsRepository, premiumRepository, interventionEngine, notificationEngine, focusModeManager);
  }
}
