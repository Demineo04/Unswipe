package com.unswipe.android.di;

import com.unswipe.android.data.analytics.UsagePatternAnalyzer;
import com.unswipe.android.domain.repository.UsageRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class ContextAwareModule_ProvideUsagePatternAnalyzerFactory implements Factory<UsagePatternAnalyzer> {
  private final Provider<UsageRepository> usageRepositoryProvider;

  public ContextAwareModule_ProvideUsagePatternAnalyzerFactory(
      Provider<UsageRepository> usageRepositoryProvider) {
    this.usageRepositoryProvider = usageRepositoryProvider;
  }

  @Override
  public UsagePatternAnalyzer get() {
    return provideUsagePatternAnalyzer(usageRepositoryProvider.get());
  }

  public static ContextAwareModule_ProvideUsagePatternAnalyzerFactory create(
      Provider<UsageRepository> usageRepositoryProvider) {
    return new ContextAwareModule_ProvideUsagePatternAnalyzerFactory(usageRepositoryProvider);
  }

  public static UsagePatternAnalyzer provideUsagePatternAnalyzer(UsageRepository usageRepository) {
    return Preconditions.checkNotNullFromProvides(ContextAwareModule.INSTANCE.provideUsagePatternAnalyzer(usageRepository));
  }
}
