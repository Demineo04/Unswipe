// Location: app/src/main/java/com/unswipe/android/data/services/SwipeAccessibilityService.kt

package com.unswipe.android.data.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
// import android.os.Build // Only if needed for specific SDK checks
import android.util.Log
import android.view.accessibility.AccessibilityEvent
// import android.view.accessibility.AccessibilityNodeInfo // Only if inspecting nodes
import com.unswipe.android.data.local.dao.UsageDao
import com.unswipe.android.data.model.EventType
import com.unswipe.android.data.model.UsageEvent
import com.unswipe.android.domain.repository.SettingsRepository
import com.unswipe.android.ui.confirmation.ConfirmationActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

@AndroidEntryPoint
class SwipeAccessibilityService : AccessibilityService() {

    @Inject lateinit var usageDao: UsageDao
    @Inject lateinit var settingsRepository: SettingsRepository
    @Inject lateinit var injectedPackageManager: PackageManager

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    private var trackedAppPackages: Set<String> = emptySet()
    private var isConfirmationEnabled = true // Default, should be fetched
    private var lastConfirmationTime = 0L
    private var lastConfirmationPackage = ""
    private val CONFIRMATION_DEBOUNCE_MS = 3000L // 3 seconds between confirmations for same app

    // --- FIX: Move constants to companion object ---
    companion object {
        private const val TAG = "SwipeAccessibility" // const is fine for String

        // Use 'val' not 'const val' for complex types like List
        private val DEFAULT_TARGET_PACKAGES = listOf(
            "com.zhiliaoapp.musically", // TikTok
            "com.instagram.android",
            "com.google.android.youtube",
            "com.facebook.katana", // Facebook
            "com.snapchat.android",
            "com.linkedin.android"
            // Add others as needed, but ideally fetch from settings
        ).toSet() // Convert to Set for efficient lookup
    }
    // --- End Fix ---


    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val packageName = event.packageName?.toString() ?: return

        // Early exit if not tracking this package
        if (!trackedAppPackages.contains(packageName)) return

        when (event.eventType) {
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                logSwipeEvent(packageName)
                // Log.i(TAG, "Detected scroll/swipe in $packageName") // Verbose logging
            }
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                if (event.className != null && isConfirmationEnabled) {
                    val className = event.className.toString()
                    // Heuristic: check if it's an Activity class name and not our own confirmation
                    if (isPotentiallyTargetActivity(className) && packageName != applicationContext.packageName) {
                        serviceScope.launch {
                            // Fetch dynamic settings inside coroutine
                            val isBlocked = settingsRepository.isAppBlocked(packageName)
                            if (isBlocked && shouldShowConfirmation(packageName)) {
                                showConfirmation(packageName)
                            }
                        }
                    }
                }
            }
            // Handle other events if needed
        }
    }

    // Basic heuristic to guess if window change is for an app's main activity
    private fun isPotentiallyTargetActivity(className: String?): Boolean {
        return className != null &&
                (className.contains("Activity", ignoreCase = true) || className.contains("Shell", ignoreCase=true) )&& // Common patterns
                className != ConfirmationActivity::class.java.name // Exclude our own
    }

    // Debounce mechanism to prevent spam
    private fun shouldShowConfirmation(packageName: String): Boolean {
        val currentTime = System.currentTimeMillis()
        val shouldShow = packageName != lastConfirmationPackage || 
                        (currentTime - lastConfirmationTime) > CONFIRMATION_DEBOUNCE_MS
        
        if (shouldShow) {
            lastConfirmationTime = currentTime
            lastConfirmationPackage = packageName
        }
        
        return shouldShow
    }

    private fun logSwipeEvent(packageName: String) {
        serviceScope.launch {
            try {
                usageDao.insertUsageEvent(
                    UsageEvent(
                        timestamp = System.currentTimeMillis(),
                        packageName = packageName,
                        eventType = EventType.SWIPE.name // Use .name to get the String "SWIPE"
                    )
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error logging swipe event", e)
            }
        }
    }

    private fun showConfirmation(packageName: String) {
        val appName = try {
            val appInfo: ApplicationInfo = injectedPackageManager.getApplicationInfo(packageName, 0)
            injectedPackageManager.getApplicationLabel(appInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            Log.w(TAG, "Could not get app name for $packageName", e)
            packageName // Fallback
        }
        Log.i(TAG, "Showing confirmation prompt for $appName ($packageName)")

        val intent = ConfirmationActivity.newIntent(this, appName, packageName)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        try {
            startActivity(intent)
            // Block the target app from opening by going back
            performGlobalAction(GLOBAL_ACTION_BACK)
        } catch (e: Exception) {
            Log.e(TAG,"Error starting ConfirmationActivity", e)
        }
    }


    override fun onInterrupt() {
        Log.w(TAG, "Accessibility Service interrupted.")
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.i(TAG, "Accessibility Service connected.")
        val serviceInfo = AccessibilityServiceInfo()
        serviceInfo.apply {
            eventTypes = AccessibilityEvent.TYPE_VIEW_SCROLLED or
                    AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            notificationTimeout = 100
            // description = getString(R.string.accessibility_service_description) // Load from strings.xml
            flags = AccessibilityServiceInfo.DEFAULT or
                    AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS // or others as needed
        }
        this.serviceInfo = serviceInfo // Apply base config

        // Fetch dynamic config in background
        serviceScope.launch {
            try {
                // Fetch dynamic list of tracked apps from settings
                val blockedApps = settingsRepository.getBlockedApps().firstOrNull()
                trackedAppPackages = blockedApps ?: DEFAULT_TARGET_PACKAGES // Use default if null/empty?
                // Fetch if confirmation is enabled globally (add to SettingsRepository)
                // isConfirmationEnabled = settingsRepository.isConfirmationGloballyEnabled().firstOrNull() ?: true // Example method needed in SettingsRepo

                Log.d(TAG, "Updating tracked packages: $trackedAppPackages, Confirmation enabled: $isConfirmationEnabled")

                // Dynamically update the service info with fetched packages
                val currentServiceInfo = this@SwipeAccessibilityService.serviceInfo
                currentServiceInfo.packageNames = trackedAppPackages.toTypedArray()
                this@SwipeAccessibilityService.serviceInfo = currentServiceInfo // Re-apply updated info

            } catch (e: Exception) {
                Log.e(TAG, "Error fetching settings in onServiceConnected", e)
                // Fallback to defaults if settings fetch fails
                trackedAppPackages = DEFAULT_TARGET_PACKAGES
                isConfirmationEnabled = true // Safer default?
                val currentServiceInfo = this@SwipeAccessibilityService.serviceInfo
                currentServiceInfo.packageNames = trackedAppPackages.toTypedArray()
                this@SwipeAccessibilityService.serviceInfo = currentServiceInfo
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.w(TAG, "Accessibility Service destroyed.")
        serviceJob.cancel() // Cancel coroutines
    }

    // Example method needed in SettingsRepository interface and implementation
    // fun SettingsRepository.isConfirmationGloballyEnabled(): Flow<Boolean>
}