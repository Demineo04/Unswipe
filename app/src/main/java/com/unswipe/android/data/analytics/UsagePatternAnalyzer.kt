package com.unswipe.android.data.analytics

import com.unswipe.android.domain.model.*
import com.unswipe.android.domain.repository.UsageRepository
import com.unswipe.android.data.model.UsageEvent
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsagePatternAnalyzer @Inject constructor(
    private val usageRepository: UsageRepository
) {
    
    /**
     * Analyzes usage patterns and returns detected patterns
     */
    suspend fun detectUsagePatterns(): List<UsagePattern> {
        val thirtyDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30)
        val now = System.currentTimeMillis()
        
        // Get usage events for analysis (this would need to be implemented in UsageRepository)
        val events = getUsageEventsInRange(thirtyDaysAgo, now)
        
        return listOfNotNull(
            detectBingeUsagePattern(events),
            detectStressUsagePattern(events),
            detectProcrastinationPattern(events),
            detectImpulseUsagePattern(events),
            detectSleepDisruptionPattern(events),
            detectWorkDistractionPattern(events)
        )
    }
    
    /**
     * Detects binge usage patterns (long sessions > 30 minutes)
     */
    private fun detectBingeUsagePattern(events: List<UsageEvent>): UsagePattern? {
        val sessions = groupEventsIntoSessions(events)
        val longSessions = sessions.filter { it.duration > TimeUnit.MINUTES.toMillis(30) }
        
        if (longSessions.size >= 3) { // 3 or more long sessions in 30 days
            val affectedApps = longSessions.map { it.packageName }.distinct()
            val avgSessionLength = longSessions.map { it.duration }.average()
            
            return UsagePattern(
                type = PatternType.BINGE_USAGE,
                description = "Detected ${longSessions.size} long usage sessions (avg: ${formatDuration(avgSessionLength.toLong())})",
                apps = affectedApps,
                confidence = calculateBingeConfidence(longSessions.size, avgSessionLength),
                suggestion = "Try setting session timers or taking regular breaks every hour"
            )
        }
        return null
    }
    
    /**
     * Detects stress usage patterns (increased usage during stress indicators)
     */
    private fun detectStressUsagePattern(events: List<UsageEvent>): UsagePattern? {
        val eveningEvents = events.filter { isEveningTime(it.timestamp) }
        val lateNightEvents = events.filter { isLateNightTime(it.timestamp) }
        val rapidSwitchingDays = detectRapidAppSwitching(events)
        
        val stressIndicators = listOf(
            eveningEvents.size > events.size * 0.4, // 40% of usage in evening
            lateNightEvents.isNotEmpty(),
            rapidSwitchingDays.size > 5 // More than 5 days with rapid switching
        ).count { it }
        
        if (stressIndicators >= 2) {
            return UsagePattern(
                type = PatternType.STRESS_USAGE,
                description = "Usage patterns suggest stress or anxiety periods",
                confidence = stressIndicators / 3.0,
                suggestion = "Consider mindfulness exercises or talking to someone when feeling overwhelmed"
            )
        }
        return null
    }
    
    /**
     * Detects procrastination patterns (heavy social media during work hours)
     */
    private fun detectProcrastinationPattern(events: List<UsageEvent>): UsagePattern? {
        val workHourEvents = events.filter { isWorkHours(it.timestamp) && isSocialMediaApp(it.packageName) }
        val workDays = getWorkDaysInRange(events.map { it.timestamp })
        
        if (workDays.isNotEmpty()) {
            val avgWorkUsagePerDay = workHourEvents.size.toDouble() / workDays.size
            
            if (avgWorkUsagePerDay > 10) { // More than 10 social media interactions per work day
                val topApps = workHourEvents.groupBy { it.packageName }
                    .mapValues { it.value.size }
                    .toList()
                    .sortedByDescending { it.second }
                    .take(3)
                    .map { it.first }
                
                return UsagePattern(
                    type = PatternType.PROCRASTINATION,
                    description = "High social media usage during work hours (${avgWorkUsagePerDay.toInt()} interactions/day)",
                    apps = topApps,
                    confidence = (avgWorkUsagePerDay / 20).coerceAtMost(1.0),
                    suggestion = "Try using Focus Mode or app blocking during work hours"
                )
            }
        }
        return null
    }
    
    /**
     * Detects impulse usage patterns (frequent short sessions)
     */
    private fun detectImpulseUsagePattern(events: List<UsageEvent>): UsagePattern? {
        val dailyLaunchCounts = events.filter { it.eventType == "APP_LAUNCH" }
            .groupBy { getDateFromTimestamp(it.timestamp) }
            .mapValues { it.value.size }
        
        val highLaunchDays = dailyLaunchCounts.values.count { it > 20 } // More than 20 launches per day
        
        if (highLaunchDays > 7) { // More than a week of high launch activity
            val avgLaunches = dailyLaunchCounts.values.average()
            
            return UsagePattern(
                type = PatternType.IMPULSE_USAGE,
                description = "Frequent app checking behavior (${avgLaunches.toInt()} launches/day)",
                confidence = (highLaunchDays / 14.0).coerceAtMost(1.0),
                suggestion = "Try batching your social media time or using notification management"
            )
        }
        return null
    }
    
    /**
     * Detects sleep disruption patterns (usage close to bedtime)
     */
    private fun detectSleepDisruptionPattern(events: List<UsageEvent>): UsagePattern? {
        val bedtimeEvents = events.filter { isBedtimeUsage(it.timestamp) }
        val nightsWithUsage = bedtimeEvents.groupBy { getDateFromTimestamp(it.timestamp) }.size
        
        if (nightsWithUsage > 10) { // More than 10 nights with bedtime usage
            val affectedApps = bedtimeEvents.groupBy { it.packageName }
                .mapValues { it.value.size }
                .toList()
                .sortedByDescending { it.second }
                .take(3)
                .map { it.first }
            
            return UsagePattern(
                type = PatternType.SLEEP_DISRUPTION,
                description = "Regular screen usage close to bedtime ($nightsWithUsage nights)",
                apps = affectedApps,
                confidence = (nightsWithUsage / 20.0).coerceAtMost(1.0),
                suggestion = "Consider enabling bedtime mode or using blue light filters"
            )
        }
        return null
    }
    
    /**
     * Detects work distraction patterns
     */
    private fun detectWorkDistractionPattern(events: List<UsageEvent>): UsagePattern? {
        val workEvents = events.filter { isWorkHours(it.timestamp) }
        val socialMediaWorkEvents = workEvents.filter { isSocialMediaApp(it.packageName) }
        
        if (workEvents.isNotEmpty()) {
            val distractionRate = socialMediaWorkEvents.size.toDouble() / workEvents.size
            
            if (distractionRate > 0.3) { // More than 30% of work time on social media
                return UsagePattern(
                    type = PatternType.WORK_DISTRACTION,
                    description = "High social media usage during work hours (${(distractionRate * 100).toInt()}%)",
                    confidence = distractionRate,
                    suggestion = "Consider using productivity tools or scheduled social media breaks"
                )
            }
        }
        return null
    }
    
    // Helper methods
    
    private suspend fun getUsageEventsInRange(startTime: Long, endTime: Long): List<UsageEvent> {
        // This would need to be implemented in UsageRepository
        // For now, return empty list as placeholder
        return emptyList()
    }
    
    private fun groupEventsIntoSessions(events: List<UsageEvent>): List<UsageSession> {
        // Group events by app and date, calculate session durations
        return events.groupBy { "${it.packageName}-${getDateFromTimestamp(it.timestamp)}" }
            .map { (key, sessionEvents) ->
                val packageName = key.split("-")[0]
                val startTime = sessionEvents.minOf { it.timestamp }
                val endTime = sessionEvents.maxOf { it.timestamp }
                val duration = endTime - startTime
                
                UsageSession(packageName, startTime, endTime, duration)
            }
    }
    
    private fun detectRapidAppSwitching(events: List<UsageEvent>): List<String> {
        // Detect days with rapid app switching (more than 20 app switches)
        return events.filter { it.eventType == "APP_LAUNCH" }
            .groupBy { getDateFromTimestamp(it.timestamp) }
            .filter { it.value.size > 20 }
            .keys
            .toList()
    }
    
    private fun isEveningTime(timestamp: Long): Boolean {
        val hour = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()).hour
        return hour >= 18 && hour <= 22 // 6 PM to 10 PM
    }
    
    private fun isLateNightTime(timestamp: Long): Boolean {
        val hour = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()).hour
        return hour >= 23 || hour <= 5 // 11 PM to 5 AM
    }
    
    private fun isWorkHours(timestamp: Long): Boolean {
        val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
        val hour = dateTime.hour
        val dayOfWeek = dateTime.dayOfWeek.value
        
        return dayOfWeek in 1..5 && hour >= 9 && hour <= 17 // Monday-Friday, 9 AM to 5 PM
    }
    
    private fun isBedtimeUsage(timestamp: Long): Boolean {
        val hour = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()).hour
        return hour >= 22 || hour <= 6 // 10 PM to 6 AM
    }
    
    private fun isSocialMediaApp(packageName: String): Boolean {
        val socialMediaApps = setOf(
            "com.zhiliaoapp.musically", // TikTok
            "com.instagram.android",
            "com.google.android.youtube",
            "com.facebook.katana",
            "com.snapchat.android",
            "com.twitter.android",
            "com.linkedin.android"
        )
        return socialMediaApps.contains(packageName)
    }
    
    private fun getDateFromTimestamp(timestamp: Long): String {
        val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
        return "${dateTime.year}-${dateTime.monthValue}-${dateTime.dayOfMonth}"
    }
    
    private fun getWorkDaysInRange(timestamps: List<Long>): Set<String> {
        return timestamps.map { timestamp ->
            val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
            if (dateTime.dayOfWeek.value in 1..5) { // Monday-Friday
                getDateFromTimestamp(timestamp)
            } else null
        }.filterNotNull().toSet()
    }
    
    private fun calculateBingeConfidence(sessionCount: Int, avgDuration: Double): Double {
        val sessionScore = (sessionCount / 10.0).coerceAtMost(1.0)
        val durationScore = (avgDuration / TimeUnit.HOURS.toMillis(2)).coerceAtMost(1.0) // Changed from 4 hours to 2 hours baseline
        return (sessionScore + durationScore) / 2.0
    }
    
    private fun formatDuration(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        return "${hours}h ${minutes}m"
    }
    
    /**
     * Data class for usage sessions
     */
    private data class UsageSession(
        val packageName: String,
        val startTime: Long,
        val endTime: Long,
        val duration: Long
    )
}