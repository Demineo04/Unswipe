package com.unswipe.android.data.premium;

import android.content.Context;
import com.unswipe.android.data.context.ContextDetectionEngine;
import com.unswipe.android.domain.repository.PremiumRepository;
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
public final class SmartFocusModeManager_Factory implements Factory<SmartFocusModeManager> {
  private final Provider<Context> contextProvider;

  private final Provider<PremiumRepository> premiumRepositoryProvider;

  private final Provider<ContextDetectionEngine> contextEngineProvider;

  public SmartFocusModeManager_Factory(Provider<Context> contextProvider,
      Provider<PremiumRepository> premiumRepositoryProvider,
      Provider<ContextDetectionEngine> contextEngineProvider) {
    this.contextProvider = contextProvider;
    this.premiumRepositoryProvider = premiumRepositoryProvider;
    this.contextEngineProvider = contextEngineProvider;
  }

  @Override
  public SmartFocusModeManager get() {
    return newInstance(contextProvider.get(), premiumRepositoryProvider.get(), contextEngineProvider.get());
  }

  public static SmartFocusModeManager_Factory create(Provider<Context> contextProvider,
      Provider<PremiumRepository> premiumRepositoryProvider,
      Provider<ContextDetectionEngine> contextEngineProvider) {
    return new SmartFocusModeManager_Factory(contextProvider, premiumRepositoryProvider, contextEngineProvider);
  }

  public static SmartFocusModeManager newInstance(Context context,
      PremiumRepository premiumRepository, ContextDetectionEngine contextEngine) {
    return new SmartFocusModeManager(context, premiumRepository, contextEngine);
  }
}
