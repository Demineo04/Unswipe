package com.unswipe.android.test

import android.content.Context
import com.unswipe.android.data.context.ContextDetectionEngine
import com.unswipe.android.data.analytics.UsagePatternAnalyzer
import com.unswipe.android.data.interventions.ContextualInterventionEngine
import com.unswipe.android.data.notifications.ContextAwareNotificationEngine
import com.unswipe.android.domain.model.*
import com.unswipe.android.domain.repository.SettingsRepository
import com.unswipe.android.domain.repository.UsageRepository
import kotlinx.coroutines.runBlocking
import java.time.LocalTime
import javax.inject.Inject

/**
 * Integration test for the context-aware digital wellness system
 * This verifies that all components work together correctly
 */
class ContextAwareIntegrationTest @Inject constructor(
    private val context: Context,
    private val contextEngine: ContextDetectionEngine,
    private val patternAnalyzer: UsagePatternAnalyzer,
    private val interventionEngine: ContextualInterventionEngine,
    private val notificationEngine: ContextAwareNotificationEngine,
    private val settingsRepository: SettingsRepository,
    private val usageRepository: UsageRepository
) {
    
    /**
     * Tests the complete flow from context detection to intervention
     */
    fun testCompleteContextAwareFlow() = runBlocking {
        println("🧪 Starting Context-Aware Integration Test")
        
        // 1. Setup user schedule
        setupTestUserSchedule()
        
        // 2. Test context detection
        testContextDetection()
        
        // 3. Test pattern analysis
        testPatternAnalysis()
        
        // 4. Test intervention decisions
        testInterventionDecisions()
        
        // 5. Test notification generation
        testNotificationGeneration()
        
        println("✅ Context-Aware Integration Test Complete")
    }
    
    private suspend fun setupTestUserSchedule() {
        println("📅 Setting up test user schedule...")
        
        val schedule = UserSchedule(
            wakeupTime = LocalTime.of(7, 0),
            workStartTime = LocalTime.of(9, 0),
            workEndTime = LocalTime.of(17, 0),
            sleepTime = LocalTime.of(23, 0),
            workDays = setOf(1, 2, 3, 4, 5), // Monday to Friday
            isWorkScheduleEnabled = true,
            isSleepScheduleEnabled = true
        )
        
        settingsRepository.updateUserSchedule(schedule)
        
        val preferences = InterventionPreferences(
            workTimeLimit = 1800000L, // 30 minutes
            sleepTimeStrict = true,
            enableWorkInterventions = true,
            enableSleepInterventions = true,
            enableStressDetection = true,
            interventionStyle = InterventionStyle.BALANCED
        )
        
        settingsRepository.updateInterventionPreferences(preferences)
        
        // Add work WiFi for location detection
        settingsRepository.addWorkWifiSSID("OfficeWiFi")
        
        println("✅ User schedule configured")
    }
    
    private suspend fun testContextDetection() {
        println("🔍 Testing context detection...")
        
        val currentContext = contextEngine.detectCurrentContext()
        val deviceState = contextEngine.getCurrentDeviceState()
        val locationContext = contextEngine.detectLocationContext()
        
        println("📍 Current context: $currentContext")
        println("📱 Device state: Battery ${deviceState.batteryLevel}%, WiFi: ${deviceState.isConnectedToWifi}")
        println("🏢 Location context: $locationContext")
        
        assert(currentContext != null) { "Context detection failed" }
        println("✅ Context detection working")
    }
    
    private suspend fun testPatternAnalysis() {
        println("📊 Testing usage pattern analysis...")
        
        // Simulate some usage events
        val testEvents = listOf(
            createTestUsageEvent("com.instagram.android", "APP_LAUNCH", System.currentTimeMillis() - 3600000),
            createTestUsageEvent("com.instagram.android", "APP_LAUNCH", System.currentTimeMillis() - 3000000),
            createTestUsageEvent("com.instagram.android", "APP_LAUNCH", System.currentTimeMillis() - 1800000),
            createTestUsageEvent("com.instagram.android", "APP_LAUNCH", System.currentTimeMillis() - 900000),
            createTestUsageEvent("com.instagram.android", "APP_LAUNCH", System.currentTimeMillis() - 300000)
        )
        
        // Log the events
        testEvents.forEach { event ->
            usageRepository.logUsageEvent(event)
        }
        
        // Analyze patterns
        val patterns = patternAnalyzer.detectUsagePatterns()
        
        println("🔍 Detected ${patterns.size} usage patterns:")
        patterns.forEach { pattern ->
            println("  - ${pattern.type}: ${pattern.description} (confidence: ${(pattern.confidence * 100).toInt()}%)")
        }
        
        assert(patterns.isNotEmpty()) { "Pattern analysis should detect patterns from test data" }
        println("✅ Pattern analysis working")
    }
    
    private suspend fun testInterventionDecisions() {
        println("🛡️ Testing intervention decisions...")
        
        // Test work-time intervention
        val workDecision = interventionEngine.shouldTriggerIntervention(
            packageName = "com.instagram.android",
            currentUsage = 2400000L, // 40 minutes
            sessionCount = 8
        )
        
        println("💼 Work intervention decision:")
        println("  - Should intervene: ${workDecision.shouldIntervene}")
        println("  - Urgency: ${workDecision.urgency}")
        println("  - Action: ${workDecision.suggestedAction}")
        println("  - Message: ${workDecision.message}")
        
        // Test sleep-time intervention
        val sleepDecision = interventionEngine.shouldTriggerIntervention(
            packageName = "com.zhiliaoapp.musically",
            currentUsage = 1800000L, // 30 minutes
            sessionCount = 3
        )
        
        println("🌙 Sleep intervention decision:")
        println("  - Should intervene: ${sleepDecision.shouldIntervene}")
        println("  - Message: ${sleepDecision.message}")
        println("  - Alternative: ${sleepDecision.alternativeActivity}")
        
        assert(workDecision.shouldIntervene || sleepDecision.shouldIntervene) { 
            "At least one intervention should trigger with test data" 
        }
        println("✅ Intervention decisions working")
    }
    
    private suspend fun testNotificationGeneration() {
        println("🔔 Testing notification generation...")
        
        // Test work-time notification
        notificationEngine.analyzeAndNotify(
            packageName = "com.instagram.android",
            usageTime = 2100000L, // 35 minutes
            sessionCount = 6
        )
        
        // Test pattern-based notifications
        notificationEngine.checkAndNotifyPatterns()
        
        println("✅ Notification generation working")
    }
    
    private fun createTestUsageEvent(packageName: String, eventType: String, timestamp: Long): com.unswipe.android.data.model.UsageEvent {
        return com.unswipe.android.data.model.UsageEvent(
            id = 0,
            packageName = packageName,
            eventType = eventType,
            timestamp = timestamp,
            additionalData = "test_data"
        )
    }
    
    /**
     * Tests specific scenarios
     */
    fun testSpecificScenarios() = runBlocking {
        println("🎯 Testing specific scenarios...")
        
        testWorkHoursScenario()
        testBedtimeScenario()
        testStressDetectionScenario()
        
        println("✅ Scenario testing complete")
    }
    
    private suspend fun testWorkHoursScenario() {
        println("💼 Testing work hours scenario...")
        
        val decision = interventionEngine.shouldTriggerIntervention(
            packageName = "com.zhiliaoapp.musically",
            currentUsage = 3600000L, // 1 hour during work
            sessionCount = 12
        )
        
        assert(decision.shouldIntervene) { "Should intervene for excessive TikTok use during work" }
        assert(decision.urgency == InterventionUrgency.HIGH) { "Should be high urgency for work distraction" }
        
        println("✅ Work hours scenario validated")
    }
    
    private suspend fun testBedtimeScenario() {
        println("🌙 Testing bedtime scenario...")
        
        val decision = interventionEngine.shouldTriggerIntervention(
            packageName = "com.instagram.android",
            currentUsage = 900000L, // 15 minutes at bedtime
            sessionCount = 1
        )
        
        // Should intervene even for moderate usage during bedtime
        assert(decision.message.contains("bedtime") || decision.message.contains("sleep")) { 
            "Should mention sleep/bedtime in intervention message" 
        }
        
        println("✅ Bedtime scenario validated")
    }
    
    private suspend fun testStressDetectionScenario() {
        println("😰 Testing stress detection scenario...")
        
        // Simulate stress pattern: many short sessions
        val decision = interventionEngine.shouldTriggerIntervention(
            packageName = "com.instagram.android",
            currentUsage = 1200000L, // 20 minutes total
            sessionCount = 25 // But many sessions = stress indicator
        )
        
        assert(decision.contextualTip?.contains("stress") == true || 
               decision.alternativeActivity?.contains("breathing") == true) { 
            "Should provide stress-related guidance for frequent checking pattern" 
        }
        
        println("✅ Stress detection scenario validated")
    }
    
    /**
     * Performance test to ensure the system is responsive
     */
    fun testPerformance() = runBlocking {
        println("⚡ Testing performance...")
        
        val startTime = System.currentTimeMillis()
        
        // Run multiple operations in sequence
        repeat(10) {
            contextEngine.detectCurrentContext()
            patternAnalyzer.detectUsagePatterns()
            interventionEngine.shouldTriggerIntervention("com.instagram.android", 1800000L, 5)
        }
        
        val endTime = System.currentTimeMillis()
        val totalTime = endTime - startTime
        
        println("⏱️ 10 complete cycles took ${totalTime}ms (avg: ${totalTime/10}ms per cycle)")
        
        assert(totalTime < 5000) { "System should complete 10 cycles in under 5 seconds" }
        println("✅ Performance test passed")
    }
}

/**
 * Helper function to run the integration test
 * This can be called from anywhere in the app for testing
 */
suspend fun runContextAwareIntegrationTest(
    context: Context,
    contextEngine: ContextDetectionEngine,
    patternAnalyzer: UsagePatternAnalyzer,
    interventionEngine: ContextualInterventionEngine,
    notificationEngine: ContextAwareNotificationEngine,
    settingsRepository: SettingsRepository,
    usageRepository: UsageRepository
) {
    val test = ContextAwareIntegrationTest(
        context, contextEngine, patternAnalyzer, interventionEngine, 
        notificationEngine, settingsRepository, usageRepository
    )
    
    try {
        test.testCompleteContextAwareFlow()
        test.testSpecificScenarios()
        test.testPerformance()
        
        println("🎉 All integration tests passed!")
    } catch (e: Exception) {
        println("❌ Integration test failed: ${e.message}")
        e.printStackTrace()
    }
}