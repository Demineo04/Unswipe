package com.unswipe.android.di;

import android.content.Context;
import com.unswipe.android.data.context.ContextDetectionEngine;
import com.unswipe.android.data.premium.SmartFocusModeManager;
import com.unswipe.android.domain.repository.PremiumRepository;
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
public final class ContextAwareModule_ProvideSmartFocusModeManagerFactory implements Factory<SmartFocusModeManager> {
  private final Provider<Context> contextProvider;

  private final Provider<PremiumRepository> premiumRepositoryProvider;

  private final Provider<ContextDetectionEngine> contextEngineProvider;

  public ContextAwareModule_ProvideSmartFocusModeManagerFactory(Provider<Context> contextProvider,
      Provider<PremiumRepository> premiumRepositoryProvider,
      Provider<ContextDetectionEngine> contextEngineProvider) {
    this.contextProvider = contextProvider;
    this.premiumRepositoryProvider = premiumRepositoryProvider;
    this.contextEngineProvider = contextEngineProvider;
  }

  @Override
  public SmartFocusModeManager get() {
    return provideSmartFocusModeManager(contextProvider.get(), premiumRepositoryProvider.get(), contextEngineProvider.get());
  }

  public static ContextAwareModule_ProvideSmartFocusModeManagerFactory create(
      Provider<Context> contextProvider, Provider<PremiumRepository> premiumRepositoryProvider,
      Provider<ContextDetectionEngine> contextEngineProvider) {
    return new ContextAwareModule_ProvideSmartFocusModeManagerFactory(contextProvider, premiumRepositoryProvider, contextEngineProvider);
  }

  public static SmartFocusModeManager provideSmartFocusModeManager(Context context,
      PremiumRepository premiumRepository, ContextDetectionEngine contextEngine) {
    return Preconditions.checkNotNullFromProvides(ContextAwareModule.INSTANCE.provideSmartFocusModeManager(context, premiumRepository, contextEngine));
  }
}
