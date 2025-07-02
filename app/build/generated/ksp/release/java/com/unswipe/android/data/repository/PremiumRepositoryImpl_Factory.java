package com.unswipe.android.data.repository;

import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import com.unswipe.android.domain.repository.UsageRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class PremiumRepositoryImpl_Factory implements Factory<PremiumRepositoryImpl> {
  private final Provider<DataStore<Preferences>> dataStoreProvider;

  private final Provider<UsageRepository> usageRepositoryProvider;

  private final Provider<Json> jsonProvider;

  public PremiumRepositoryImpl_Factory(Provider<DataStore<Preferences>> dataStoreProvider,
      Provider<UsageRepository> usageRepositoryProvider, Provider<Json> jsonProvider) {
    this.dataStoreProvider = dataStoreProvider;
    this.usageRepositoryProvider = usageRepositoryProvider;
    this.jsonProvider = jsonProvider;
  }

  @Override
  public PremiumRepositoryImpl get() {
    return newInstance(dataStoreProvider.get(), usageRepositoryProvider.get(), jsonProvider.get());
  }

  public static PremiumRepositoryImpl_Factory create(
      Provider<DataStore<Preferences>> dataStoreProvider,
      Provider<UsageRepository> usageRepositoryProvider, Provider<Json> jsonProvider) {
    return new PremiumRepositoryImpl_Factory(dataStoreProvider, usageRepositoryProvider, jsonProvider);
  }

  public static PremiumRepositoryImpl newInstance(DataStore<Preferences> dataStore,
      UsageRepository usageRepository, Json json) {
    return new PremiumRepositoryImpl(dataStore, usageRepository, json);
  }
}
