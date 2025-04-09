package com.unswipe.android.data.services

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.unswipe.android.data.local.dao.UsageDao
import com.unswipe.android.data.model.EventType
import com.unswipe.android.data.model.UsageEvent
import com.unswipe.android.domain.repository.SettingsRepository
import com.unswipe.android.ui.confirmation.ConfirmationActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class SwipeAccessibilityService : AccessibilityService() {

    @Inject lateinit var usageDao: UsageDao
    @Inject lateinit var settingsRepository: SettingsRepository
    @Inject lateinit var packageManager: PackageManager // Inject PackageManager

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    // Cache settings locally to reduce DataStore reads on every event
    private var cachedBlockedApps: Set<String> = emptySet()
    private var isConfirmationEnabled: Boolean = true // Default, updated from settings
    private var isServiceConfigured = false

    // Debounce confirmation dialogs to prevent spamming if events fire rapidly
    private var lastConfirmationShownTime: Long = 0
    private const val CONFIRMATION_DEBOUNCE_MS = 2000 // 2 seconds


    companion object {
        private const val TAG = "SwipeAccessibility"
        // Fallback list if settings can't be loaded initially
        private val DEFAULT_TARGET_PACKAGES = listOf(
            "com.zhiliaoapp.musically", // TikTok
            "com.instagram.android",
            "com.google.android.youtube",
            "com.facebook.katana", // Facebook
            "com.snapchat.android",
            "com.linkedin.android",
            "com.twitter.android", // X / Twitter
            // Add others as needed
        )
    }

     override fun onServiceConnected() {
        super.onServiceConnected()
        Log.i(TAG, "Accessibility Service connected. Configuring...")
        isServiceConfigured = false // Reset flag
        updateServiceConfiguration() // Initial configuration fetch
    }

    private fun updateServiceConfiguration() {
        serviceScope.launch {
            try {
                // Fetch confirmation setting and blocked apps list from repository
                val settings = settingsRepository.getUserSettings().firstOrNull()
                cachedBlockedApps = settings?.blockedApps ?: emptySet()
                isConfirmationEnabled = true // TODO: Fetch actual preference from settings if user can disable it

                // If blocked apps list is empty, maybe default to common ones? Or let user explicitly select?
                // Using DEFAULT_TARGET_PACKAGES if empty might be unexpected. Let's use the stored set.
                val packagesToTrack = if (cachedBlockedApps.isNotEmpty()) cachedBlockedApps else DEFAULT_TARGET_PACKAGES.toSet()
                Log.d(TAG, "Updating configuration: Confirmation enabled=$isConfirmationEnabled, Tracking=${packagesToTrack.size} apps: $packagesToTrack")


                val serviceInfo = AccessibilityServiceInfo().apply {
                    eventTypes = AccessibilityEvent.TYPE_VIEW_SCROLLED or
                                 AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or
                                 AccessibilityEvent.TYPE_WINDOWS_CHANGED // Might help catch some tricky launches

                    packageNames = packagesToTrack.toTypedArray().takeIf { it.isNotEmpty() }
                        ?: DEFAULT_TARGET_PACKAGES.toTypedArray() // Use default if settings list is empty

                    feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
                    notificationTimeout = 100 // ms
                    // Load description from strings.xml (ensure it's defined)
                    // description = getString(R.string.accessibility_service_description)

                    flags = AccessibilityServiceInfo.DEFAULT or
                            AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS or
                            // AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or // Use cautiously
                            AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS // Often needed

                    // Capabilities (Optional, if needed for performGlobalAction)
                    // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //     capabilities = capabilities or AccessibilityServiceInfo.CAPABILITY_CAN_PERFORM_GESTURES
                    // }
                }
                this@SwipeAccessibilityService.serviceInfo = serviceInfo // Apply the configuration
                isServiceConfigured = true
                Log.i(TAG, "Accessibility Service configuration updated.")

            } catch (e: Exception) {
                 Log.e(TAG, "Error fetching settings for service configuration", e)
                 // Optionally retry or use defaults
                  if (!isServiceConfigured) { // If first config failed, use defaults
                       Log.w(TAG, "Failed to load settings, using default package list.")
                       val serviceInfoDefaults = AccessibilityServiceInfo().apply { /* Set defaults */ }
                       serviceInfoDefaults.packageNames = DEFAULT_TARGET_PACKAGES.toTypedArray()
                       // set other defaults as above
                       this@SwipeAccessibilityService.serviceInfo = serviceInfoDefaults
                       isServiceConfigured = true // Mark as configured even with defaults
                  }
            }
        }
    }


    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
         if (!isServiceConfigured || event == null) {
            // Log.v(TAG, "Ignoring event, service not configured yet.")
             return // Don't process events until configured
         }

        val packageName = event.packageName?.toString() ?: return

        // Double-check against cached list (serviceInfo might lag slightly?)
        if (!cachedBlockedApps.contains(packageName)) {
            // Log.v(TAG, "Ignoring event from non-tracked package: $packageName")
            return
        }
       // Log.d(TAG, "Event: ${AccessibilityEvent.eventTypeToString(event.eventType)} from $packageName Class: ${event.className} Time: ${event.eventTime}")

        when (event.eventType) {
            // --- Swipe Detection ---
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                // Optimization: Could add debounce here if needed
                logSwipeEvent(packageName, event.eventTime)
               // Log.i(TAG, "Detected scroll/swipe in $packageName")
            }

            // --- App Launch Detection & Confirmation ---
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED, AccessibilityEvent.TYPE_WINDOWS_CHANGED -> {
                 if (event.packageName != null &&
                    isConfirmationEnabled &&
                    event.className != null &&
                    event.className.toString() != ConfirmationActivity::class.java.name && // Ignore our own confirmation
                    !event.className.toString().contains("InputMethod") && // Ignore keyboards
                    event.packageName == event.source?.packageName // Ensure event is for the foreground app activity
                 ) {
                    // Heuristic: Activity launch often triggers WINDOW_STATE_CHANGED
                    // Check if this app is in the blocked list
                    if (cachedBlockedApps.contains(packageName)) {
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastConfirmationShownTime > CONFIRMATION_DEBOUNCE_MS) {
                            lastConfirmationShownTime = currentTime
                            Log.d(TAG, "Potential blocked app launch detected: $packageName Class: ${event.className}")
                            showConfirmation(packageName)
                        } else {
                           // Log.v(TAG, "Confirmation debounced for $packageName")
                        }
                    }
                 }
            }
        }
    }

    private fun logSwipeEvent(packageName: String, timestamp: Long) {
        serviceScope.launch {
            try {
                // Insert the raw swipe event
                usageDao.insertUsageEvent(
                    UsageEvent(
                        timestamp = timestamp, // Use event time for accuracy
                        packageName = packageName,
                        eventType = EventType.SWIPE
                    )
                )

                // Increment swipe count in daily summary immediately
                 val startOfDay = getStartOfDayMillis(timestamp)
                 var summary = usageDao.getDailySummary(startOfDay)

                 if (summary != null) {
                     summary = summary.copy(swipeCount = summary.swipeCount + 1)
                 } else {
                     // No summary yet for today, create one (fetch screen time later?)
                     // Or rely on UsageTrackingWorker to create the initial summary?
                     // Let's create a basic one here if needed, worker will update screen time.
                     summary = DailyUsageSummary(
                         dateMillis = startOfDay,
                         totalScreenTimeMillis = 0, // Worker will update this
                         swipeCount = 1,
                         unlockCount = 0 // Worker/Receiver handles unlocks
                     )
                 }
                 usageDao.insertDailySummary(summary)
                 // Log.d(TAG, "Swipe count incremented for $packageName on $startOfDay")

            } catch (e: Exception) {
                Log.e(TAG, "Error logging swipe event or updating summary", e)
            }
        }
    }

     private fun getStartOfDayMillis(timestamp: Long): Long {
         return Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }


     private fun showConfirmation(packageName: String) {
         // Get user-friendly app name
         val appName = try {
             val appInfo: ApplicationInfo = packageManager.getApplicationInfo(packageName, 0)
             packageManager.getApplicationLabel(appInfo).toString()
         } catch (e: PackageManager.NameNotFoundException) {
             Log.w(TAG, "Could not get app name for $packageName", e)
             packageName // Fallback to package name
         }
         Log.i(TAG, "Showing confirmation prompt for $appName ($packageName)")

         // Use application context to start activity from service
         val context = applicationContext
         val intent = ConfirmationActivity.newIntent(context, appName, packageName)

         // Required flags for starting activity from background/service
         intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)

         try {
             context.startActivity(intent)
             // Optional: Try to briefly interrupt the original app launch. Risky.
             // serviceScope.launch {
             //     delay(50) // Small delay to allow our activity to potentially start first
             //     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
             //        // performGlobalAction(GLOBAL_ACTION_BACK) // Requires permission & might affect user flow negatively
             //     }
             // }
         } catch (e: Exception) {
             Log.e(TAG,"Error starting ConfirmationActivity for $packageName", e)
         }
     }


    override fun onInterrupt() {
        Log.w(TAG, "Accessibility Service interrupted.")
        serviceJob.cancel() // Cancel coroutines
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.w(TAG, "Accessibility Service destroyed.")
        serviceJob.cancel() // Ensure coroutines are cancelled
    }
} 