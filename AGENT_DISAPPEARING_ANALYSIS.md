# Agent Disappearing Analysis - Unswipe App

## Your "Agents" Identified

Based on your codebase analysis, your app has three main "agents" (background components):

### 1. **SwipeAccessibilityService** 
- **Location**: `app/src/main/java/com/unswipe/android/data/services/SwipeAccessibilityService.kt`
- **Purpose**: Detects swipes and app launches, shows confirmation dialogs
- **Status**: Most likely to "disappear"

### 2. **UsageTrackingWorker** 
- **Location**: `app/src/main/java/com/unswipe/android/data/workers/UsageTrackingWorker.kt` 
- **Purpose**: Periodically collects usage statistics and calculates daily summaries
- **Status**: Managed by WorkManager, more reliable

### 3. **UnlockReceiver**
- **Location**: `app/src/main/java/com/unswipe/android/data/receivers/UnlockReceiver.kt`
- **Purpose**: Detects device unlocks and logs them
- **Status**: System broadcast receiver, generally reliable

## Why Your Agents Are Disappearing

### 🔴 **Primary Culprit: SwipeAccessibilityService**

The accessibility service is most vulnerable to disappearing due to:

#### **1. User Disabling the Service**
```kotlin
// From SwipeAccessibilityService.kt:136
Log.i(TAG, "Accessibility Service connected.")
// From SwipeAccessibilityService.kt:131  
Log.w(TAG, "Accessibility Service interrupted.")
// From SwipeAccessibilityService.kt:179
Log.w(TAG, "Accessibility Service destroyed.")
```

**What happens:**
- Users can disable accessibility services in Settings > Accessibility
- Android may automatically disable it during system updates
- The service gets interrupted during low memory situations

#### **2. Android System Killing the Service**
- **Battery optimization**: Android kills accessibility services for battery saving
- **Memory pressure**: System terminates services when RAM is low  
- **App standby**: If main app isn't used, Android may kill associated services

#### **3. Permission Issues**
```kotlin
// From UsageTrackingWorker.kt:66
Log.e(TAG, "Permission potentially denied for UsageStatsManager", e)
```

**Critical permissions that can be revoked:**
- `PACKAGE_USAGE_STATS` - Can be disabled in Settings
- `BIND_ACCESSIBILITY_SERVICE` - Can be disabled in Accessibility settings

#### **4. Configuration Problems**
```kotlin
// From SwipeAccessibilityService.kt:158-166
Log.d(TAG, "Updating tracked packages: $trackedAppPackages, Confirmation enabled: $isConfirmationEnabled")
// ...
Log.e(TAG, "Error fetching settings in onServiceConnected", e)
```

The service tries to fetch settings dynamically but falls back to defaults if it fails.

### 🟡 **Secondary Issues: WorkManager Worker**

```kotlin
// From UsageTrackingWorker.kt:70
Log.e(TAG, "Error during usage tracking work", e)
return Result.retry() // Retry if it seems like a temporary error
```

**WorkManager issues:**
- **Doze mode**: Android may delay work in battery optimization
- **Background execution limits**: Android 8+ restricts background services
- **Permission revocation**: `PACKAGE_USAGE_STATS` permission can be removed

### 🟢 **Least Likely: UnlockReceiver**

This is the most reliable agent as it's a system broadcast receiver.

## How to Debug Agent Disappearance

### **1. Check Accessibility Service Status**
Add this method to check if your service is running:
```kotlin
fun isAccessibilityServiceEnabled(context: Context): Boolean {
    val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    val enabledServices = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    )
    return enabledServices?.contains("com.unswipe.android.data.services.SwipeAccessibilityService") == true
}
```

### **2. Monitor Service Lifecycle**
Your service already logs key events:
```bash
# Check logcat for these messages:
adb logcat | grep "SwipeAccessibility"
# Look for:
# - "Accessibility Service connected."
# - "Accessibility Service interrupted." 
# - "Accessibility Service destroyed."
```

### **3. Check WorkManager Status**
```kotlin
// Add to your debug code:
val workManager = WorkManager.getInstance(context)
val workInfos = workManager.getWorkInfosForUniqueWork("UsageTrackingWorker").get()
// Check if work is ENQUEUED, RUNNING, SUCCEEDED, FAILED, etc.
```

### **4. Monitor Permissions**
```kotlin
// Check if usage stats permission is still granted:
val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
val mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, 
    android.os.Process.myUid(), context.packageName)
val hasUsageStatsPermission = mode == AppOpsManager.MODE_ALLOWED
```

## Solutions to Prevent Agent Disappearance

### **1. Improve Service Resilience**
```kotlin
// Add to SwipeAccessibilityService:
override fun onUnbind(intent: Intent?): Boolean {
    Log.w(TAG, "Service unbound, attempting to reconnect...")
    // Return true to indicate we want onRebind() to be called
    return true
}

override fun onRebind(intent: Intent?) {
    super.onRebind(intent)
    Log.i(TAG, "Service rebound successfully")
}
```

### **2. Add Service Monitoring**
Create a periodic check to restart services:
```kotlin
class ServiceMonitorWorker : CoroutineWorker(...) {
    override suspend fun doWork(): Result {
        if (!isAccessibilityServiceEnabled(applicationContext)) {
            // Notify user or attempt to restart
            showServiceDisabledNotification()
        }
        return Result.success()
    }
}
```

### **3. User Education**
- Show clear instructions about keeping accessibility service enabled
- Warn users about battery optimization settings
- Provide troubleshooting steps when agents stop working

### **4. Graceful Degradation**
```kotlin
// In your main app logic:
if (!isAccessibilityServiceEnabled()) {
    // Fall back to alternative tracking methods
    // Show reduced functionality warning
}
```

## Immediate Action Items

1. **Add service status monitoring** to your main app
2. **Implement user notifications** when services are disabled  
3. **Add debug logging** to track when and why services stop
4. **Test on different Android versions** and manufacturers
5. **Document user setup instructions** clearly

## Root Cause Summary

Your agents disappear primarily because:
1. **Accessibility services are fragile** and easily disabled by system or user
2. **Android aggressively manages background processes** for battery life
3. **Permissions can be revoked** without user realizing the impact
4. **No monitoring system** to detect and recover from service failures

The SwipeAccessibilityService is your most critical and vulnerable agent - focus your debugging efforts there first.