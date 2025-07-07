package com.unswipe.android.ui.settings;

import com.unswipe.android.domain.repository.BillingRepository;
import com.unswipe.android.domain.repository.PremiumRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class PremiumViewModel_Factory implements Factory<PremiumViewModel> {
  private final Provider<BillingRepository> billingRepositoryProvider;

  private final Provider<PremiumRepository> premiumRepositoryProvider;

  public PremiumViewModel_Factory(Provider<BillingRepository> billingRepositoryProvider,
      Provider<PremiumRepository> premiumRepositoryProvider) {
    this.billingRepositoryProvider = billingRepositoryProvider;
    this.premiumRepositoryProvider = premiumRepositoryProvider;
  }

  @Override
  public PremiumViewModel get() {
    return newInstance(billingRepositoryProvider.get(), premiumRepositoryProvider.get());
  }

  public static PremiumViewModel_Factory create(
      Provider<BillingRepository> billingRepositoryProvider,
      Provider<PremiumRepository> premiumRepositoryProvider) {
    return new PremiumViewModel_Factory(billingRepositoryProvider, premiumRepositoryProvider);
  }

  public static PremiumViewModel newInstance(BillingRepository billingRepository,
      PremiumRepository premiumRepository) {
    return new PremiumViewModel(billingRepository, premiumRepository);
  }
}
