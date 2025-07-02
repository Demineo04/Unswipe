package com.unswipe.android.data.repository;

import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class OnboardingRepositoryImpl_Factory implements Factory<OnboardingRepositoryImpl> {
  private final Provider<DataStore<Preferences>> dataStoreProvider;

  public OnboardingRepositoryImpl_Factory(Provider<DataStore<Preferences>> dataStoreProvider) {
    this.dataStoreProvider = dataStoreProvider;
  }

  @Override
  public OnboardingRepositoryImpl get() {
    return newInstance(dataStoreProvider.get());
  }

  public static OnboardingRepositoryImpl_Factory create(
      Provider<DataStore<Preferences>> dataStoreProvider) {
    return new OnboardingRepositoryImpl_Factory(dataStoreProvider);
  }

  public static OnboardingRepositoryImpl newInstance(DataStore<Preferences> dataStore) {
    return new OnboardingRepositoryImpl(dataStore);
  }
}
