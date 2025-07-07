package com.unswipe.android.ui.confirmation;

import com.unswipe.android.domain.repository.UsageRepository;
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
public final class EnhancedConfirmationViewModel_Factory implements Factory<EnhancedConfirmationViewModel> {
  private final Provider<UsageRepository> usageRepositoryProvider;

  public EnhancedConfirmationViewModel_Factory(Provider<UsageRepository> usageRepositoryProvider) {
    this.usageRepositoryProvider = usageRepositoryProvider;
  }

  @Override
  public EnhancedConfirmationViewModel get() {
    return newInstance(usageRepositoryProvider.get());
  }

  public static EnhancedConfirmationViewModel_Factory create(
      Provider<UsageRepository> usageRepositoryProvider) {
    return new EnhancedConfirmationViewModel_Factory(usageRepositoryProvider);
  }

  public static EnhancedConfirmationViewModel newInstance(UsageRepository usageRepository) {
    return new EnhancedConfirmationViewModel(usageRepository);
  }
}
