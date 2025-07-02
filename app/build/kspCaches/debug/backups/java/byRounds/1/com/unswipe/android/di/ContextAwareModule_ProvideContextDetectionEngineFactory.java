package com.unswipe.android.di;

import android.content.Context;
import com.unswipe.android.data.context.ContextDetectionEngine;
import com.unswipe.android.domain.repository.SettingsRepository;
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
public final class ContextAwareModule_ProvideContextDetectionEngineFactory implements Factory<ContextDetectionEngine> {
  private final Provider<Context> contextProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public ContextAwareModule_ProvideContextDetectionEngineFactory(Provider<Context> contextProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    this.contextProvider = contextProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public ContextDetectionEngine get() {
    return provideContextDetectionEngine(contextProvider.get(), settingsRepositoryProvider.get());
  }

  public static ContextAwareModule_ProvideContextDetectionEngineFactory create(
      Provider<Context> contextProvider, Provider<SettingsRepository> settingsRepositoryProvider) {
    return new ContextAwareModule_ProvideContextDetectionEngineFactory(contextProvider, settingsRepositoryProvider);
  }

  public static ContextDetectionEngine provideContextDetectionEngine(Context context,
      SettingsRepository settingsRepository) {
    return Preconditions.checkNotNullFromProvides(ContextAwareModule.INSTANCE.provideContextDetectionEngine(context, settingsRepository));
  }
}
