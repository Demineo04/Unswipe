package com.unswipe.android.ui.dashboard;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class TestViewModel_Factory implements Factory<TestViewModel> {
  @Override
  public TestViewModel get() {
    return newInstance();
  }

  public static TestViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static TestViewModel newInstance() {
    return new TestViewModel();
  }

  private static final class InstanceHolder {
    private static final TestViewModel_Factory INSTANCE = new TestViewModel_Factory();
  }
}
