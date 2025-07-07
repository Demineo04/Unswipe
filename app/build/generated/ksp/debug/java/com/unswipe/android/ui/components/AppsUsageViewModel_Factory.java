package com.unswipe.android.ui.components;

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
public final class AppsUsageViewModel_Factory implements Factory<AppsUsageViewModel> {
  private final Provider<UsageRepository> usageRepositoryProvider;

  public AppsUsageViewModel_Factory(Provider<UsageRepository> usageRepositoryProvider) {
    this.usageRepositoryProvider = usageRepositoryProvider;
  }

  @Override
  public AppsUsageViewModel get() {
    return newInstance(usageRepositoryProvider.get());
  }

  public static AppsUsageViewModel_Factory create(
      Provider<UsageRepository> usageRepositoryProvider) {
    return new AppsUsageViewModel_Factory(usageRepositoryProvider);
  }

  public static AppsUsageViewModel newInstance(UsageRepository usageRepository) {
    return new AppsUsageViewModel(usageRepository);
  }
}
