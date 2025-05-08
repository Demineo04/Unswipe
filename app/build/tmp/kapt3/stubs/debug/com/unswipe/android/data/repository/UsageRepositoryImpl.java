package com.unswipe.android.data.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000x\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B/\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u00a2\u0006\u0002\u0010\fJ\u0016\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010H\u0096@\u00a2\u0006\u0002\u0010\u0011J\u000e\u0010\u0012\u001a\u00020\u0013H\u0096@\u00a2\u0006\u0002\u0010\u0014J\u000e\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00170\u0016H\u0016J\b\u0010\u0018\u001a\u00020\u0010H\u0002J\b\u0010\u0019\u001a\u00020\u0010H\u0002J\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u001bH\u0096@\u00a2\u0006\u0002\u0010\u0014J\u000e\u0010\u001c\u001a\u00020\u001dH\u0096@\u00a2\u0006\u0002\u0010\u0014J\u0018\u0010\u001e\u001a\u00020\u00102\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\u001f\u001a\u00020\u0010H\u0002J\u0014\u0010 \u001a\b\u0012\u0004\u0012\u00020\u001b0!H\u0096@\u00a2\u0006\u0002\u0010\u0014J\u0010\u0010\"\u001a\u00020#2\u0006\u0010\n\u001a\u00020\u000bH\u0002J\u0010\u0010$\u001a\u00020#2\u0006\u0010\n\u001a\u00020\u000bH\u0002J\u0016\u0010%\u001a\u00020\u000e2\u0006\u0010&\u001a\u00020\'H\u0096@\u00a2\u0006\u0002\u0010(J\u0010\u0010)\u001a\u00020\u001b2\u0006\u0010*\u001a\u00020+H\u0002J\u000e\u0010,\u001a\u00020\u000eH\u0096@\u00a2\u0006\u0002\u0010\u0014R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006-"}, d2 = {"Lcom/unswipe/android/data/repository/UsageRepositoryImpl;", "Lcom/unswipe/android/domain/repository/UsageRepository;", "usageDao", "Lcom/unswipe/android/data/local/dao/UsageDao;", "usageStatsManager", "Landroid/app/usage/UsageStatsManager;", "firestore", "Lcom/google/firebase/firestore/FirebaseFirestore;", "settingsRepository", "Lcom/unswipe/android/domain/repository/SettingsRepository;", "context", "Landroid/content/Context;", "(Lcom/unswipe/android/data/local/dao/UsageDao;Landroid/app/usage/UsageStatsManager;Lcom/google/firebase/firestore/FirebaseFirestore;Lcom/unswipe/android/domain/repository/SettingsRepository;Landroid/content/Context;)V", "clearOldData", "", "olderThanTimestamp", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getCurrentStreak", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getDashboardDataFlow", "Lkotlinx/coroutines/flow/Flow;", "Lcom/unswipe/android/domain/model/DashboardData;", "getStartOfDayInMillis", "getStartOfWeekInMillis", "getTodaysSummary", "Lcom/unswipe/android/domain/model/DailyUsageSummary;", "getTodaysUsageStats", "Lcom/unswipe/android/domain/model/TodayStats;", "getUsageTimeFromManager", "startTime", "getWeeklyUsageSummary", "", "hasUsageStatsPermission", "", "isAccessibilityServiceEnabled", "logUsageEvent", "event", "Lcom/unswipe/android/data/model/UsageEvent;", "(Lcom/unswipe/android/data/model/UsageEvent;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "mapDataSummaryToDomain", "dataSummary", "Lcom/unswipe/android/data/model/DailyUsageSummary;", "syncUsageToCloud", "app_debug"})
public final class UsageRepositoryImpl implements com.unswipe.android.domain.repository.UsageRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.unswipe.android.data.local.dao.UsageDao usageDao = null;
    @org.jetbrains.annotations.NotNull()
    private final android.app.usage.UsageStatsManager usageStatsManager = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.firebase.firestore.FirebaseFirestore firestore = null;
    @org.jetbrains.annotations.NotNull()
    private final com.unswipe.android.domain.repository.SettingsRepository settingsRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    
    @javax.inject.Inject()
    public UsageRepositoryImpl(@org.jetbrains.annotations.NotNull()
    com.unswipe.android.data.local.dao.UsageDao usageDao, @org.jetbrains.annotations.NotNull()
    android.app.usage.UsageStatsManager usageStatsManager, @org.jetbrains.annotations.NotNull()
    com.google.firebase.firestore.FirebaseFirestore firestore, @org.jetbrains.annotations.NotNull()
    com.unswipe.android.domain.repository.SettingsRepository settingsRepository, @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object logUsageEvent(@org.jetbrains.annotations.NotNull()
    com.unswipe.android.data.model.UsageEvent event, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<com.unswipe.android.domain.model.DashboardData> getDashboardDataFlow() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getTodaysSummary(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.unswipe.android.domain.model.DailyUsageSummary> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object syncUsageToCloud(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object clearOldData(long olderThanTimestamp, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getTodaysUsageStats(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.unswipe.android.domain.model.TodayStats> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getCurrentStreak(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object getWeeklyUsageSummary(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.unswipe.android.domain.model.DailyUsageSummary>> $completion) {
        return null;
    }
    
    private final com.unswipe.android.domain.model.DailyUsageSummary mapDataSummaryToDomain(com.unswipe.android.data.model.DailyUsageSummary dataSummary) {
        return null;
    }
    
    private final long getStartOfDayInMillis() {
        return 0L;
    }
    
    private final long getStartOfWeekInMillis() {
        return 0L;
    }
    
    private final boolean hasUsageStatsPermission(android.content.Context context) {
        return false;
    }
    
    private final boolean isAccessibilityServiceEnabled(android.content.Context context) {
        return false;
    }
    
    private final long getUsageTimeFromManager(android.content.Context context, long startTime) {
        return 0L;
    }
}