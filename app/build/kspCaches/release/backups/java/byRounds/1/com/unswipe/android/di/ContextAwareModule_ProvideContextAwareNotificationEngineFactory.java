package com.unswipe.android.di;

import android.content.Context;
import androidx.core.app.NotificationManagerCompat;
import com.unswipe.android.data.analytics.UsagePatternAnalyzer;
import com.unswipe.android.data.context.ContextDetectionEngine;
import com.unswipe.android.data.notifications.ContextAwareNotificationEngine;
import com.unswipe.android.domain.repository.SettingsRepository;
import com.unswipe.android.domain.repository.UsageRepository;
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
public final class ContextAwareModule_ProvideContextAwareNotificationEngineFactory implements Factory<ContextAwareNotificationEngine> {
  private final Provider<Context> contextProvider;

  private final Provider<ContextDetectionEngine> contextEngineProvider;

  private final Provider<UsagePatternAnalyzer> patternAnalyzerProvider;

  private final Provider<UsageRepository> usageRepositoryProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<NotificationManagerCompat> notificationManagerProvider;

  public ContextAwareModule_ProvideContextAwareNotificationEngineFactory(
      Provider<Context> contextProvider, Provider<ContextDetectionEngine> contextEngineProvider,
      Provider<UsagePatternAnalyzer> patternAnalyzerProvider,
      Provider<UsageRepository> usageRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<NotificationManagerCompat> notificationManagerProvider) {
    this.contextProvider = contextProvider;
    this.contextEngineProvider = contextEngineProvider;
    this.patternAnalyzerProvider = patternAnalyzerProvider;
    this.usageRepositoryProvider = usageRepositoryProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
    this.notificationManagerProvider = notificationManagerProvider;
  }

  @Override
  public ContextAwareNotificationEngine get() {
    return provideContextAwareNotificationEngine(contextProvider.get(), contextEngineProvider.get(), patternAnalyzerProvider.get(), usageRepositoryProvider.get(), settingsRepositoryProvider.get(), notificationManagerProvider.get());
  }

  public static ContextAwareModule_ProvideContextAwareNotificationEngineFactory create(
      Provider<Context> contextProvider, Provider<ContextDetectionEngine> contextEngineProvider,
      Provider<UsagePatternAnalyzer> patternAnalyzerProvider,
      Provider<UsageRepository> usageRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<NotificationManagerCompat> notificationManagerProvider) {
    return new ContextAwareModule_ProvideContextAwareNotificationEngineFactory(contextProvider, contextEngineProvider, patternAnalyzerProvider, usageRepositoryProvider, settingsRepositoryProvider, notificationManagerProvider);
  }

  public static ContextAwareNotificationEngine provideContextAwareNotificationEngine(
      Context context, ContextDetectionEngine contextEngine, UsagePatternAnalyzer patternAnalyzer,
      UsageRepository usageRepository, SettingsRepository settingsRepository,
      NotificationManagerCompat notificationManager) {
    return Preconditions.checkNotNullFromProvides(ContextAwareModule.INSTANCE.provideContextAwareNotificationEngine(context, contextEngine, patternAnalyzer, usageRepository, settingsRepository, notificationManager));
  }
}
