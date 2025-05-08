package com.unswipe.android.data.repository;

import android.app.Application;
import com.android.billingclient.api.BillingClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.unswipe.android.domain.repository.SettingsRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.coroutines.CoroutineDispatcher;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata({
    "com.unswipe.android.di.IoDispatcher",
    "com.unswipe.android.di.MainDispatcher"
})
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
public final class BillingRepositoryImpl_Factory implements Factory<BillingRepositoryImpl> {
  private final Provider<Application> applicationProvider;

  private final Provider<BillingClient> billingClientProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<FirebaseFirestore> firestoreProvider;

  private final Provider<FirebaseAuth> firebaseAuthProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  private final Provider<CoroutineDispatcher> mainDispatcherProvider;

  public BillingRepositoryImpl_Factory(Provider<Application> applicationProvider,
      Provider<BillingClient> billingClientProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<FirebaseFirestore> firestoreProvider, Provider<FirebaseAuth> firebaseAuthProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider,
      Provider<CoroutineDispatcher> mainDispatcherProvider) {
    this.applicationProvider = applicationProvider;
    this.billingClientProvider = billingClientProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.firestoreProvider = firestoreProvider;
    this.firebaseAuthProvider = firebaseAuthProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
    this.mainDispatcherProvider = mainDispatcherProvider;
  }

  @Override
  public BillingRepositoryImpl get() {
    return newInstance(applicationProvider.get(), billingClientProvider.get(), settingsRepositoryProvider.get(), firestoreProvider.get(), firebaseAuthProvider.get(), ioDispatcherProvider.get(), mainDispatcherProvider.get());
  }

  public static BillingRepositoryImpl_Factory create(Provider<Application> applicationProvider,
      Provider<BillingClient> billingClientProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<FirebaseFirestore> firestoreProvider, Provider<FirebaseAuth> firebaseAuthProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider,
      Provider<CoroutineDispatcher> mainDispatcherProvider) {
    return new BillingRepositoryImpl_Factory(applicationProvider, billingClientProvider, settingsRepositoryProvider, firestoreProvider, firebaseAuthProvider, ioDispatcherProvider, mainDispatcherProvider);
  }

  public static BillingRepositoryImpl newInstance(Application application,
      BillingClient billingClient, SettingsRepository settingsRepository,
      FirebaseFirestore firestore, FirebaseAuth firebaseAuth, CoroutineDispatcher ioDispatcher,
      CoroutineDispatcher mainDispatcher) {
    return new BillingRepositoryImpl(application, billingClient, settingsRepository, firestore, firebaseAuth, ioDispatcher, mainDispatcher);
  }
}
