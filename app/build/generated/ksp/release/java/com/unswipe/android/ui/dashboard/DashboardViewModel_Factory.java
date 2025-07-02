package com.unswipe.android.ui.dashboard;

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
public final class DashboardViewModel_Factory implements Factory<DashboardViewModel> {
  private final Provider<UsageRepository> usageRepositoryProvider;

  public DashboardViewModel_Factory(Provider<UsageRepository> usageRepositoryProvider) {
    this.usageRepositoryProvider = usageRepositoryProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(usageRepositoryProvider.get());
  }

  public static DashboardViewModel_Factory create(
      Provider<UsageRepository> usageRepositoryProvider) {
    return new DashboardViewModel_Factory(usageRepositoryProvider);
  }

  public static DashboardViewModel newInstance(UsageRepository usageRepository) {
    return new DashboardViewModel(usageRepository);
  }
}
