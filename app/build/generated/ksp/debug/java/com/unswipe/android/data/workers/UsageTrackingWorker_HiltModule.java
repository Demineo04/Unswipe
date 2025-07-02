package com.unswipe.android.data.workers;

import androidx.hilt.work.WorkerAssistedFactory;
import androidx.work.ListenableWorker;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import javax.annotation.processing.Generated;

@Generated("androidx.hilt.AndroidXHiltProcessor")
@Module
@InstallIn(SingletonComponent.class)
@OriginatingElement(
    topLevelClass = UsageTrackingWorker.class
)
public interface UsageTrackingWorker_HiltModule {
  @Binds
  @IntoMap
  @StringKey("com.unswipe.android.data.workers.UsageTrackingWorker")
  WorkerAssistedFactory<? extends ListenableWorker> bind(
      UsageTrackingWorker_AssistedFactory factory);
}
