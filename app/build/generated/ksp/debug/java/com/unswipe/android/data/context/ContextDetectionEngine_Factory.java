package com.unswipe.android.data.context;

import android.content.Context;
import com.unswipe.android.domain.repository.SettingsRepository;
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
public final class ContextDetectionEngine_Factory implements Factory<ContextDetectionEngine> {
  private final Provider<Context> contextProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public ContextDetectionEngine_Factory(Provider<Context> contextProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    this.contextProvider = contextProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public ContextDetectionEngine get() {
    return newInstance(contextProvider.get(), settingsRepositoryProvider.get());
  }

  public static ContextDetectionEngine_Factory create(Provider<Context> contextProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new ContextDetectionEngine_Factory(contextProvider, settingsRepositoryProvider);
  }

  public static ContextDetectionEngine newInstance(Context context,
      SettingsRepository settingsRepository) {
    return new ContextDetectionEngine(context, settingsRepository);
  }
}
