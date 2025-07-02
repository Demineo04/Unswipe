package com.unswipe.android.data.receivers;

import com.unswipe.android.data.local.dao.UsageDao;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class UnlockReceiver_MembersInjector implements MembersInjector<UnlockReceiver> {
  private final Provider<UsageDao> usageDaoProvider;

  public UnlockReceiver_MembersInjector(Provider<UsageDao> usageDaoProvider) {
    this.usageDaoProvider = usageDaoProvider;
  }

  public static MembersInjector<UnlockReceiver> create(Provider<UsageDao> usageDaoProvider) {
    return new UnlockReceiver_MembersInjector(usageDaoProvider);
  }

  @Override
  public void injectMembers(UnlockReceiver instance) {
    injectUsageDao(instance, usageDaoProvider.get());
  }

  @InjectedFieldSignature("com.unswipe.android.data.receivers.UnlockReceiver.usageDao")
  public static void injectUsageDao(UnlockReceiver instance, UsageDao usageDao) {
    instance.usageDao = usageDao;
  }
}
