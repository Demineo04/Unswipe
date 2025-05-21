package com.unswipe.android.data.repository

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.provider.Settings
import app.cash.turbine.test
import com.google.firebase.firestore.FirebaseFirestore
import com.unswipe.android.data.local.dao.UsageDao
import com.unswipe.android.data.model.DailyUsageSummary as DataDailyUsageSummary
import com.unswipe.android.data.model.EventType
import com.unswipe.android.data.model.UsageEvent
import com.unswipe.android.domain.model.DailyUsageSummary as DomainDailyUsageSummary
import com.unswipe.android.domain.model.TodayStats as DomainTodayStats
import com.unswipe.android.domain.repository.SettingsRepository
import com.unswipe.android.data.services.SwipeAccessibilityService // Required for isAccessibilityServiceEnabled
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import java.util.Calendar
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
@ExtendWith(MainDispatcherExtension::class) // Using the same extension as ViewModelTest
class UsageRepositoryImplTest {

    private lateinit var repository: UsageRepositoryImpl
    private lateinit var usageDao: UsageDao
    private lateinit var usageStatsManager: UsageStatsManager
    private lateinit var firestore: FirebaseFirestore
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var context: Context
    private lateinit var appOpsManager: AppOpsManager
    private lateinit var packageManager: PackageManager


    // Test dispatcher for coroutines
    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        usageDao = mockk(relaxed = true) // relaxed to allow skipping DAO calls not under test
        usageStatsManager = mockk(relaxed = true)
        firestore = mockk(relaxed = true)
        settingsRepository = mockk(relaxed = true)
        context = mockk(relaxed = true)
        appOpsManager = mockk(relaxed = true)
        packageManager = mockk(relaxed = true)


        // Mock context.getSystemService behavior
        every { context.getSystemService(Context.APP_OPS_SERVICE) } returns appOpsManager
        every { context.getSystemService(Context.USAGE_STATS_SERVICE) } returns usageStatsManager // Though UsageStatsManager is also directly injected
        every { context.packageManager } returns packageManager
        every { context.packageName } returns "com.unswipe.android"
        every { context.contentResolver } returns mockk(relaxed = true) // For Settings.Secure

        // Default mock for permission checks (can be overridden in specific tests)
        every { appOpsManager.checkOpNoThrow(any(), any(), any()) } returns AppOpsManager.MODE_ALLOWED
        every { Settings.Secure.getString(any(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES) } returns
                "com.example.other/com.example.other.Service:${context.packageName}/${SwipeAccessibilityService::class.java.name}"


        repository = UsageRepositoryImpl(
            usageDao,
            usageStatsManager,
            firestore,
            settingsRepository,
            context
        )
    }

    @Test
    fun `logUsageEvent inserts event into DAO`() = runTest {
        val event = UsageEvent(System.currentTimeMillis(), "com.test.app", EventType.SWIPE.name)
        coEvery { usageDao.insertUsageEvent(event) } just runs

        repository.logUsageEvent(event)

        coVerify { usageDao.insertUsageEvent(event) }
    }

    @Test
    fun `getTodaysSummary fetches and maps data from DAO`() = runTest {
        val todayMillis = repository.getStartOfDayInMillis()
        val dataSummary = DataDailyUsageSummary(
            dateMillis = todayMillis,
            totalScreenTimeMillis = TimeUnit.HOURS.toMillis(2),
            swipeCount = 100,
            unlockCount = 10
        )
        coEvery { usageDao.getDailySummary(todayMillis) } returns dataSummary

        val result = repository.getTodaysSummary()

        assertNotNull(result)
        assertEquals(LocalDate.ofEpochDay(todayMillis / (24 * 60 * 60 * 1000)), result!!.date)
        assertEquals(TimeUnit.HOURS.toMillis(2), result.totalUsageMillis)
        assertEquals(100, result.swipeCount)
        assertEquals(10, result.unlockCount)
    }

    @Test
    fun `getTodaysSummary returns null if DAO returns null`() = runTest {
        val todayMillis = repository.getStartOfDayInMillis()
        coEvery { usageDao.getDailySummary(todayMillis) } returns null
        val result = repository.getTodaysSummary()
        assertNull(result)
    }
    
    @Test
    fun `clearOldData calls DAO methods`() = runTest {
        val timestamp = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7)
        coEvery { usageDao.deleteOldUsageEvents(timestamp) } just runs
        coEvery { usageDao.deleteOldSummaries(timestamp) } just runs

        repository.clearOldData(timestamp)

