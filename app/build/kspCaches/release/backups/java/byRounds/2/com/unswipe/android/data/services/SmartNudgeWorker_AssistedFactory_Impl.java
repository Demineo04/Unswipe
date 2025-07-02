package com.unswipe.android.data.services;

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
public final class SmartNudgeWorker_AssistedFactory_Impl implements SmartNudgeWorker_AssistedFactory {
  private final SmartNudgeWorker_Factory delegateFactory;

  SmartNudgeWorker_AssistedFactory_Impl(SmartNudgeWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public SmartNudgeWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<SmartNudgeWorker_AssistedFactory> create(
      SmartNudgeWorker_Factory delegateFactory) {
    return InstanceFactory.create(new SmartNudgeWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<SmartNudgeWorker_AssistedFactory> createFactoryProvider(
      SmartNudgeWorker_Factory delegateFactory) {
    return InstanceFactory.create(new SmartNudgeWorker_AssistedFactory_Impl(delegateFactory));
  }
}
