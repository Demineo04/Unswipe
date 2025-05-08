package com.unswipe.android.data.workers;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0007\u0018\u0000 !2\u00020\u0001:\u0001!BI\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r\u0012\u0006\u0010\u000f\u001a\u00020\u0010\u00a2\u0006\u0002\u0010\u0011J\u0016\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015H\u0082@\u00a2\u0006\u0002\u0010\u0016J\u000e\u0010\u0017\u001a\u00020\u0018H\u0096@\u00a2\u0006\u0002\u0010\u0019J\u0010\u0010\u001a\u001a\u00020\u00152\u0006\u0010\u001b\u001a\u00020\u0015H\u0002J\u0018\u0010\u001c\u001a\u00020\u00132\b\u0010\u001d\u001a\u0004\u0018\u00010\u001eH\u0082@\u00a2\u0006\u0002\u0010\u001fJ\u0016\u0010 \u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015H\u0082@\u00a2\u0006\u0002\u0010\u0016R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\""}, d2 = {"Lcom/unswipe/android/data/workers/UsageTrackingWorker;", "Landroidx/work/CoroutineWorker;", "appContext", "Landroid/content/Context;", "workerParams", "Landroidx/work/WorkerParameters;", "usageStatsManager", "Landroid/app/usage/UsageStatsManager;", "usageDao", "Lcom/unswipe/android/data/local/dao/UsageDao;", "settingsRepository", "Lcom/unswipe/android/domain/repository/SettingsRepository;", "preferencesDataStore", "Landroidx/datastore/core/DataStore;", "Landroidx/datastore/preferences/core/Preferences;", "packageManager", "Landroid/content/pm/PackageManager;", "(Landroid/content/Context;Landroidx/work/WorkerParameters;Landroid/app/usage/UsageStatsManager;Lcom/unswipe/android/data/local/dao/UsageDao;Lcom/unswipe/android/domain/repository/SettingsRepository;Landroidx/datastore/core/DataStore;Landroid/content/pm/PackageManager;)V", "calculateAndSaveDailySummary", "", "currentTime", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "doWork", "Landroidx/work/ListenableWorker$Result;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getStartOfDayMillis", "timestamp", "processUsageEvents", "usageEvents", "Landroid/app/usage/UsageEvents;", "(Landroid/app/usage/UsageEvents;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateStreak", "Companion", "app_debug"})
@androidx.hilt.work.HiltWorker()
public final class UsageTrackingWorker extends androidx.work.CoroutineWorker {
    @org.jetbrains.annotations.NotNull()
    private final android.app.usage.UsageStatsManager usageStatsManager = null;
    @org.jetbrains.annotations.NotNull()
    private final com.unswipe.android.data.local.dao.UsageDao usageDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.unswipe.android.domain.repository.SettingsRepository settingsRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> preferencesDataStore = null;
    @org.jetbrains.annotations.NotNull()
    private final android.content.pm.PackageManager packageManager = null;
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String WORK_NAME = "UsageTrackingWorker";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "UsageTrackingWorker";
    private static final long QUERY_INTERVAL_MINUTES = 0L;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Long> LAST_STREAK_UPDATE_KEY = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.unswipe.android.data.workers.UsageTrackingWorker.Companion Companion = null;
    
    @dagger.assisted.AssistedInject()
    public UsageTrackingWorker(@dagger.assisted.Assisted()
    @org.jetbrains.annotations.NotNull()
    android.content.Context appContext, @dagger.assisted.Assisted()
    @org.jetbrains.annotations.NotNull()
    androidx.work.WorkerParameters workerParams, @org.jetbrains.annotations.NotNull()
    android.app.usage.UsageStatsManager usageStatsManager, @org.jetbrains.annotations.NotNull()
    com.unswipe.android.data.local.dao.UsageDao usageDao, @org.jetbrains.annotations.NotNull()
    com.unswipe.android.domain.repository.SettingsRepository settingsRepository, @org.jetbrains.annotations.NotNull()
    androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences> preferencesDataStore, @org.jetbrains.annotations.NotNull()
    android.content.pm.PackageManager packageManager) {
        super(null, null);
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object doWork(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super androidx.work.ListenableWorker.Result> $completion) {
        return null;
    }
    
    private final java.lang.Object processUsageEvents(android.app.usage.UsageEvents usageEvents, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final long getStartOfDayMillis(long timestamp) {
        return 0L;
    }
    
    private final java.lang.Object calculateAndSaveDailySummary(long currentTime, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final java.lang.Object updateStreak(long currentTime, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0017\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u000e\u0010\b\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\nX\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/unswipe/android/data/workers/UsageTrackingWorker$Companion;", "", "()V", "LAST_STREAK_UPDATE_KEY", "Landroidx/datastore/preferences/core/Preferences$Key;", "", "getLAST_STREAK_UPDATE_KEY", "()Landroidx/datastore/preferences/core/Preferences$Key;", "QUERY_INTERVAL_MINUTES", "TAG", "", "WORK_NAME", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Long> getLAST_STREAK_UPDATE_KEY() {
            return null;
        }
    }
}