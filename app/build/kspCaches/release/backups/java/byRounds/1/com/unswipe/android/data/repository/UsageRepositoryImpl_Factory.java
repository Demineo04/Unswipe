package com.unswipe.android.data.repository;

import android.app.usage.UsageStatsManager;
import android.content.Context;
import com.google.firebase.firestore.FirebaseFirestore;
import com.unswipe.android.data.local.dao.UsageDao;
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
public final class UsageRepositoryImpl_Factory implements Factory<UsageRepositoryImpl> {
  private final Provider<UsageDao> usageDaoProvider;

  private final Provider<UsageStatsManager> usageStatsManagerProvider;

  private final Provider<FirebaseFirestore> firestoreProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<Context> contextProvider;

  public UsageRepositoryImpl_Factory(Provider<UsageDao> usageDaoProvider,
      Provider<UsageStatsManager> usageStatsManagerProvider,
      Provider<FirebaseFirestore> firestoreProvider,
      Provider<SettingsRepository> settingsRepositoryProvider, Provider<Context> contextProvider) {
    this.usageDaoProvider = usageDaoProvider;
    this.usageStatsManagerProvider = usageStatsManagerProvider;
    this.firestoreProvider = firestoreProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public UsageRepositoryImpl get() {
    return newInstance(usageDaoProvider.get(), usageStatsManagerProvider.get(), firestoreProvider.get(), settingsRepositoryProvider.get(), contextProvider.get());
  }

  public static UsageRepositoryImpl_Factory create(Provider<UsageDao> usageDaoProvider,
      Provider<UsageStatsManager> usageStatsManagerProvider,
      Provider<FirebaseFirestore> firestoreProvider,
      Provider<SettingsRepository> settingsRepositoryProvider, Provider<Context> contextProvider) {
    return new UsageRepositoryImpl_Factory(usageDaoProvider, usageStatsManagerProvider, firestoreProvider, settingsRepositoryProvider, contextProvider);
  }

  public static UsageRepositoryImpl newInstance(UsageDao usageDao,
      UsageStatsManager usageStatsManager, FirebaseFirestore firestore,
      SettingsRepository settingsRepository, Context context) {
    return new UsageRepositoryImpl(usageDao, usageStatsManager, firestore, settingsRepository, context);
  }
}
