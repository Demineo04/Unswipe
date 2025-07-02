package com.unswipe.android.data.interventions;

import com.unswipe.android.data.analytics.UsagePatternAnalyzer;
import com.unswipe.android.data.context.ContextDetectionEngine;
import com.unswipe.android.domain.repository.SettingsRepository;
import com.unswipe.android.domain.repository.UsageRepository;
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
public final class ContextualInterventionEngine_Factory implements Factory<ContextualInterventionEngine> {
  private final Provider<ContextDetectionEngine> contextEngineProvider;

  private final Provider<UsagePatternAnalyzer> patternAnalyzerProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<UsageRepository> usageRepositoryProvider;

  public ContextualInterventionEngine_Factory(
      Provider<ContextDetectionEngine> contextEngineProvider,
      Provider<UsagePatternAnalyzer> patternAnalyzerProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<UsageRepository> usageRepositoryProvider) {
    this.contextEngineProvider = contextEngineProvider;
    this.patternAnalyzerProvider = patternAnalyzerProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.usageRepositoryProvider = usageRepositoryProvider;
  }

  @Override
  public ContextualInterventionEngine get() {
    return newInstance(contextEngineProvider.get(), patternAnalyzerProvider.get(), settingsRepositoryProvider.get(), usageRepositoryProvider.get());
  }

  public static ContextualInterventionEngine_Factory create(
      Provider<ContextDetectionEngine> contextEngineProvider,
      Provider<UsagePatternAnalyzer> patternAnalyzerProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<UsageRepository> usageRepositoryProvider) {
    return new ContextualInterventionEngine_Factory(contextEngineProvider, patternAnalyzerProvider, settingsRepositoryProvider, usageRepositoryProvider);
  }

  public static ContextualInterventionEngine newInstance(ContextDetectionEngine contextEngine,
      UsagePatternAnalyzer patternAnalyzer, SettingsRepository settingsRepository,
      UsageRepository usageRepository) {
    return new ContextualInterventionEngine(contextEngine, patternAnalyzer, settingsRepository, usageRepository);
  }
}
