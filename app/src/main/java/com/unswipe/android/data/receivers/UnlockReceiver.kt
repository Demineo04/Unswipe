package com.unswipe.android.data.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.unswipe.android.data.local.dao.UsageDao
import com.unswipe.android.data.model.EventType
import com.unswipe.android.data.model.UsageEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class UnlockReceiver : BroadcastReceiver() {

    @Inject lateinit var usageDao: UsageDao

    // Define a scope for background work initiated by this receiver
    // Using SupervisorJob so failure in one job doesn't cancel others if needed
    // Using Dispatchers.IO for database operations
    private val receiverJob = SupervisorJob()
    private val receiverScope = CoroutineScope(Dispatchers.IO + receiverJob)

    companion object {
        private const val TAG = "UnlockReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        // Check if the action is the one we are interested in
        if (intent?.action == Intent.ACTION_USER_PRESENT) {
            Log.d(TAG, "Device unlocked (user present).")

            // Tell the system we need more time to process in the background
            val pendingResult: PendingResult = goAsync() // <-- Get PendingResult

            // Launch the coroutine on the receiver's dedicated scope
            receiverScope.launch { // <-- Launch on receiverScope, NOT pendingResult
                try {
                    // Perform the suspending database operation
                    usageDao.insertUsageEvent(
                        UsageEvent(
                            timestamp = System.currentTimeMillis(),
                            packageName = "android", // System event
                            eventType = EventType.SCREEN_UNLOCK
                        )
                    )
                    Log.d(TAG, "Unlock event logged to DB.")
                } catch (e: Exception) {
                    Log.e(TAG, "Error logging unlock event", e)
                } finally {
                    // ALWAYS finish the pending result, even if an error occurred
                    Log.d(TAG, "Finishing PendingResult.")
                    pendingResult.finish() // <-- Signal background work is done
                }
            }
        } else if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "Device booted.")
            // Optional: Reschedule WorkManager jobs or other setup if needed on boot
        }
    }
    // Note: For manifest-declared receivers, cancelling the job might not be strictly necessary
    // as the process hosting the receiver might be killed after onReceive returns anyway,
    // but it's good practice if the receiver could be registered dynamically.
    // fun cancelScope() { receiverJob.cancel() }
}