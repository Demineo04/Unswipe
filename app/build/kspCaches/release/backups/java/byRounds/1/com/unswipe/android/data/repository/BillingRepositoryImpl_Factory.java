package com.unswipe.android.data.repository;

import android.content.Context;
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
public final class BillingRepositoryImpl_Factory implements Factory<BillingRepositoryImpl> {
  private final Provider<Context> contextProvider;

  public BillingRepositoryImpl_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public BillingRepositoryImpl get() {
    return newInstance(contextProvider.get());
  }

  public static BillingRepositoryImpl_Factory create(Provider<Context> contextProvider) {
    return new BillingRepositoryImpl_Factory(contextProvider);
  }

  public static BillingRepositoryImpl newInstance(Context context) {
    return new BillingRepositoryImpl(context);
  }
}
