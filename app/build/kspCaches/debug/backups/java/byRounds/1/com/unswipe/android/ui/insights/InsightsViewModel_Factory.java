package com.unswipe.android.ui.insights;

import com.unswipe.android.data.analytics.UsagePatternAnalyzer;
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
public final class InsightsViewModel_Factory implements Factory<InsightsViewModel> {
  private final Provider<UsagePatternAnalyzer> usagePatternAnalyzerProvider;

  private final Provider<UsageRepository> usageRepositoryProvider;

  public InsightsViewModel_Factory(Provider<UsagePatternAnalyzer> usagePatternAnalyzerProvider,
      Provider<UsageRepository> usageRepositoryProvider) {
    this.usagePatternAnalyzerProvider = usagePatternAnalyzerProvider;
    this.usageRepositoryProvider = usageRepositoryProvider;
  }

  @Override
  public InsightsViewModel get() {
    return newInstance(usagePatternAnalyzerProvider.get(), usageRepositoryProvider.get());
  }

  public static InsightsViewModel_Factory create(
      Provider<UsagePatternAnalyzer> usagePatternAnalyzerProvider,
      Provider<UsageRepository> usageRepositoryProvider) {
    return new InsightsViewModel_Factory(usagePatternAnalyzerProvider, usageRepositoryProvider);
  }

  public static InsightsViewModel newInstance(UsagePatternAnalyzer usagePatternAnalyzer,
      UsageRepository usageRepository) {
    return new InsightsViewModel(usagePatternAnalyzer, usageRepository);
  }
}
