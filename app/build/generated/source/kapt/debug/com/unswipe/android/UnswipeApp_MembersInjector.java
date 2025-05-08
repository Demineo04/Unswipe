package com.unswipe.android;

import androidx.hilt.work.HiltWorkerFactory;
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
public final class UnswipeApp_MembersInjector implements MembersInjector<UnswipeApp> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  public UnswipeApp_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
  }

  public static MembersInjector<UnswipeApp> create(
      Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new UnswipeApp_MembersInjector(workerFactoryProvider);
  }

  @Override
  public void injectMembers(UnswipeApp instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
  }

  @InjectedFieldSignature("com.unswipe.android.UnswipeApp.workerFactory")
  public static void injectWorkerFactory(UnswipeApp instance, HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }
}
