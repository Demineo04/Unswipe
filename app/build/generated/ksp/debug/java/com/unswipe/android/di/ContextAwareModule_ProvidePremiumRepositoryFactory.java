package com.unswipe.android.di;

import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import com.unswipe.android.domain.repository.PremiumRepository;
import com.unswipe.android.domain.repository.UsageRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.serialization.json.Json;

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
public final class ContextAwareModule_ProvidePremiumRepositoryFactory implements Factory<PremiumRepository> {
  private final Provider<DataStore<Preferences>> dataStoreProvider;

  private final Provider<UsageRepository> usageRepositoryProvider;

  private final Provider<Json> jsonProvider;

  public ContextAwareModule_ProvidePremiumRepositoryFactory(
      Provider<DataStore<Preferences>> dataStoreProvider,
      Provider<UsageRepository> usageRepositoryProvider, Provider<Json> jsonProvider) {
    this.dataStoreProvider = dataStoreProvider;
    this.usageRepositoryProvider = usageRepositoryProvider;
    this.jsonProvider = jsonProvider;
  }

  @Override
  public PremiumRepository get() {
    return providePremiumRepository(dataStoreProvider.get(), usageRepositoryProvider.get(), jsonProvider.get());
  }

  public static ContextAwareModule_ProvidePremiumRepositoryFactory create(
      Provider<DataStore<Preferences>> dataStoreProvider,
      Provider<UsageRepository> usageRepositoryProvider, Provider<Json> jsonProvider) {
    return new ContextAwareModule_ProvidePremiumRepositoryFactory(dataStoreProvider, usageRepositoryProvider, jsonProvider);
  }

  public static PremiumRepository providePremiumRepository(DataStore<Preferences> dataStore,
      UsageRepository usageRepository, Json json) {
    return Preconditions.checkNotNullFromProvides(ContextAwareModule.INSTANCE.providePremiumRepository(dataStore, usageRepository, json));
  }
}
