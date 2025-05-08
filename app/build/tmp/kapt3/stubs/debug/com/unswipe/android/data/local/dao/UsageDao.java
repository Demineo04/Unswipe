package com.unswipe.android.data.local.dao;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\t\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0007\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J&\u0010\b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000b0\n0\t2\u0006\u0010\f\u001a\u00020\u00052\b\b\u0002\u0010\r\u001a\u00020\u000eH\'J\u0018\u0010\u000f\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\u0010\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u001e\u0010\u0011\u001a\u00020\u000e2\u0006\u0010\u0012\u001a\u00020\u00052\u0006\u0010\u0013\u001a\u00020\u0014H\u00a7@\u00a2\u0006\u0002\u0010\u0015J\u0010\u0010\u0016\u001a\u0004\u0018\u00010\u000bH\u00a7@\u00a2\u0006\u0002\u0010\u0017J\u001c\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\u0006\u0010\f\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J$\u0010\u0019\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001a0\n0\t2\u0006\u0010\u001b\u001a\u00020\u00052\u0006\u0010\u001c\u001a\u00020\u0005H\'J\u0016\u0010\u001d\u001a\u00020\u00032\u0006\u0010\u001e\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\u001fJ\u0016\u0010 \u001a\u00020\u00032\u0006\u0010!\u001a\u00020\u001aH\u00a7@\u00a2\u0006\u0002\u0010\"\u00a8\u0006#"}, d2 = {"Lcom/unswipe/android/data/local/dao/UsageDao;", "", "deleteOldSummaries", "", "olderThanTimestamp", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteOldUsageEvents", "getDailySummariesFlow", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/unswipe/android/data/model/DailyUsageSummary;", "startDateMillis", "limit", "", "getDailySummary", "dateMillis", "getEventCountSince", "startTimeMillis", "eventType", "", "(JLjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getLatestDailySummary", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getSummariesSince", "getUsageEventsForPeriodFlow", "Lcom/unswipe/android/data/model/UsageEvent;", "startTime", "endTime", "insertDailySummary", "summary", "(Lcom/unswipe/android/data/model/DailyUsageSummary;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "insertUsageEvent", "event", "(Lcom/unswipe/android/data/model/UsageEvent;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface UsageDao {
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertUsageEvent(@org.jetbrains.annotations.NotNull()
    com.unswipe.android.data.model.UsageEvent event, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertDailySummary(@org.jetbrains.annotations.NotNull()
    com.unswipe.android.data.model.DailyUsageSummary summary, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM usage_events WHERE timestamp >= :startTime AND timestamp < :endTime ORDER BY timestamp DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.unswipe.android.data.model.UsageEvent>> getUsageEventsForPeriodFlow(long startTime, long endTime);
    
    @androidx.room.Query(value = "SELECT * FROM daily_summaries WHERE dateMillis >= :startDateMillis ORDER BY dateMillis DESC LIMIT :limit")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.unswipe.android.data.model.DailyUsageSummary>> getDailySummariesFlow(long startDateMillis, int limit);
    
    @androidx.room.Query(value = "SELECT * FROM daily_summaries WHERE dateMillis = :dateMillis")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getDailySummary(long dateMillis, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.unswipe.android.data.model.DailyUsageSummary> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM daily_summaries ORDER BY dateMillis DESC LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getLatestDailySummary(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.unswipe.android.data.model.DailyUsageSummary> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM daily_summaries WHERE dateMillis >= :startDateMillis ORDER BY dateMillis ASC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getSummariesSince(long startDateMillis, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.unswipe.android.data.model.DailyUsageSummary>> $completion);
    
    @androidx.room.Query(value = "DELETE FROM usage_events WHERE timestamp < :olderThanTimestamp")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteOldUsageEvents(long olderThanTimestamp, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM daily_summaries WHERE dateMillis < :olderThanTimestamp")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteOldSummaries(long olderThanTimestamp, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM usage_events WHERE timestamp >= :startTimeMillis AND eventType = :eventType")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getEventCountSince(long startTimeMillis, @org.jetbrains.annotations.NotNull()
    java.lang.String eventType, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}