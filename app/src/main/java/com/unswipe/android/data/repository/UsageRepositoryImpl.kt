package com.unswipe.android.data.repository

import android.app.usage.UsageStatsManager
import android.content.Context // Keep if needed for UsageStatsManager or Permissions
import com.google.firebase.firestore.FirebaseFirestore
import com.unswipe.android.data.local.dao.UsageDao // Corrected: Assuming this is the correct path
import com.unswipe.android.domain.repository.UsageRepository
import com.unswipe.android.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine // Needed for getDashboardDataFlow
import kotlinx.coroutines.flow.flow // Example for simple flow creation
import kotlinx.coroutines.flow.transformLatest // Needed for refactored getDashboardDataFlow
import java.time.LocalDate // Example for date handling
import java.util.Calendar // Example for date calculations
import javax.inject.Inject

// --- Import Aliases for Clarity ---
// Import your DOMAIN models
import com.unswipe.android.domain.model.DailyUsageSummary as DomainDailyUsageSummary
import com.unswipe.android.domain.model.TodayStats as DomainTodayStats // Renamed TodayStats in domain
import com.unswipe.android.domain.model.DashboardData
// import com.unswipe.android.domain.model.UsageEvent as DomainUsageEvent // Removed unused alias

// Import your DATA models (that DAO returns)
import com.unswipe.android.data.model.DailyUsageSummary as DataDailyUsageSummary
import com.unswipe.android.data.model.UsageEvent // Removed 'as DataUsageEvent' alias


// Assuming TodayStats is now correctly defined in domain/model:
// import com.unswipe.android.domain.model.TodayStats <- Make sure this exists and remove the ui one below
// Remove this incorrect import: import com.unswipe.android.ui.dashboard.TodayStats

