package com.unswipe.android.di;

import com.unswipe.android.data.local.AppDatabase;
import com.unswipe.android.data.local.dao.UsageDao;
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
public final class DatabaseModule_ProvideUsageDaoFactory implements Factory<UsageDao> {
  private final Provider<AppDatabase> appDatabaseProvider;

  public DatabaseModule_ProvideUsageDaoFactory(Provider<AppDatabase> appDatabaseProvider) {
    this.appDatabaseProvider = appDatabaseProvider;
  }

  @Override
  public UsageDao get() {
    return provideUsageDao(appDatabaseProvider.get());
  }

  public static DatabaseModule_ProvideUsageDaoFactory create(
      Provider<AppDatabase> appDatabaseProvider) {
    return new DatabaseModule_ProvideUsageDaoFactory(appDatabaseProvider);
  }

  public static UsageDao provideUsageDao(AppDatabase appDatabase) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideUsageDao(appDatabase));
  }
}
