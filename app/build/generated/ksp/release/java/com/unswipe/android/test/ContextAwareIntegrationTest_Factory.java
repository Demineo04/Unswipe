package com.unswipe.android.test;

import android.content.Context;
import com.unswipe.android.data.analytics.UsagePatternAnalyzer;
import com.unswipe.android.data.context.ContextDetectionEngine;
import com.unswipe.android.data.interventions.ContextualInterventionEngine;
import com.unswipe.android.data.notifications.ContextAwareNotificationEngine;
import com.unswipe.android.domain.repository.SettingsRepository;
import com.unswipe.android.domain.repository.UsageRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class ContextAwareIntegrationTest_Factory implements Factory<ContextAwareIntegrationTest> {
  private final Provider<Context> contextProvider;

  private final Provider<ContextDetectionEngine> contextEngineProvider;

  private final Provider<UsagePatternAnalyzer> patternAnalyzerProvider;

  private final Provider<ContextualInterventionEngine> interventionEngineProvider;

  private final Provider<ContextAwareNotificationEngine> notificationEngineProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<UsageRepository> usageRepositoryProvider;

  public ContextAwareIntegrationTest_Factory(Provider<Context> contextProvider,
      Provider<ContextDetectionEngine> contextEngineProvider,
      Provider<UsagePatternAnalyzer> patternAnalyzerProvider,
      Provider<ContextualInterventionEngine> interventionEngineProvider,
      Provider<ContextAwareNotificationEngine> notificationEngineProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<UsageRepository> usageRepositoryProvider) {
    this.contextProvider = contextProvider;
    this.contextEngineProvider = contextEngineProvider;
    this.patternAnalyzerProvider = patternAnalyzerProvider;
    this.interventionEngineProvider = interventionEngineProvider;
    this.notificationEngineProvider = notificationEngineProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.usageRepositoryProvider = usageRepositoryProvider;
  }

  @Override
  public ContextAwareIntegrationTest get() {
    return newInstance(contextProvider.get(), contextEngineProvider.get(), patternAnalyzerProvider.get(), interventionEngineProvider.get(), notificationEngineProvider.get(), settingsRepositoryProvider.get(), usageRepositoryProvider.get());
  }

  public static ContextAwareIntegrationTest_Factory create(Provider<Context> contextProvider,
      Provider<ContextDetectionEngine> contextEngineProvider,
      Provider<UsagePatternAnalyzer> patternAnalyzerProvider,
      Provider<ContextualInterventionEngine> interventionEngineProvider,
      Provider<ContextAwareNotificationEngine> notificationEngineProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<UsageRepository> usageRepositoryProvider) {
    return new ContextAwareIntegrationTest_Factory(contextProvider, contextEngineProvider, patternAnalyzerProvider, interventionEngineProvider, notificationEngineProvider, settingsRepositoryProvider, usageRepositoryProvider);
  }

  public static ContextAwareIntegrationTest newInstance(Context context,
      ContextDetectionEngine contextEngine, UsagePatternAnalyzer patternAnalyzer,
      ContextualInterventionEngine interventionEngine,
      ContextAwareNotificationEngine notificationEngine, SettingsRepository settingsRepository,
      UsageRepository usageRepository) {
    return new ContextAwareIntegrationTest(context, contextEngine, patternAnalyzer, interventionEngine, notificationEngine, settingsRepository, usageRepository);
  }
}
