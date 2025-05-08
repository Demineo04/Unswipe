package com.unswipe.android.domain.usecase;

import com.unswipe.android.domain.repository.SettingsRepository;
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
public final class UpdateStreakUseCase_Factory implements Factory<UpdateStreakUseCase> {
  private final Provider<UsageRepository> usageRepositoryProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public UpdateStreakUseCase_Factory(Provider<UsageRepository> usageRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    this.usageRepositoryProvider = usageRepositoryProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public UpdateStreakUseCase get() {
    return newInstance(usageRepositoryProvider.get(), settingsRepositoryProvider.get());
  }

  public static UpdateStreakUseCase_Factory create(
      Provider<UsageRepository> usageRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new UpdateStreakUseCase_Factory(usageRepositoryProvider, settingsRepositoryProvider);
  }

  public static UpdateStreakUseCase newInstance(UsageRepository usageRepository,
      SettingsRepository settingsRepository) {
    return new UpdateStreakUseCase(usageRepository, settingsRepository);
  }
}