        coVerifyAll {
            usageDao.deleteOldUsageEvents(timestamp)
            usageDao.deleteOldSummaries(timestamp)
        }
    }

    @Test
    fun `getTodaysUsageStats combines DAO data and usage manager time`() = runTest {
        val todayStartMillis = repository.getStartOfDayInMillis()
        val mockSwipes = 50
        val mockUnlocks = 5
        val mockUsageTime = TimeUnit.MINUTES.toMillis(90)

        coEvery { usageDao.getEventCountSince(todayStartMillis, "SWIPE") } returns mockSwipes
        coEvery { usageDao.getEventCountSince(todayStartMillis, "SCREEN_UNLOCK") } returns mockUnlocks

        // Mocking for getUsageTimeFromManager's internal workings
        // Assuming permission is granted (default mock setup)
        val usageStatsList = listOf(
            mockk<android.app.usage.UsageStats> {
                every { packageName } returns context.packageName
                every { totalTimeInForeground } returns mockUsageTime
            },
            mockk<android.app.usage.UsageStats> { // Another app, should be ignored
                every { packageName } returns "com.other.app"
                every { totalTimeInForeground } returns TimeUnit.HOURS.toMillis(1)
            }
        )
        every { usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, todayStartMillis, any()) } returns usageStatsList


        val result = repository.getTodaysUsageStats()

        assertEquals(mockUsageTime, result.totalUsageMillis)
        assertEquals(mockSwipes, result.swipeCount)
        assertEquals(mockUnlocks, result.unlockCount)
    }
    
    @Test
    fun `getTodaysUsageStats returns 0 usage time if permission denied`() = runTest {
        val todayStartMillis = repository.getStartOfDayInMillis()
        // Deny permission
        every { appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, any(), context.packageName) } returns AppOpsManager.MODE_ERRORED

        val result = repository.getTodaysUsageStats()
        
        assertEquals(0L, result.totalUsageMillis)
        // Swipes and unlocks should still be fetched from DAO
        coVerify { usageDao.getEventCountSince(todayStartMillis, "SWIPE") }
        coVerify { usageDao.getEventCountSince(todayStartMillis, "SCREEN_UNLOCK") }
    }


    @Test
    fun `getWeeklyUsageSummary fetches and maps list from DAO`() = runTest {
        val startOfWeekMillis = repository.getStartOfWeekInMillis()
        val dataSummaries = listOf(
            DataDailyUsageSummary(startOfWeekMillis, TimeUnit.HOURS.toMillis(1), 50, 5),
            DataDailyUsageSummary(startOfWeekMillis + TimeUnit.DAYS.toMillis(1), TimeUnit.HOURS.toMillis(2), 100, 10)
        )
        coEvery { usageDao.getSummariesSince(startOfWeekMillis) } returns dataSummaries

        val result = repository.getWeeklyUsageSummary()

        assertEquals(2, result.size)
        assertEquals(LocalDate.ofEpochDay(startOfWeekMillis / (24*60*60*1000)), result[0].date)
        assertEquals(TimeUnit.HOURS.toMillis(1), result[0].totalUsageMillis)
        assertEquals(LocalDate.ofEpochDay((startOfWeekMillis + TimeUnit.DAYS.toMillis(1)) / (24*60*60*1000)), result[1].date)
        assertEquals(TimeUnit.HOURS.toMillis(2), result[1].totalUsageMillis)
    }
    
    @Nested
    inner class GetDashboardDataFlowTests {
        @Test
        fun `getDashboardDataFlow combines data sources correctly`() = runTest(testDispatcher.scheduler) {
            val dailyLimitFlow = flowOf(TimeUnit.HOURS.toMillis(3))
            val streakFlow = flowOf(7)
            val todayStartMillis = repository.getStartOfDayInMillis()

            val mockTodayStats = DomainTodayStats(
                totalUsageMillis = TimeUnit.HOURS.toMillis(1),
                swipeCount = 10,
                unlockCount = 2
            )
            val mockWeeklySummary = listOf(
                DomainDailyUsageSummary(LocalDate.now(), TimeUnit.MINUTES.toMillis(60), 10, 2)
            )

            every { settingsRepository.getDailyLimitFlow() } returns dailyLimitFlow
            every { settingsRepository.getStreakFlow() } returns streakFlow
            
            // Use coEvery for suspend functions
            coEvery { usageDao.getEventCountSince(todayStartMillis, "SWIPE") } returns mockTodayStats.swipeCount
            coEvery { usageDao.getEventCountSince(todayStartMillis, "SCREEN_UNLOCK") } returns mockTodayStats.unlockCount
            
            // Mocking for getUsageTimeFromManager's internal workings for getTodaysUsageStats
             val usageStatsList = listOf(
                mockk<android.app.usage.UsageStats> {
                    every { packageName } returns context.packageName
                    every { totalTimeInForeground } returns mockTodayStats.totalUsageMillis
                }
            )
            every { usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, todayStartMillis, any()) } returns usageStatsList


            coEvery { usageDao.getSummariesSince(any()) } returns mockWeeklySummary.map {
                // Map Domain back to Data for DAO mock
                DataDailyUsageSummary(it.date.toEpochDay() * 24 * 60 * 60 * 1000, it.totalUsageMillis, it.swipeCount, it.unlockCount)
            }
            
            // Grant permissions by default for this test
            every { appOpsManager.checkOpNoThrow(any(), any(), any()) } returns AppOpsManager.MODE_ALLOWED
            every { Settings.Secure.getString(any(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES) } returns
                "${context.packageName}/${SwipeAccessibilityService::class.java.name}"


            repository.getDashboardDataFlow().test {
                val dashboardData = awaitItem()
                assertNotNull(dashboardData)
                assertEquals(TimeUnit.HOURS.toMillis(1), dashboardData.timeUsedTodayMillis)
                assertEquals(TimeUnit.HOURS.toMillis(3), dashboardData.timeLimitMillis)
                assertEquals(7, dashboardData.currentStreak)
                assertEquals(10, dashboardData.swipesToday)
                assertEquals(2, dashboardData.unlocksToday)
                assertEquals(1, dashboardData.weeklyProgress.size)
                assertEquals(mockWeeklySummary[0].totalUsageMillis, dashboardData.weeklyProgress[0].totalUsageMillis)
                assertFalse(dashboardData.isPremium) // Default
                assertFalse(dashboardData.hasUsageStatsPermission) // This is because the private method is called with real context
                                                                // but the mock for checkOpNoThrow should make it true. Let's verify.
                                                                // Ah, hasUsageStatsPermission and isAccessibilityServiceEnabled are called with the class's context.
                                                                // The mocks set up in BeforeEach should cover this.
                assertTrue(dashboardData.hasUsageStatsPermission)
                assertTrue(dashboardData.isAccessibilityEnabled)

                cancelAndConsumeRemainingEvents()
            }
        }
    }
    
    // Tests for private helper methods (hasUsageStatsPermission, isAccessibilityServiceEnabled)
    // These are more like integration tests for these specific private methods if we don't make them internal/public
    // Or we trust their implementation from previous steps and test them via public API effects as done in GetDashboardDataFlowTests.
    
    @Nested
    inner class PermissionHelperTests {
        @Test
        fun `hasUsageStatsPermission returns true when permission granted`() {
            every { appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, any(), context.packageName) } returns AppOpsManager.MODE_ALLOWED
            assertTrue(repository.hasUsageStatsPermissionReflect(context)) // Using reflection to test private method
        }

        @Test
        fun `hasUsageStatsPermission returns false when permission denied`() {
            every { appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, any(), context.packageName) } returns AppOpsManager.MODE_ERRORED
            assertFalse(repository.hasUsageStatsPermissionReflect(context))
        }

        @Test
        fun `isAccessibilityServiceEnabled returns true when service is enabled`() {
            val serviceName = "${context.packageName}/${SwipeAccessibilityService::class.java.name}"
            every { Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES) } returns serviceName
            assertTrue(repository.isAccessibilityServiceEnabledReflect(context))
        }
        
        @Test
        fun `isAccessibilityServiceEnabled returns false when service is not in setting string`() {
            every { Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES) } returns "com.other.service/com.other.Service"
            assertFalse(repository.isAccessibilityServiceEnabledReflect(context))
        }

        @Test
        fun `isAccessibilityServiceEnabled returns false when setting string is null or empty`() {
            every { Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES) } returns null
            assertFalse(repository.isAccessibilityServiceEnabledReflect(context))
            
            every { Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES) } returns ""
            assertFalse(repository.isAccessibilityServiceEnabledReflect(context))
        }
    }


    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
        clearAllMocks() // Clear MockK mocks
    }
}

