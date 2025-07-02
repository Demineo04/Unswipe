package com.unswipe.android.data.analytics;

import com.unswipe.android.domain.repository.UsageRepository;
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
public final class UsagePatternAnalyzer_Factory implements Factory<UsagePatternAnalyzer> {
  private final Provider<UsageRepository> usageRepositoryProvider;

  public UsagePatternAnalyzer_Factory(Provider<UsageRepository> usageRepositoryProvider) {
    this.usageRepositoryProvider = usageRepositoryProvider;
  }

  @Override
  public UsagePatternAnalyzer get() {
    return newInstance(usageRepositoryProvider.get());
  }

  public static UsagePatternAnalyzer_Factory create(
      Provider<UsageRepository> usageRepositoryProvider) {
    return new UsagePatternAnalyzer_Factory(usageRepositoryProvider);
  }

  public static UsagePatternAnalyzer newInstance(UsageRepository usageRepository) {
    return new UsagePatternAnalyzer(usageRepository);
  }
}