class UsageRepositoryImpl @Inject constructor(
    private val usageDao: UsageDao,
    private val usageStatsManager: UsageStatsManager, // Be mindful of context/permissions needed
    private val firestore: FirebaseFirestore,
    private val settingsRepository: SettingsRepository, // Depends on another repo
    private val context: Context // Often needed for UsageStatsManager
) : UsageRepository {

    // --- Implement ALL methods defined in UsageRepository interface here ---

    // Assuming UsageRepository interface uses DataUsageEvent for now
    // If interface changes to DomainUsageEvent, map it here before calling DAO
    override suspend fun logUsageEvent(event: UsageEvent) {
        try {
            usageDao.insertUsageEvent(event) // Directly call DAO
        } catch (e: Exception) {
            // Log error (e.g., using Timber or Log.e)
            println("Error logging usage event: ${e.message}")
        }
    }

    // This is complex and combines multiple data sources
    override fun getDashboardDataFlow(): Flow<DashboardData> {
        // Combine the flows that trigger updates
        return combine(
            settingsRepository.getDailyLimitFlow(),
            settingsRepository.getStreakFlow()
            // Add other necessary flows here (e.g., premium status flow when available)
        ) { limit, streak -> // Receive the latest values from the combined flows
            // Pass the non-suspend values to a Pair or intermediate data class
            Pair(limit, streak)
        }.transformLatest { (timeLimit, currentStreak) -> // Use transformLatest for suspend operations
            // --- Perform suspend calls here ---
            val stats = getTodaysUsageStats() // Suspend call OK here
            val weekly = getWeeklyUsageSummary() // Suspend call OK here

            // Permissions / System status checks (Keep as non-suspend for now)
            val hasUsagePerms = hasUsageStatsPermission(context)
            val isAccessEnabled = isAccessibilityServiceEnabled(context)

            // --- Map to DashboardData Domain Model ---
            val dashboardData = DashboardData(
                timeUsedTodayMillis = stats.totalUsageMillis,
                timeLimitMillis = timeLimit, // From combined flow
                currentStreak = currentStreak, // From combined flow
                swipesToday = stats.swipeCount,
                unlocksToday = stats.unlockCount,
                weeklyProgress = weekly,
                isPremium = false, // Still hardcoded
                hasUsageStatsPermission = hasUsagePerms,
                isAccessibilityEnabled = isAccessEnabled
            )
            emit(dashboardData) // Emit the result
        }
        // TODO: Add error handling (.catch operator) and potentially emit Loading states
    }

    override suspend fun getTodaysSummary(): DomainDailyUsageSummary? {
        try {
            // Fetch DATA model from DAO (example query)
            val todayStart = getStartOfDayInMillis()
            val dataSummary: DataDailyUsageSummary? = usageDao.getDailySummary(todayStart) // Corrected from getSummaryForDay

            // Map DATA model to DOMAIN model (if not null)
            return dataSummary?.let { ds ->
                mapDataSummaryToDomain(ds) // Use helper function for mapping
            }
        } catch (e: Exception) {
            println("Error getting today's summary: ${e.message}")
            return null
        }
    }

    override suspend fun syncUsageToCloud() {
        // TODO: Implement Firestore sync logic
        // 1. Fetch data to sync (e.g., last N days summaries) from DAO
        // 2. Get user ID (e.g., from AuthRepository or Firebase Auth)
        // 3. Format data for Firestore
        // 4. Write to Firestore using firestore.collection(...).document(...).set(...)
        // 5. Add error handling
        println("TODO: Implement syncUsageToCloud")
    }

    override suspend fun clearOldData(olderThanTimestamp: Long) {
        try {
            usageDao.deleteOldUsageEvents(olderThanTimestamp) // Corrected from deleteEventsOlderThan
            usageDao.deleteOldSummaries(olderThanTimestamp) // Corrected from deleteSummariesOlderThan
        } catch (e: Exception) {
            println("Error clearing old data: ${e.message}")
        }
    }

    // Make sure the interface returns the DOMAIN TodayStats
    override suspend fun getTodaysUsageStats(): DomainTodayStats {
        // TODO: Implement logic to calculate TodayStats
        // This might involve:
        // 1. Querying UsageStatsManager for app usage time today. Requires permission check.
        // 2. Querying UsageDao for swipe/unlock events for today.
        // 3. Combining the results.

        // Placeholder implementation:
        val todayStart = getStartOfDayInMillis()
        val swipes = usageDao.getEventCountSince(todayStart, "SWIPE") // Example DAO query
        val unlocks = usageDao.getEventCountSince(todayStart, "SCREEN_UNLOCK") // Example DAO query
        val usageTime = getUsageTimeFromManager(context, todayStart) // Example helper

        return DomainTodayStats(
            totalUsageMillis = usageTime,
            swipeCount = swipes,
            unlockCount = unlocks
            // Add other relevant stats fields as defined in DomainTodayStats
        )
    }

    override suspend fun getCurrentStreak(): Int {
        // TODO: Implement streak calculation logic
        // Could depend on SettingsRepository or calculated from daily summaries in DAO
        // Example: Fetch last N summaries, check consecutive days meeting criteria
        return settingsRepository.getCurrentStreak() // Example: Delegating to SettingsRepository
        // OR calculate based on usageDao.getRecentSummaries(...)
    }

    // Make sure the interface returns List<DomainDailyUsageSummary>
    override suspend fun getWeeklyUsageSummary(): List<DomainDailyUsageSummary> {
        try {
            // Fetch LIST of DATA models from DAO (example query)
            val weekStart = getStartOfWeekInMillis() // Or last 7 days
            val dataSummaries: List<DataDailyUsageSummary> = usageDao.getSummariesSince(weekStart) // Adjust DAO query

            // MAP the list of DATA models to a list of DOMAIN models
            return dataSummaries.map { dataSummary ->
                mapDataSummaryToDomain(dataSummary) // Use helper function
            }
        } catch (e: Exception) {
            println("Error getting weekly summary: ${e.message}")
            return emptyList() // Return empty list on error
        }
    }

    // --- Helper Functions (Private) ---

    private fun mapDataSummaryToDomain(dataSummary: DataDailyUsageSummary): DomainDailyUsageSummary {
        // Perform the mapping logic based on your class properties
        return DomainDailyUsageSummary(
            // Example: Map properties assuming they have similar names/types
            // If using LocalDate in Domain, convert Long from Data
            date = LocalDate.ofEpochDay(dataSummary.dateMillis / (24*60*60*1000)), // Corrected to use dateMillis
            totalUsageMillis = dataSummary.totalScreenTimeMillis, // Corrected to use totalScreenTimeMillis
            swipeCount = dataSummary.swipeCount ?: 0, // Handle nulls if needed
            unlockCount = dataSummary.unlockCount ?: 0
        )
    }

    // Example helpers (Implement these based on your needs)
    private fun getStartOfDayInMillis(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun getStartOfWeekInMillis(): Long {
        return Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY // Or Sunday depending on locale/preference
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

import android.app.AppOpsManager
import android.os.Process
import android.provider.Settings
import android.text.TextUtils
import com.unswipe.android.data.services.SwipeAccessibilityService

    private fun hasUsageStatsPermission(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun isAccessibilityServiceEnabled(context: Context): Boolean {
        val expectedServiceName = ComponentName(context, SwipeAccessibilityService::class.java).flattenToString()
        val enabledServicesSetting = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        if (enabledServicesSetting == null || TextUtils.isEmpty(enabledServicesSetting)) {
            return false
        }
        val colonSplitter = TextUtils.SimpleStringSplitter(':')
        colonSplitter.setString(enabledServicesSetting)
        while (colonSplitter.hasNext()) {
            val componentNameString = colonSplitter.next()
            if (componentNameString.equals(expectedServiceName, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    private fun getUsageTimeFromManager(context: Context, startTime: Long): Long {
        if (!hasUsageStatsPermission(context)) {
            return 0L // Return 0 if permission is not granted
        }
        val endTime = System.currentTimeMillis()
        var totalUsageTime = 0L
        try {
            val stats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY, // Interval doesn't matter as much as start/end for specific app
                startTime,
                endTime
            )
            if (stats != null) {
                for (usageStats in stats) {
                    // Accumulate time for the app's own package
                    if (usageStats.packageName == context.packageName) {
                        totalUsageTime += usageStats.totalTimeInForeground
                    }
                    // Note: If you need to sum usage for OTHER apps, you'd iterate and sum here.
                    // For Unswipe, we are primarily interested in its own usage or specific target apps.
                    // The current subtask implies getting *the app's* usage time,
                    // which might be for features like "how much time you spent in Unswipe itself".
                    // If the goal was "total phone usage", this query would need to be broader
                    // or sum all packages. Given the name "getUsageTimeFromManager" and its usage
                    // within `getTodaysUsageStats` (which seems to be about Unswipe's own stats),
                    // this implementation focuses on the app's own foreground time.
                }
            }
        } catch (e: Exception) {
            // Log error or handle appropriately
            println("Error querying usage stats: ${e.message}")
            return 0L // Return 0 on error
        }
        return totalUsageTime
    }
}