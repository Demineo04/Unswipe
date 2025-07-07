package com.unswipe.android.ui.splash;

import com.unswipe.android.domain.repository.AuthRepository;
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
public final class SplashViewModel_Factory implements Factory<SplashViewModel> {
  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<OnboardingRepository> onboardingRepositoryProvider;

  public SplashViewModel_Factory(Provider<AuthRepository> authRepositoryProvider,
      Provider<OnboardingRepository> onboardingRepositoryProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
    this.onboardingRepositoryProvider = onboardingRepositoryProvider;
  }

  @Override
  public SplashViewModel get() {
    return newInstance(authRepositoryProvider.get(), onboardingRepositoryProvider.get());
  }

  public static SplashViewModel_Factory create(Provider<AuthRepository> authRepositoryProvider,
      Provider<OnboardingRepository> onboardingRepositoryProvider) {
    return new SplashViewModel_Factory(authRepositoryProvider, onboardingRepositoryProvider);
  }

  public static SplashViewModel newInstance(AuthRepository authRepository,
      OnboardingRepository onboardingRepository) {
    return new SplashViewModel(authRepository, onboardingRepository);
  }
}