// Reflection helpers to test private methods (use with caution, prefer testing via public API)
fun UsageRepositoryImpl.hasUsageStatsPermissionReflect(context: Context): Boolean {
    val method = this::class.java.getDeclaredMethod("hasUsageStatsPermission", Context::class.java)
    method.isAccessible = true
    return method.invoke(this, context) as Boolean
}

fun UsageRepositoryImpl.isAccessibilityServiceEnabledReflect(context: Context): Boolean {
    val method = this::class.java.getDeclaredMethod("isAccessibilityServiceEnabled", Context::class.java)
    method.isAccessible = true
    return method.invoke(this, context) as Boolean
}

// Using the same JUnit 5 extension from DashboardViewModelTest for consistency
// @ExperimentalCoroutinesApi
// class MainDispatcherExtension(
//     private val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
// ) : BeforeEachCallback, AfterEachCallback, TestExecutionExceptionHandler {
// ... (already defined in DashboardViewModelTest.kt, so no need to redefine if in same module/classpath for tests)
// }

// Helper to get private field or method for testing (if needed, use with caution)
// fun <T> Any.getPrivateField(name: String): T {
//     val field = this::class.java.getDeclaredField(name)
//     field.isAccessible = true
//     @Suppress("UNCHECKED_CAST")
//     return field.get(this) as T
// }

// Private helper in UsageRepositoryImpl
private fun UsageRepositoryImpl.getStartOfDayInMillis(): Long {
    return Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

private fun UsageRepositoryImpl.getStartOfWeekInMillis(): Long {
    return Calendar.getInstance().apply {
        firstDayOfWeek = Calendar.MONDAY 
        set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}
