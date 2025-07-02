package com.unswipe.android.di;

import android.content.Context;
import com.android.billingclient.api.BillingClient;
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
public final class AppModule_ProvideBillingClientFactory implements Factory<BillingClient> {
  private final Provider<Context> contextProvider;

  public AppModule_ProvideBillingClientFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public BillingClient get() {
    return provideBillingClient(contextProvider.get());
  }

  public static AppModule_ProvideBillingClientFactory create(Provider<Context> contextProvider) {
    return new AppModule_ProvideBillingClientFactory(contextProvider);
  }

  public static BillingClient provideBillingClient(Context context) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideBillingClient(context));
  }
}
