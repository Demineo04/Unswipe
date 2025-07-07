package com.unswipe.android.ui.settings;

import com.unswipe.android.domain.repository.AuthRepository;
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
public final class ResetPasswordViewModel_Factory implements Factory<ResetPasswordViewModel> {
  private final Provider<AuthRepository> authRepositoryProvider;

  public ResetPasswordViewModel_Factory(Provider<AuthRepository> authRepositoryProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
  }

  @Override
  public ResetPasswordViewModel get() {
    return newInstance(authRepositoryProvider.get());
  }

  public static ResetPasswordViewModel_Factory create(
      Provider<AuthRepository> authRepositoryProvider) {
    return new ResetPasswordViewModel_Factory(authRepositoryProvider);
  }

  public static ResetPasswordViewModel newInstance(AuthRepository authRepository) {
    return new ResetPasswordViewModel(authRepository);
  }
}
