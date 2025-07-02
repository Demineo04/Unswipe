package com.unswipe.android.data.services;

import android.content.Context;
import com.unswipe.android.domain.repository.OnboardingRepository;
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
public final class NotificationService_Factory implements Factory<NotificationService> {
  private final Provider<Context> contextProvider;

  private final Provider<UsageRepository> usageRepositoryProvider;

  private final Provider<OnboardingRepository> onboardingRepositoryProvider;

  public NotificationService_Factory(Provider<Context> contextProvider,
      Provider<UsageRepository> usageRepositoryProvider,
      Provider<OnboardingRepository> onboardingRepositoryProvider) {
    this.contextProvider = contextProvider;
    this.usageRepositoryProvider = usageRepositoryProvider;
    this.onboardingRepositoryProvider = onboardingRepositoryProvider;
  }

  @Override
  public NotificationService get() {
    return newInstance(contextProvider.get(), usageRepositoryProvider.get(), onboardingRepositoryProvider.get());
  }

  public static NotificationService_Factory create(Provider<Context> contextProvider,
      Provider<UsageRepository> usageRepositoryProvider,
      Provider<OnboardingRepository> onboardingRepositoryProvider) {
    return new NotificationService_Factory(contextProvider, usageRepositoryProvider, onboardingRepositoryProvider);
  }

  public static NotificationService newInstance(Context context, UsageRepository usageRepository,
      OnboardingRepository onboardingRepository) {
    return new NotificationService(context, usageRepository, onboardingRepository);
  }
}
