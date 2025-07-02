package com.unswipe.android.data.notifications;

import android.content.Context;
import androidx.core.app.NotificationManagerCompat;
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
public final class ContextAwareNotificationEngine_Factory implements Factory<ContextAwareNotificationEngine> {
  private final Provider<Context> contextProvider;

  private final Provider<ContextDetectionEngine> contextEngineProvider;

  private final Provider<UsagePatternAnalyzer> patternAnalyzerProvider;

  private final Provider<UsageRepository> usageRepositoryProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  private final Provider<NotificationManagerCompat> notificationManagerProvider;

  public ContextAwareNotificationEngine_Factory(Provider<Context> contextProvider,
      Provider<ContextDetectionEngine> contextEngineProvider,
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
    return newInstance(contextProvider.get(), contextEngineProvider.get(), patternAnalyzerProvider.get(), usageRepositoryProvider.get(), settingsRepositoryProvider.get(), notificationManagerProvider.get());
  }

  public static ContextAwareNotificationEngine_Factory create(Provider<Context> contextProvider,
      Provider<ContextDetectionEngine> contextEngineProvider,
      Provider<UsagePatternAnalyzer> patternAnalyzerProvider,
      Provider<UsageRepository> usageRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider,
      Provider<NotificationManagerCompat> notificationManagerProvider) {
    return new ContextAwareNotificationEngine_Factory(contextProvider, contextEngineProvider, patternAnalyzerProvider, usageRepositoryProvider, settingsRepositoryProvider, notificationManagerProvider);
  }

  public static ContextAwareNotificationEngine newInstance(Context context,
      ContextDetectionEngine contextEngine, UsagePatternAnalyzer patternAnalyzer,
      UsageRepository usageRepository, SettingsRepository settingsRepository,
      NotificationManagerCompat notificationManager) {
    return new ContextAwareNotificationEngine(context, contextEngine, patternAnalyzer, usageRepository, settingsRepository, notificationManager);
  }
}
