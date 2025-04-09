package com.unswipe.android.data.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.unswipe.android.data.local.dao.UsageDao
import com.unswipe.android.data.model.DailyUsageSummary
import com.unswipe.android.data.model.EventType
import com.unswipe.android.data.model.UsageEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class UnlockReceiver : BroadcastReceiver() {

    @Inject lateinit var usageDao: UsageDao

    private val receiverJob = SupervisorJob()
    private val receiverScope = CoroutineScope(Dispatchers.IO + receiverJob) // Use IO for DB ops

    companion object {
        private const val TAG = "UnlockReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        // Check for null context for safety, though unlikely for manifest receiver
        if (context == null || intent == null) return

        val action = intent.action
        val currentTime = System.currentTimeMillis()

        if (Intent.ACTION_USER_PRESENT == action) {
            Log.d(TAG, "Device unlocked (user present).")
            // Use a suspend function for DB operations
            goAsync().launch(receiverScope.coroutineContext) { // Use goAsync and scope
                 logUnlockEventAndUpdateSummary(currentTime)
            }
        } else if (Intent.ACTION_BOOT_COMPLETED == action) {
            Log.d(TAG, "Device boot completed. Rescheduling work if needed.")
            // Optional: Enqueue UsageTrackingWorker explicitly here if needed,
            // but WorkManager's periodic requests with KEEP policy usually handle this.
            // Example: Application class setup might be sufficient.
        }
    }

    private suspend fun logUnlockEventAndUpdateSummary(timestamp: Long) {
         try {
             // Log the raw unlock event
             usageDao.insertUsageEvent(
                 UsageEvent(
                     timestamp = timestamp,
                     packageName = "android", // System event
                     eventType = EventType.SCREEN_UNLOCK
                 )
             )

             // Increment unlock count in daily summary immediately
             val startOfDay = getStartOfDayMillis(timestamp)
             var summary = usageDao.getDailySummary(startOfDay)

             if (summary != null) {
                 summary = summary.copy(unlockCount = summary.unlockCount + 1)
             } else {
                  // Create a basic summary if none exists for today
                 summary = DailyUsageSummary(
                     dateMillis = startOfDay,
                     totalScreenTimeMillis = 0, // Worker updates this
                     swipeCount = 0, // Accessibility updates this
                     unlockCount = 1
                 )
             }
             usageDao.insertDailySummary(summary)
            // Log.d(TAG, "Unlock count incremented for $startOfDay")

         } catch (e: Exception) {
              Log.e(TAG, "Error logging unlock event or updating summary", e)
         } finally {
            // Finish the async task for goAsync()
            // goAsyncResult.finish() // No longer needed with launch extension
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

    // Helper extension for goAsync with CoroutineScope
    private fun BroadcastReceiver.PendingResult.launch(scope: CoroutineContext, block: suspend () -> Unit) {
        scope.launch {
            try {
                block()
            } finally {
                // Always finish the broadcast receiver operation.
                finish()
            }
        }
    }
} 