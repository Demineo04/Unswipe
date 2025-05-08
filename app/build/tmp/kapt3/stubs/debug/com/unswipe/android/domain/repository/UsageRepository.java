package com.unswipe.android.domain.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a6@\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\u0007\u001a\u00020\bH\u00a6@\u00a2\u0006\u0002\u0010\tJ\u000e\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bH&J\u0010\u0010\r\u001a\u0004\u0018\u00010\u000eH\u00a6@\u00a2\u0006\u0002\u0010\tJ\u000e\u0010\u000f\u001a\u00020\u0010H\u00a6@\u00a2\u0006\u0002\u0010\tJ\u0014\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u000e0\u0012H\u00a6@\u00a2\u0006\u0002\u0010\tJ\u0016\u0010\u0013\u001a\u00020\u00032\u0006\u0010\u0014\u001a\u00020\u0015H\u00a6@\u00a2\u0006\u0002\u0010\u0016J\u000e\u0010\u0017\u001a\u00020\u0003H\u00a6@\u00a2\u0006\u0002\u0010\t\u00a8\u0006\u0018"}, d2 = {"Lcom/unswipe/android/domain/repository/UsageRepository;", "", "clearOldData", "", "olderThanTimestamp", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getCurrentStreak", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getDashboardDataFlow", "Lkotlinx/coroutines/flow/Flow;", "Lcom/unswipe/android/domain/model/DashboardData;", "getTodaysSummary", "Lcom/unswipe/android/domain/model/DailyUsageSummary;", "getTodaysUsageStats", "Lcom/unswipe/android/domain/model/TodayStats;", "getWeeklyUsageSummary", "", "logUsageEvent", "event", "Lcom/unswipe/android/data/model/UsageEvent;", "(Lcom/unswipe/android/data/model/UsageEvent;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "syncUsageToCloud", "app_debug"})
public abstract interface UsageRepository {
    
    /**
     * Logs a raw usage event (e.g., swipe, unlock, app open - if tracking opens this way).
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object logUsageEvent(@org.jetbrains.annotations.NotNull()
    com.unswipe.android.data.model.UsageEvent event, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Provides a Flow of the data needed for the main dashboard screen.
     * This likely combines data from UsageStatsManager, local DB summaries, and SettingsRepository.
     */
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.unswipe.android.domain.model.DashboardData> getDashboardDataFlow();
    
    /**
     * Gets the daily summary for today (might be needed by streak logic).
     * Should ideally calculate based on UsageStats and locally logged events (swipes/unlocks).
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTodaysSummary(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.unswipe.android.domain.model.DailyUsageSummary> $completion);
    
    /**
     * (Optional) Syncs relevant aggregated usage data (like summaries) to the cloud (e.g., Firestore).
     * Triggered periodically or manually.
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object syncUsageToCloud(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Deletes old usage events and/or summaries from the local database to manage storage.
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object clearOldData(long olderThanTimestamp, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTodaysUsageStats(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.unswipe.android.domain.model.TodayStats> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getCurrentStreak(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getWeeklyUsageSummary(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.unswipe.android.domain.model.DailyUsageSummary>> $completion);
}