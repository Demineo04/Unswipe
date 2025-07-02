package com.unswipe.android.data.workers;

import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.work.WorkerParameters;
import com.unswipe.android.data.local.dao.UsageDao;
import com.unswipe.android.domain.repository.SettingsRepository;
import dagger.internal.DaggerGenerated;
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
public final class UsageTrackingWorker_Factory {
  private final Provider<UsageStatsManager> usageStatsManagerProvider;

  private final Provider<UsageDao> usageDaoProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<PackageManager> packageManagerProvider;

  public UsageTrackingWorker_Factory(Provider<UsageStatsManager> usageStatsManagerProvider,
      Provider<UsageDao> usageDaoProvider, Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<PackageManager> packageManagerProvider) {
    this.usageStatsManagerProvider = usageStatsManagerProvider;
    this.usageDaoProvider = usageDaoProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.packageManagerProvider = packageManagerProvider;
  }

  public UsageTrackingWorker get(Context appContext, WorkerParameters workerParams) {
    return newInstance(appContext, workerParams, usageStatsManagerProvider.get(), usageDaoProvider.get(), settingsRepositoryProvider.get(), packageManagerProvider.get());
  }

  public static UsageTrackingWorker_Factory create(
      Provider<UsageStatsManager> usageStatsManagerProvider, Provider<UsageDao> usageDaoProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<PackageManager> packageManagerProvider) {
    return new UsageTrackingWorker_Factory(usageStatsManagerProvider, usageDaoProvider, settingsRepositoryProvider, packageManagerProvider);
  }

  public static UsageTrackingWorker newInstance(Context appContext, WorkerParameters workerParams,
      UsageStatsManager usageStatsManager, UsageDao usageDao, SettingsRepository settingsRepository,
      PackageManager packageManager) {
    return new UsageTrackingWorker(appContext, workerParams, usageStatsManager, usageDao, settingsRepository, packageManager);
  }
}
