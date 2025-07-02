package com.unswipe.android.di;

import android.app.usage.UsageStatsManager;
import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class AppModule_ProvideUsageStatsManagerFactory implements Factory<UsageStatsManager> {
  private final Provider<Context> contextProvider;

  public AppModule_ProvideUsageStatsManagerFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public UsageStatsManager get() {
    return provideUsageStatsManager(contextProvider.get());
  }

  public static AppModule_ProvideUsageStatsManagerFactory create(
      Provider<Context> contextProvider) {
    return new AppModule_ProvideUsageStatsManagerFactory(contextProvider);
  }

  public static UsageStatsManager provideUsageStatsManager(Context context) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideUsageStatsManager(context));
  }
}
