package com.unswipe.android.data.receivers;

@dagger.hilt.android.AndroidEntryPoint()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u0000 \u00132\u00020\u0001:\u0001\u0013B\u0005\u00a2\u0006\u0002\u0010\u0002J\u001c\u0010\r\u001a\u00020\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H\u0016R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001e\u0010\u0007\u001a\u00020\b8\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\f\u00a8\u0006\u0014"}, d2 = {"Lcom/unswipe/android/data/receivers/UnlockReceiver;", "Landroid/content/BroadcastReceiver;", "()V", "receiverJob", "Lkotlinx/coroutines/CompletableJob;", "receiverScope", "Lkotlinx/coroutines/CoroutineScope;", "usageDao", "Lcom/unswipe/android/data/local/dao/UsageDao;", "getUsageDao", "()Lcom/unswipe/android/data/local/dao/UsageDao;", "setUsageDao", "(Lcom/unswipe/android/data/local/dao/UsageDao;)V", "onReceive", "", "context", "Landroid/content/Context;", "intent", "Landroid/content/Intent;", "Companion", "app_debug"})
public final class UnlockReceiver extends android.content.BroadcastReceiver {
    @javax.inject.Inject()
    public com.unswipe.android.data.local.dao.UsageDao usageDao;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CompletableJob receiverJob = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope receiverScope = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "UnlockReceiver";
    @org.jetbrains.annotations.NotNull()
    public static final com.unswipe.android.data.receivers.UnlockReceiver.Companion Companion = null;
    
    public UnlockReceiver() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.unswipe.android.data.local.dao.UsageDao getUsageDao() {
        return null;
    }
    
    public final void setUsageDao(@org.jetbrains.annotations.NotNull()
    com.unswipe.android.data.local.dao.UsageDao p0) {
    }
    
    @java.lang.Override()
    public void onReceive(@org.jetbrains.annotations.Nullable()
    android.content.Context context, @org.jetbrains.annotations.Nullable()
    android.content.Intent intent) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/unswipe/android/data/receivers/UnlockReceiver$Companion;", "", "()V", "TAG", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}