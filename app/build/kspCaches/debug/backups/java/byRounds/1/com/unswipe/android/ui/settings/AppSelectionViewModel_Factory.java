package com.unswipe.android.ui.settings;

import android.content.pm.PackageManager;
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
public final class AppSelectionViewModel_Factory implements Factory<AppSelectionViewModel> {
  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<PackageManager> packageManagerProvider;

  public AppSelectionViewModel_Factory(Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<PackageManager> packageManagerProvider) {
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.packageManagerProvider = packageManagerProvider;
  }

  @Override
  public AppSelectionViewModel get() {
    return newInstance(settingsRepositoryProvider.get(), packageManagerProvider.get());
  }

  public static AppSelectionViewModel_Factory create(
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<PackageManager> packageManagerProvider) {
    return new AppSelectionViewModel_Factory(settingsRepositoryProvider, packageManagerProvider);
  }

  public static AppSelectionViewModel newInstance(SettingsRepository settingsRepository,
      PackageManager packageManager) {
    return new AppSelectionViewModel(settingsRepository, packageManager);
  }
}
