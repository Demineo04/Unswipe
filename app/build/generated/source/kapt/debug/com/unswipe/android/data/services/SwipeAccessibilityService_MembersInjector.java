package com.unswipe.android.data.services;

import android.content.pm.PackageManager;
import com.unswipe.android.data.local.dao.UsageDao;
import com.unswipe.android.domain.repository.SettingsRepository;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class SwipeAccessibilityService_MembersInjector implements MembersInjector<SwipeAccessibilityService> {
  private final Provider<UsageDao> usageDaoProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<PackageManager> injectedPackageManagerProvider;

  public SwipeAccessibilityService_MembersInjector(Provider<UsageDao> usageDaoProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<PackageManager> injectedPackageManagerProvider) {
    this.usageDaoProvider = usageDaoProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.injectedPackageManagerProvider = injectedPackageManagerProvider;
  }

  public static MembersInjector<SwipeAccessibilityService> create(
      Provider<UsageDao> usageDaoProvider, Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<PackageManager> injectedPackageManagerProvider) {
    return new SwipeAccessibilityService_MembersInjector(usageDaoProvider, settingsRepositoryProvider, injectedPackageManagerProvider);
  }

  @Override
  public void injectMembers(SwipeAccessibilityService instance) {
    injectUsageDao(instance, usageDaoProvider.get());
    injectSettingsRepository(instance, settingsRepositoryProvider.get());
    injectInjectedPackageManager(instance, injectedPackageManagerProvider.get());
  }

  @InjectedFieldSignature("com.unswipe.android.data.services.SwipeAccessibilityService.usageDao")
  public static void injectUsageDao(SwipeAccessibilityService instance, UsageDao usageDao) {
    instance.usageDao = usageDao;
  }

  @InjectedFieldSignature("com.unswipe.android.data.services.SwipeAccessibilityService.settingsRepository")
  public static void injectSettingsRepository(SwipeAccessibilityService instance,
      SettingsRepository settingsRepository) {
    instance.settingsRepository = settingsRepository;
  }

  @InjectedFieldSignature("com.unswipe.android.data.services.SwipeAccessibilityService.injectedPackageManager")
  public static void injectInjectedPackageManager(SwipeAccessibilityService instance,
      PackageManager injectedPackageManager) {
    instance.injectedPackageManager = injectedPackageManager;
  }
}
