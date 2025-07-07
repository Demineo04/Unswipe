package com.unswipe.android.ui.settings;

import com.unswipe.android.domain.repository.SettingsRepository;
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
public final class NotificationSettingsViewModel_Factory implements Factory<NotificationSettingsViewModel> {
  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public NotificationSettingsViewModel_Factory(
      Provider<SettingsRepository> settingsRepositoryProvider) {
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public NotificationSettingsViewModel get() {
    return newInstance(settingsRepositoryProvider.get());
  }

  public static NotificationSettingsViewModel_Factory create(
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new NotificationSettingsViewModel_Factory(settingsRepositoryProvider);
  }

  public static NotificationSettingsViewModel newInstance(SettingsRepository settingsRepository) {
    return new NotificationSettingsViewModel(settingsRepository);
  }
}
