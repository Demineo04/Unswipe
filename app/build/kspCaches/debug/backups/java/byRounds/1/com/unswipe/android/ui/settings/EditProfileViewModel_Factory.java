package com.unswipe.android.ui.settings;

import com.unswipe.android.domain.repository.AuthRepository;
import com.unswipe.android.domain.repository.SettingsRepository;
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
public final class EditProfileViewModel_Factory implements Factory<EditProfileViewModel> {
  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<SettingsRepository> settingsRepositoryProvider;

  public EditProfileViewModel_Factory(Provider<AuthRepository> authRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
    this.settingsRepositoryProvider = settingsRepositoryProvider;
  }

  @Override
  public EditProfileViewModel get() {
    return newInstance(authRepositoryProvider.get(), settingsRepositoryProvider.get());
  }

  public static EditProfileViewModel_Factory create(Provider<AuthRepository> authRepositoryProvider,
      Provider<SettingsRepository> settingsRepositoryProvider) {
    return new EditProfileViewModel_Factory(authRepositoryProvider, settingsRepositoryProvider);
  }

  public static EditProfileViewModel newInstance(AuthRepository authRepository,
      SettingsRepository settingsRepository) {
    return new EditProfileViewModel(authRepository, settingsRepository);
  }
}
