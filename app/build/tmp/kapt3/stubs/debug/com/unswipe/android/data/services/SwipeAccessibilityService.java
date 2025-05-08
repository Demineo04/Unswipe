package com.unswipe.android.data.services;

@dagger.hilt.android.AndroidEntryPoint()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0007\u0018\u0000 *2\u00020\u0001:\u0001*B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\u001e\u001a\u00020\n2\b\u0010\u001f\u001a\u0004\u0018\u00010\u0017H\u0002J\u0010\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020\u0017H\u0002J\u0012\u0010#\u001a\u00020!2\b\u0010$\u001a\u0004\u0018\u00010%H\u0016J\b\u0010&\u001a\u00020!H\u0016J\b\u0010\'\u001a\u00020!H\u0016J\b\u0010(\u001a\u00020!H\u0014J\u0010\u0010)\u001a\u00020!2\u0006\u0010\"\u001a\u00020\u0017H\u0002R\u001e\u0010\u0003\u001a\u00020\u00048\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0004\b\u0007\u0010\bR\u000e\u0010\t\u001a\u00020\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001e\u0010\u000f\u001a\u00020\u00108\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014R\u0014\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00170\u0016X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001e\u0010\u0018\u001a\u00020\u00198\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u001a\u0010\u001b\"\u0004\b\u001c\u0010\u001d\u00a8\u0006+"}, d2 = {"Lcom/unswipe/android/data/services/SwipeAccessibilityService;", "Landroid/accessibilityservice/AccessibilityService;", "()V", "injectedPackageManager", "Landroid/content/pm/PackageManager;", "getInjectedPackageManager", "()Landroid/content/pm/PackageManager;", "setInjectedPackageManager", "(Landroid/content/pm/PackageManager;)V", "isConfirmationEnabled", "", "serviceJob", "Lkotlinx/coroutines/CompletableJob;", "serviceScope", "Lkotlinx/coroutines/CoroutineScope;", "settingsRepository", "Lcom/unswipe/android/domain/repository/SettingsRepository;", "getSettingsRepository", "()Lcom/unswipe/android/domain/repository/SettingsRepository;", "setSettingsRepository", "(Lcom/unswipe/android/domain/repository/SettingsRepository;)V", "trackedAppPackages", "", "", "usageDao", "Lcom/unswipe/android/data/local/dao/UsageDao;", "getUsageDao", "()Lcom/unswipe/android/data/local/dao/UsageDao;", "setUsageDao", "(Lcom/unswipe/android/data/local/dao/UsageDao;)V", "isPotentiallyTargetActivity", "className", "logSwipeEvent", "", "packageName", "onAccessibilityEvent", "event", "Landroid/view/accessibility/AccessibilityEvent;", "onDestroy", "onInterrupt", "onServiceConnected", "showConfirmation", "Companion", "app_debug"})
public final class SwipeAccessibilityService extends android.accessibilityservice.AccessibilityService {
    @javax.inject.Inject()
    public com.unswipe.android.data.local.dao.UsageDao usageDao;
    @javax.inject.Inject()
    public com.unswipe.android.domain.repository.SettingsRepository settingsRepository;
    @javax.inject.Inject()
    public android.content.pm.PackageManager injectedPackageManager;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CompletableJob serviceJob = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope serviceScope = null;
    @org.jetbrains.annotations.NotNull()
    private java.util.Set<java.lang.String> trackedAppPackages;
    private boolean isConfirmationEnabled = true;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "SwipeAccessibility";
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Set<java.lang.String> DEFAULT_TARGET_PACKAGES = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.unswipe.android.data.services.SwipeAccessibilityService.Companion Companion = null;
    
    public SwipeAccessibilityService() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.unswipe.android.data.local.dao.UsageDao getUsageDao() {
        return null;
    }
    
    public final void setUsageDao(@org.jetbrains.annotations.NotNull()
    com.unswipe.android.data.local.dao.UsageDao p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.unswipe.android.domain.repository.SettingsRepository getSettingsRepository() {
        return null;
    }
    
    public final void setSettingsRepository(@org.jetbrains.annotations.NotNull()
    com.unswipe.android.domain.repository.SettingsRepository p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final android.content.pm.PackageManager getInjectedPackageManager() {
        return null;
    }
    
    public final void setInjectedPackageManager(@org.jetbrains.annotations.NotNull()
    android.content.pm.PackageManager p0) {
    }
    
    @java.lang.Override()
    public void onAccessibilityEvent(@org.jetbrains.annotations.Nullable()
    android.view.accessibility.AccessibilityEvent event) {
    }
    
    private final boolean isPotentiallyTargetActivity(java.lang.String className) {
        return false;
    }
    
    private final void logSwipeEvent(java.lang.String packageName) {
    }
    
    private final void showConfirmation(java.lang.String packageName) {
    }
    
    @java.lang.Override()
    public void onInterrupt() {
    }
    
    @java.lang.Override()
    protected void onServiceConnected() {
    }
    
    @java.lang.Override()
    public void onDestroy() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lcom/unswipe/android/data/services/SwipeAccessibilityService$Companion;", "", "()V", "DEFAULT_TARGET_PACKAGES", "", "", "TAG", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}