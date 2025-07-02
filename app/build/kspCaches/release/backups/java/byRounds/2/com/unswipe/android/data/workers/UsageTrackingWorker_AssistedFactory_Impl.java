package com.unswipe.android.data.workers;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.InstanceFactory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class UsageTrackingWorker_AssistedFactory_Impl implements UsageTrackingWorker_AssistedFactory {
  private final UsageTrackingWorker_Factory delegateFactory;

  UsageTrackingWorker_AssistedFactory_Impl(UsageTrackingWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public UsageTrackingWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<UsageTrackingWorker_AssistedFactory> create(
      UsageTrackingWorker_Factory delegateFactory) {
    return InstanceFactory.create(new UsageTrackingWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<UsageTrackingWorker_AssistedFactory> createFactoryProvider(
      UsageTrackingWorker_Factory delegateFactory) {
    return InstanceFactory.create(new UsageTrackingWorker_AssistedFactory_Impl(delegateFactory));
  }
}
