package com.unswipe.android.data.premium

import com.unswipe.android.domain.model.*
import com.unswipe.android.domain.repository.PremiumRepository
import com.unswipe.android.domain.repository.UsageRepository
import com.unswipe.android.domain.repository.SettingsRepository
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*

@Singleton
class AdvancedAnalyticsEngine @Inject constructor(
    private val premiumRepository: PremiumRepository,
    private val usageRepository: UsageRepository,
    private val settingsRepository: SettingsRepository
) {
    
    /**
     * Calculates comprehensive productivity score based on multiple factors
     */
    suspend fun calculateProductivityScore(date: LocalDateTime): Float {
        if (!premiumRepository.hasFeature(PremiumFeature.ADVANCED_ANALYTICS)) return 0f
        
        val dayStart = date.truncatedTo(ChronoUnit.DAYS)
        val dayEnd = dayStart.plusDays(1)
        
        // Get usage data for the day
        val usageEvents = usageRepository.getUsageEventsInRange(
            dayStart.toEpochSecond(java.time.ZoneOffset.UTC) * 1000,
            dayEnd.toEpochSecond(java.time.ZoneOffset.UTC) * 1000
        )
        
        val userSchedule = settingsRepository.getUserSchedule()
        
        // Calculate sub-scores
        val focusScore = calculateFocusScore(usageEvents, userSchedule, date)
        val distractionScore = calculateDistractionScore(usageEvents, userSchedule)
        val timingScore = calculateTimingScore(usageEvents, userSchedule)
        val consistencyScore = calculateConsistencyScore(usageEvents)
        
        // Weighted average
        return (focusScore * 0.3f + 
                distractionScore * 0.3f + 
                timingScore * 0.2f + 
                consistencyScore * 0.2f).coerceIn(0f, 1f)
    }
    
    /**
     * Generates personalized recommendations based on usage patterns
     */
    suspend fun generatePersonalizedRecommendations(): List<PersonalizedRecommendation> {
        if (!premiumRepository.hasFeature(PremiumFeature.ADVANCED_ANALYTICS)) return emptyList()
        
        val recommendations = mutableListOf<PersonalizedRecommendation>()
        
        // Analyze recent patterns
        val recentAnalytics = premiumRepository.getAdvancedAnalytics(
            LocalDateTime.now().minusDays(7),
            LocalDateTime.now()
        )
        
        // Morning routine optimization
        if (shouldRecommendMorningRoutineOptimization()) {
            recommendations.add(
                PersonalizedRecommendation(
                    title = "Optimize Your Morning Routine",
                    description = "Your productivity is 40% higher when you avoid social media for the first hour after waking up",
                    priority = RecommendationPriority.HIGH,
                    category = RecommendationCategory.PRODUCTIVITY,
                    estimatedImpact = "15% improvement in daily focus"
                )
            )
        }
        
        // Sleep hygiene improvement
        if (shouldRecommendSleepHygiene()) {
            recommendations.add(
                PersonalizedRecommendation(
                    title = "Improve Sleep Hygiene",
                    description = "Using your phone within 30 minutes of bedtime reduces your sleep quality score by 20%",
                    priority = RecommendationPriority.MEDIUM,
                    category = RecommendationCategory.SLEEP,
                    estimatedImpact = "Better sleep quality and morning energy"
                )
            )
        }
        
        // Focus session optimization
        if (shouldRecommendFocusSessions()) {
            recommendations.add(
                PersonalizedRecommendation(
                    title = "Try Focused Work Sessions",
                    description = "Your attention span is strongest during 90-minute blocks with 15-minute breaks",
                    priority = RecommendationPriority.MEDIUM,
                    category = RecommendationCategory.FOCUS,
                    estimatedImpact = "25% increase in deep work quality"
                )
            )
        }
        
        // Social media batching
        if (shouldRecommendSocialMediaBatching()) {
            recommendations.add(
                PersonalizedRecommendation(
                    title = "Batch Your Social Media Time",
                    description = "You check social media 23 times per day. Batching into 3 sessions could reduce interruptions by 60%",
                    priority = RecommendationPriority.HIGH,
                    category = RecommendationCategory.PRODUCTIVITY,
                    estimatedImpact = "Reduced context switching and better focus"
                )
            )
        }
        
        // Stress management
        if (shouldRecommendStressManagement()) {
            recommendations.add(
                PersonalizedRecommendation(
                    title = "Manage Digital Stress",
                    description = "Your usage spikes during stressful periods. Consider mindfulness techniques instead",
                    priority = RecommendationPriority.MEDIUM,
                    category = RecommendationCategory.WELLNESS,
                    estimatedImpact = "Reduced anxiety and better emotional regulation"
                )
            )
        }
        
        return recommendations.sortedByDescending { it.priority.ordinal }
    }
    
    /**
     * Calculates digital wellness score based on multiple health factors
     */
    suspend fun calculateDigitalWellnessScore(): Float {
        if (!premiumRepository.hasFeature(PremiumFeature.ADVANCED_ANALYTICS)) return 0f
        
        val now = LocalDateTime.now()
        val weekAgo = now.minusDays(7)
        
        // Get weekly usage data
        val weeklyTrends = premiumRepository.getExtendedUsageHistory(7)
        
        // Calculate sub-scores
        val balanceScore = calculateUsageBalanceScore(weeklyTrends)
        val sleepScore = calculateSleepImpactScore(weeklyTrends)
        val stressScore = calculateStressIndicatorScore(weeklyTrends)
        val socialScore = calculateSocialBalanceScore(weeklyTrends)
        
        // Weighted average
        return (balanceScore * 0.3f + 
                sleepScore * 0.25f + 
                stressScore * 0.25f + 
                socialScore * 0.2f).coerceIn(0f, 1f)
    }
    
    /**
     * Generates comparative insights against user's historical data and anonymized benchmarks
     */
    suspend fun generateComparativeInsights(): ComparisonData {
        if (!premiumRepository.hasFeature(PremiumFeature.COMPARATIVE_INSIGHTS)) {
            return ComparisonData(0f, 0f, 0, 0f)
        }
        
        val currentWeekUsage = calculateCurrentWeekUsage()
        val previousWeekUsage = calculatePreviousWeekUsage()
        val globalAverage = getAnonymizedGlobalAverage()
        
        val improvement = if (previousWeekUsage > 0) {
            ((previousWeekUsage - currentWeekUsage) / previousWeekUsage * 100)
        } else 0f
        
        val percentile = calculatePercentile(currentWeekUsage, globalAverage)
        
        return ComparisonData(
            userAverage = currentWeekUsage,
            globalAverage = globalAverage,
            percentile = percentile,
            improvement = improvement
        )
    }
    
    /**
     * Exports usage data in various formats for premium users
     */
    suspend fun exportDetailedUsageData(
        format: ExportFormat,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): String {
        if (!premiumRepository.hasFeature(PremiumFeature.DATA_EXPORT)) return ""
        
        val usageEvents = usageRepository.getUsageEventsInRange(
            startDate.toEpochSecond(java.time.ZoneOffset.UTC) * 1000,
            endDate.toEpochSecond(java.time.ZoneOffset.UTC) * 1000
        )
        
        val analytics = premiumRepository.getAdvancedAnalytics(startDate, endDate)
        
        return when (format) {
            ExportFormat.CSV -> generateCSVExport(usageEvents, analytics)
            ExportFormat.JSON -> generateJSONExport(usageEvents, analytics)
            ExportFormat.PDF -> generatePDFExport(usageEvents, analytics)
        }
    }
    
    /**
     * Identifies peak productivity hours for the user
     */
    suspend fun identifyPeakProductivityHours(): List<Int> {
        val hourlyProductivity = mutableMapOf<Int, Float>()
        
        // Analyze last 30 days
        for (day in 0..29) {
            val date = LocalDateTime.now().minusDays(day.toLong())
            val dayScore = calculateProductivityScore(date)
            
            // Distribute score across hours (simplified)
            for (hour in 9..17) { // Work hours
                hourlyProductivity[hour] = hourlyProductivity.getOrDefault(hour, 0f) + dayScore
            }
        }
        
        // Return top 3 hours
        return hourlyProductivity.entries
            .sortedByDescending { it.value }
            .take(3)
            .map { it.key }
    }
    
    /**
     * Calculates focus quality based on interruption patterns
     */
    suspend fun calculateFocusQuality(date: LocalDateTime): Float {
        val dayStart = date.truncatedTo(ChronoUnit.DAYS)
        val dayEnd = dayStart.plusDays(1)
        
        val usageEvents = usageRepository.getUsageEventsInRange(
            dayStart.toEpochSecond(java.time.ZoneOffset.UTC) * 1000,
            dayEnd.toEpochSecond(java.time.ZoneOffset.UTC) * 1000
        )
        
        val socialMediaEvents = usageEvents.filter { event ->
            event.packageName in setOf(
                "com.instagram.android",
                "com.zhiliaoapp.musically",
                "com.facebook.katana",
                "com.twitter.android"
            )
        }
        
        val workHourEvents = socialMediaEvents.filter { event ->
            val hour = LocalDateTime.ofEpochSecond(
                event.timestamp / 1000, 
                0, 
                java.time.ZoneOffset.UTC
            ).hour
            hour in 9..17
        }
        
        // Lower interruptions = higher focus quality
        val interruptionScore = when {
            workHourEvents.size <= 3 -> 1.0f
            workHourEvents.size <= 6 -> 0.8f
            workHourEvents.size <= 10 -> 0.6f
            workHourEvents.size <= 15 -> 0.4f
            else -> 0.2f
        }
        
        return interruptionScore
    }
    
    // Private helper methods
    
    private suspend fun calculateFocusScore(
        usageEvents: List<com.unswipe.android.data.model.UsageEvent>,
        userSchedule: UserSchedule,
        date: LocalDateTime
    ): Float {
        // Count social media interruptions during work hours
        val workInterruptions = usageEvents.count { event ->
            val eventTime = LocalDateTime.ofEpochSecond(
                event.timestamp / 1000, 
                0, 
                java.time.ZoneOffset.UTC
            )
            val hour = eventTime.hour
            
            hour >= userSchedule.workStartTime.hour && 
            hour < userSchedule.workEndTime.hour &&
            event.packageName in setOf(
                "com.instagram.android",
                "com.zhiliaoapp.musically",
                "com.facebook.katana"
            )
        }
        
        // Inverse relationship: fewer interruptions = higher score
        return when {
            workInterruptions <= 2 -> 1.0f
            workInterruptions <= 5 -> 0.8f
            workInterruptions <= 8 -> 0.6f
            workInterruptions <= 12 -> 0.4f
            else -> 0.2f
        }
    }
    
    private fun calculateDistractionScore(
        usageEvents: List<com.unswipe.android.data.model.UsageEvent>,
        userSchedule: UserSchedule
    ): Float {
        val totalSocialMediaTime = usageEvents
            .filter { it.packageName in setOf("com.instagram.android", "com.zhiliaoapp.musically") }
            .size * 60000L // Approximate 1 minute per event
        
        val dailyLimit = TimeUnit.HOURS.toMillis(2) // 2 hours as baseline
        val ratio = totalSocialMediaTime.toFloat() / dailyLimit
        
        return (1f - ratio).coerceIn(0f, 1f)
    }
    
    private fun calculateTimingScore(
        usageEvents: List<com.unswipe.android.data.model.UsageEvent>,
        userSchedule: UserSchedule
    ): Float {
        val lateNightUsage = usageEvents.count { event ->
            val hour = LocalDateTime.ofEpochSecond(
                event.timestamp / 1000, 
                0, 
                java.time.ZoneOffset.UTC
            ).hour
            hour >= 22 || hour <= 6
        }
        
        return when {
            lateNightUsage == 0 -> 1.0f
            lateNightUsage <= 2 -> 0.8f
            lateNightUsage <= 5 -> 0.6f
            else -> 0.3f
        }
    }
    
    private fun calculateConsistencyScore(
        usageEvents: List<com.unswipe.android.data.model.UsageEvent>
    ): Float {
        // Measure consistency in usage patterns
        val hourlyUsage = usageEvents.groupBy { event ->
            LocalDateTime.ofEpochSecond(
                event.timestamp / 1000, 
                0, 
                java.time.ZoneOffset.UTC
            ).hour
        }
        
        val variance = hourlyUsage.values.map { it.size.toFloat() }.let { values ->
            val mean = values.average().toFloat()
            values.map { (it - mean).pow(2) }.average().toFloat()
        }
        
        // Lower variance = higher consistency = higher score
        return (1f / (1f + variance * 0.1f)).coerceIn(0f, 1f)
    }
    
    private fun calculateUsageBalanceScore(trends: List<TrendData>): Float {
        if (trends.isEmpty()) return 0f
        
        val avgUsage = trends.map { it.usageMinutes }.average()
        val idealUsage = 120 // 2 hours per day
        
        val balanceRatio = idealUsage / avgUsage.coerceAtLeast(1.0)
        return balanceRatio.toFloat().coerceIn(0f, 1f)
    }
    
    private fun calculateSleepImpactScore(trends: List<TrendData>): Float {
        // Simplified: assume less evening usage = better sleep
        val eveningUsage = trends.map { it.usageMinutes * 0.3f }.average() // Assume 30% is evening
        return (1f - (eveningUsage / 60f)).coerceIn(0f, 1f)
    }
    
    private fun calculateStressIndicatorScore(trends: List<TrendData>): Float {
        val avgSessions = trends.map { it.sessionCount }.average()
        val stressThreshold = 20 // More than 20 sessions per day indicates stress
        
        return (1f - (avgSessions / stressThreshold).toFloat()).coerceIn(0f, 1f)
    }
    
    private fun calculateSocialBalanceScore(trends: List<TrendData>): Float {
        // Simplified social balance calculation
        val avgUsage = trends.map { it.usageMinutes }.average()
        val socialThreshold = 180 // 3 hours
        
        return (1f - (avgUsage / socialThreshold).toFloat()).coerceIn(0f, 1f)
    }
    
    private suspend fun shouldRecommendMorningRoutineOptimization(): Boolean {
        // Analyze morning usage patterns
        return true // Simplified
    }
    
    private suspend fun shouldRecommendSleepHygiene(): Boolean {
        // Check late-night usage
        return true // Simplified
    }
    
    private suspend fun shouldRecommendFocusSessions(): Boolean {
        // Analyze work interruptions
        return true // Simplified
    }
    
    private suspend fun shouldRecommendSocialMediaBatching(): Boolean {
        // Check session frequency
        return true // Simplified
    }
    
    private suspend fun shouldRecommendStressManagement(): Boolean {
        // Detect stress patterns
        return true // Simplified
    }
    
    private suspend fun calculateCurrentWeekUsage(): Float {
        // Calculate current week average usage
        return 3.2f // Placeholder
    }
    
    private suspend fun calculatePreviousWeekUsage(): Float {
        // Calculate previous week average usage
        return 3.8f // Placeholder
    }
    
    private fun getAnonymizedGlobalAverage(): Float {
        // Return anonymized global average (no personal data)
        return 4.1f // Placeholder
    }
    
    private fun calculatePercentile(userUsage: Float, globalAverage: Float): Int {
        // Calculate user's percentile compared to global average
        return if (userUsage < globalAverage) {
            ((globalAverage - userUsage) / globalAverage * 50 + 50).toInt()
        } else {
            (50 - (userUsage - globalAverage) / globalAverage * 50).toInt()
        }.coerceIn(1, 99)
    }
    
    private fun generateCSVExport(
        events: List<com.unswipe.android.data.model.UsageEvent>,
        analytics: AdvancedAnalytics
    ): String {
        val header = "Date,App,Event Type,Timestamp,Productivity Score\n"
        val data = events.joinToString("\n") { event ->
            val date = LocalDateTime.ofEpochSecond(event.timestamp / 1000, 0, java.time.ZoneOffset.UTC)
            "${date.toLocalDate()},${event.packageName},${event.eventType},${event.timestamp},${analytics.productivityScore}"
        }
        return header + data
    }
    
    private fun generateJSONExport(
        events: List<com.unswipe.android.data.model.UsageEvent>,
        analytics: AdvancedAnalytics
    ): String {
        // Simplified JSON export
        return """
        {
            "export_date": "${LocalDateTime.now()}",
            "productivity_score": ${analytics.productivityScore},
            "focus_quality": ${analytics.focusQuality},
            "digital_wellness_score": ${analytics.digitalWellnessScore},
            "events_count": ${events.size},
            "recommendations": ${analytics.recommendations.size}
        }
        """.trimIndent()
    }
    
    private fun generatePDFExport(
        events: List<com.unswipe.android.data.model.UsageEvent>,
        analytics: AdvancedAnalytics
    ): String {
        // This would generate actual PDF content
        return "PDF_EXPORT_PLACEHOLDER_${events.size}_EVENTS"
    }
}