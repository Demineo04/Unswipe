package com.unswipe.android.domain.model;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u000f\n\u0002\u0010\u0007\n\u0002\b\u0012\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0087\b\u0018\u0000 12\u00020\u0001:\u00011BS\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u0012\u0006\u0010\b\u001a\u00020\u0006\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n\u0012\u0006\u0010\f\u001a\u00020\r\u0012\u0006\u0010\u000e\u001a\u00020\r\u0012\u0006\u0010\u000f\u001a\u00020\r\u00a2\u0006\u0002\u0010\u0010J\t\u0010\"\u001a\u00020\u0003H\u00c6\u0003J\t\u0010#\u001a\u00020\u0003H\u00c6\u0003J\t\u0010$\u001a\u00020\u0006H\u00c6\u0003J\t\u0010%\u001a\u00020\u0006H\u00c6\u0003J\t\u0010&\u001a\u00020\u0006H\u00c6\u0003J\u000f\u0010\'\u001a\b\u0012\u0004\u0012\u00020\u000b0\nH\u00c6\u0003J\t\u0010(\u001a\u00020\rH\u00c6\u0003J\t\u0010)\u001a\u00020\rH\u00c6\u0003J\t\u0010*\u001a\u00020\rH\u00c6\u0003Ji\u0010+\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u00062\b\b\u0002\u0010\b\u001a\u00020\u00062\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\r2\b\b\u0002\u0010\u000f\u001a\u00020\rH\u00c6\u0001J\u0013\u0010,\u001a\u00020\r2\b\u0010-\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010.\u001a\u00020\u0006H\u00d6\u0001J\t\u0010/\u001a\u000200H\u00d6\u0001R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u000e\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u000f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0014R\u0011\u0010\f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u0014R\u0011\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0012R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\u0018\u001a\u00020\u00038F\u00a2\u0006\u0006\u001a\u0004\b\u0019\u0010\u0017R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0017R\u0011\u0010\b\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0012R\u0011\u0010\u001c\u001a\u00020\u001d8F\u00a2\u0006\u0006\u001a\u0004\b\u001e\u0010\u001fR\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010!\u00a8\u00062"}, d2 = {"Lcom/unswipe/android/domain/model/DashboardData;", "", "timeUsedTodayMillis", "", "timeLimitMillis", "currentStreak", "", "swipesToday", "unlocksToday", "weeklyProgress", "", "Lcom/unswipe/android/domain/model/DailyUsageSummary;", "isPremium", "", "hasUsageStatsPermission", "isAccessibilityEnabled", "(JJIIILjava/util/List;ZZZ)V", "getCurrentStreak", "()I", "getHasUsageStatsPermission", "()Z", "getSwipesToday", "getTimeLimitMillis", "()J", "timeRemainingMillis", "getTimeRemainingMillis", "getTimeUsedTodayMillis", "getUnlocksToday", "usagePercentage", "", "getUsagePercentage", "()F", "getWeeklyProgress", "()Ljava/util/List;", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "toString", "", "Companion", "app_debug"})
public final class DashboardData {
    private final long timeUsedTodayMillis = 0L;
    private final long timeLimitMillis = 0L;
    private final int currentStreak = 0;
    private final int swipesToday = 0;
    private final int unlocksToday = 0;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.unswipe.android.domain.model.DailyUsageSummary> weeklyProgress = null;
    private final boolean isPremium = false;
    private final boolean hasUsageStatsPermission = false;
    private final boolean isAccessibilityEnabled = false;
    @org.jetbrains.annotations.NotNull()
    private static final com.unswipe.android.domain.model.DashboardData Loading = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.unswipe.android.domain.model.DashboardData.Companion Companion = null;
    
    public DashboardData(long timeUsedTodayMillis, long timeLimitMillis, int currentStreak, int swipesToday, int unlocksToday, @org.jetbrains.annotations.NotNull()
    java.util.List<com.unswipe.android.domain.model.DailyUsageSummary> weeklyProgress, boolean isPremium, boolean hasUsageStatsPermission, boolean isAccessibilityEnabled) {
        super();
    }
    
    public final long getTimeUsedTodayMillis() {
        return 0L;
    }
    
    public final long getTimeLimitMillis() {
        return 0L;
    }
    
    public final int getCurrentStreak() {
        return 0;
    }
    
    public final int getSwipesToday() {
        return 0;
    }
    
    public final int getUnlocksToday() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.unswipe.android.domain.model.DailyUsageSummary> getWeeklyProgress() {
        return null;
    }
    
    public final boolean isPremium() {
        return false;
    }
    
    public final boolean getHasUsageStatsPermission() {
        return false;
    }
    
    public final boolean isAccessibilityEnabled() {
        return false;
    }
    
    public final long getTimeRemainingMillis() {
        return 0L;
    }
    
    public final float getUsagePercentage() {
        return 0.0F;
    }
    
    public final long component1() {
        return 0L;
    }
    
    public final long component2() {
        return 0L;
    }
    
    public final int component3() {
        return 0;
    }
    
    public final int component4() {
        return 0;
    }
    
    public final int component5() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.unswipe.android.domain.model.DailyUsageSummary> component6() {
        return null;
    }
    
    public final boolean component7() {
        return false;
    }
    
    public final boolean component8() {
        return false;
    }
    
    public final boolean component9() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.unswipe.android.domain.model.DashboardData copy(long timeUsedTodayMillis, long timeLimitMillis, int currentStreak, int swipesToday, int unlocksToday, @org.jetbrains.annotations.NotNull()
    java.util.List<com.unswipe.android.domain.model.DailyUsageSummary> weeklyProgress, boolean isPremium, boolean hasUsageStatsPermission, boolean isAccessibilityEnabled) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/unswipe/android/domain/model/DashboardData$Companion;", "", "()V", "Loading", "Lcom/unswipe/android/domain/model/DashboardData;", "getLoading", "()Lcom/unswipe/android/domain/model/DashboardData;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.unswipe.android.domain.model.DashboardData getLoading() {
            return null;
        }
    }
}