package com.unswipe.android.domain.repository;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\"\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\f\bf\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a6@\u00a2\u0006\u0002\u0010\u0006J\u0014\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\t0\bH&J\u000e\u0010\n\u001a\u00020\u000bH\u00a6@\u00a2\u0006\u0002\u0010\fJ\u000e\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\bH&J\u000e\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u000b0\bH&J\u000e\u0010\u0010\u001a\u00020\u000eH\u00a6@\u00a2\u0006\u0002\u0010\fJ\u000e\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\bH&J\u000e\u0010\u0013\u001a\u00020\u0003H\u00a6@\u00a2\u0006\u0002\u0010\fJ\u0016\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0004\u001a\u00020\u0005H\u00a6@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0016\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a6@\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\u0017\u001a\u00020\u0003H\u00a6@\u00a2\u0006\u0002\u0010\fJ\u0016\u0010\u0018\u001a\u00020\u00032\u0006\u0010\u0019\u001a\u00020\u0015H\u00a6@\u00a2\u0006\u0002\u0010\u001aJ\u0016\u0010\u001b\u001a\u00020\u00032\u0006\u0010\u001c\u001a\u00020\u000eH\u00a6@\u00a2\u0006\u0002\u0010\u001dJ\u0016\u0010\u001e\u001a\u00020\u00032\u0006\u0010\u001f\u001a\u00020\u000bH\u00a6@\u00a2\u0006\u0002\u0010 \u00a8\u0006!"}, d2 = {"Lcom/unswipe/android/domain/repository/SettingsRepository;", "", "addBlockedApp", "", "packageName", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getBlockedApps", "Lkotlinx/coroutines/flow/Flow;", "", "getCurrentStreak", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getDailyLimitFlow", "", "getStreakFlow", "getTimeLimitMillis", "getUserSettings", "Lcom/unswipe/android/domain/model/UserSettings;", "incrementStreak", "isAppBlocked", "", "removeBlockedApp", "resetStreak", "setPremiumStatus", "isPremium", "(ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateDailyLimit", "limitMillis", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateStreak", "streak", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public abstract interface SettingsRepository {
    
    /**
     * Provides a Flow of the user's complete settings.
     * Consider if this is needed or if individual flows are better.
     */
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.unswipe.android.domain.model.UserSettings> getUserSettings();
    
    /**
     * Updates the user's chosen daily screen time limit.
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateDailyLimit(long limitMillis, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Gets a Flow that emits the current daily limit whenever it changes.
     */
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.lang.Long> getDailyLimitFlow();
    
    /**
     * Updates the user's current streak count directly (use with caution).
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateStreak(int streak, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Resets the user's streak count to zero.
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object resetStreak(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Increments the user's streak count by one.
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object incrementStreak(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Gets a Flow that emits the current streak count whenever it changes.
     */
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.lang.Integer> getStreakFlow();
    
    /**
     * Gets the current streak count.
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getCurrentStreak(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    /**
     * Updates the user's premium status (e.g., after a billing change).
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object setPremiumStatus(boolean isPremium, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Gets a Flow that emits the set of package names marked for blocking/confirmation.
     */
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.Set<java.lang.String>> getBlockedApps();
    
    /**
     * Adds an app's package name to the blocked list.
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object addBlockedApp(@org.jetbrains.annotations.NotNull()
    java.lang.String packageName, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Removes an app's package name from the blocked list.
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object removeBlockedApp(@org.jetbrains.annotations.NotNull()
    java.lang.String packageName, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Checks if a specific app is currently in the blocked list.
     * (Optional, might be derived from getBlockedApps().first().contains(...))
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object isAppBlocked(@org.jetbrains.annotations.NotNull()
    java.lang.String packageName, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion);
    
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getTimeLimitMillis(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
}