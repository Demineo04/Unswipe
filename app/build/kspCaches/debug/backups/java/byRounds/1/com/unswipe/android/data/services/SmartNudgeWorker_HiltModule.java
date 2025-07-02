package com.unswipe.android.data.services;

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
    topLevelClass = SmartNudgeWorker.class
)
public interface SmartNudgeWorker_HiltModule {
  @Binds
  @IntoMap
  @StringKey("com.unswipe.android.data.services.SmartNudgeWorker")
  WorkerAssistedFactory<? extends ListenableWorker> bind(SmartNudgeWorker_AssistedFactory factory);
}
