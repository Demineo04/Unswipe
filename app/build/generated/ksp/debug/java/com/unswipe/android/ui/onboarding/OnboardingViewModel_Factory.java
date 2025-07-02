package com.unswipe.android.ui.onboarding;

import com.unswipe.android.domain.repository.OnboardingRepository;
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
public final class OnboardingViewModel_Factory implements Factory<OnboardingViewModel> {
  private final Provider<OnboardingRepository> onboardingRepositoryProvider;

  public OnboardingViewModel_Factory(Provider<OnboardingRepository> onboardingRepositoryProvider) {
    this.onboardingRepositoryProvider = onboardingRepositoryProvider;
  }

  @Override
  public OnboardingViewModel get() {
    return newInstance(onboardingRepositoryProvider.get());
  }

  public static OnboardingViewModel_Factory create(
      Provider<OnboardingRepository> onboardingRepositoryProvider) {
    return new OnboardingViewModel_Factory(onboardingRepositoryProvider);
  }

  public static OnboardingViewModel newInstance(OnboardingRepository onboardingRepository) {
    return new OnboardingViewModel(onboardingRepository);
  }
}
