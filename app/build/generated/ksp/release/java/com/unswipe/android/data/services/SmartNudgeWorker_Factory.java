package com.unswipe.android.data.services;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
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
public final class SmartNudgeWorker_Factory {
  private final Provider<NotificationService> notificationServiceProvider;

  public SmartNudgeWorker_Factory(Provider<NotificationService> notificationServiceProvider) {
    this.notificationServiceProvider = notificationServiceProvider;
  }

  public SmartNudgeWorker get(Context context, WorkerParameters params) {
    return newInstance(context, params, notificationServiceProvider.get());
  }

  public static SmartNudgeWorker_Factory create(
      Provider<NotificationService> notificationServiceProvider) {
    return new SmartNudgeWorker_Factory(notificationServiceProvider);
  }

  public static SmartNudgeWorker newInstance(Context context, WorkerParameters params,
      NotificationService notificationService) {
    return new SmartNudgeWorker(context, params, notificationService);
  }
}
