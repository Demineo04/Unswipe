package com.unswipe.android.data.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.unswipe.android.R
import com.unswipe.android.data.context.ContextDetectionEngine
import com.unswipe.android.data.analytics.UsagePatternAnalyzer
import com.unswipe.android.domain.model.*
import com.unswipe.android.domain.repository.UsageRepository
import com.unswipe.android.domain.repository.SettingsRepository
import com.unswipe.android.ui.MainActivity
import kotlinx.coroutines.flow.first
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContextAwareNotificationEngine @Inject constructor(
    private val context: Context,
    private val contextEngine: ContextDetectionEngine,
    private val patternAnalyzer: UsagePatternAnalyzer,
    private val usageRepository: UsageRepository,
    private val settingsRepository: SettingsRepository,
    private val notificationManager: NotificationManagerCompat
) {
    
    companion object {
        private const val CHANNEL_ID_INSIGHTS = "unswipe_insights"
        private const val CHANNEL_ID_WARNINGS = "unswipe_warnings"
        private const val CHANNEL_ID_GENTLE = "unswipe_gentle"
        
        private const val NOTIFICATION_ID_WORK = 1001
        private const val NOTIFICATION_ID_SLEEP = 1002
        private const val NOTIFICATION_ID_PATTERN = 1003
        private const val NOTIFICATION_ID_BINGE = 1004
    }
    
    init {
        createNotificationChannels()
    }
    
    /**
     * Analyzes current usage and generates contextual notifications if needed
     */
    suspend fun analyzeAndNotify(packageName: String, usageTime: Long, sessionCount: Int = 1) {
        val context = contextEngine.detectCurrentContext()
        val insight = generateContextualInsight(packageName, usageTime, sessionCount, context)
        
        insight?.let { 
            if (it.shouldNotify) {
                sendContextualNotification(it)
            }
        }
    }
    
    /**
     * Checks for pattern-based insights and sends notifications
     */
    suspend fun checkAndNotifyPatterns() {
        val patterns = patternAnalyzer.detectUsagePatterns()
        
        patterns.forEach { pattern ->
            if (pattern.isActive && shouldNotifyForPattern(pattern)) {
                val insight = generatePatternInsight(pattern)
                sendContextualNotification(insight)
            }
        }
    }
    
    /**
     * Generates contextual insights based on usage and context
     */
    private suspend fun generateContextualInsight(
        packageName: String,
        usageTime: Long,
        sessionCount: Int,
        contextType: ContextType
    ): ContextualInsight? {
        
        return when (contextType) {
            ContextType.WORK_HOURS -> generateWorkTimeInsight(packageName, usageTime, sessionCount)
            ContextType.SLEEP_PREPARATION -> generateSleepPreparationInsight(packageName, usageTime)
            ContextType.BEDTIME -> generateBedtimeInsight(packageName, usageTime)
            ContextType.MORNING_ROUTINE -> generateMorningRoutineInsight(packageName, usageTime)
            else -> generateGeneralInsight(packageName, usageTime, sessionCount)
        }
    }
    
    private suspend fun generateWorkTimeInsight(
        packageName: String,
        usageTime: Long,
        sessionCount: Int
    ): ContextualInsight? {
        val appName = getAppName(packageName)
        val workTimeLimit = getWorkTimeLimit()
        val usagePercentage = usageTime.toFloat() / workTimeLimit
        
        return when {
            usageTime > workTimeLimit && sessionCount > 5 -> ContextualInsight(
                type = InsightType.WORK_DISTRACTION,
                message = "You've used $appName $sessionCount times during work hours (${formatTime(usageTime)} total).",
                suggestion = "Frequent app switching can reduce productivity by up to 25%.",
                severity = InsightSeverity.MEDIUM,
                shouldNotify = true,
                actionButton = "Enable Focus Mode",
                contextualTip = "Try batching social media time or scheduling specific break periods."
            )
            
            usageTime > workTimeLimit * 1.5 -> ContextualInsight(
                type = InsightType.WORK_INTERRUPTION,
                message = "You've exceeded your work-time limit for $appName by ${formatTime(usageTime - workTimeLimit)}.",
                suggestion = "Consider taking a productivity break instead.",
                severity = InsightSeverity.HIGH,
                shouldNotify = true,
                actionButton = "Block Until 5 PM",
                alternativeActivity = "Take a 5-minute walk or grab some water."
            )
            
            usagePercentage > 0.8 -> ContextualInsight(
                type = InsightType.PRODUCTIVITY_TIP,
                message = "You're approaching your work-time limit for $appName (${(usagePercentage * 100).toInt()}%).",
                suggestion = "Consider wrapping up and focusing on work tasks.",
                severity = InsightSeverity.LOW,
                shouldNotify = true,
                contextualTip = "Research shows that limiting social media during work can improve focus by 40%."
            )
            
            else -> null
        }
    }
    
    private suspend fun generateSleepPreparationInsight(
        packageName: String,
        usageTime: Long
    ): ContextualInsight? {
        val appName = getAppName(packageName)
        val sleepTime = getSleepTime()
        val timeUntilBed = getTimeUntilBed(sleepTime)
        
        return when {
            usageTime > TimeUnit.MINUTES.toMillis(30) && timeUntilBed < 60 -> ContextualInsight(
                type = InsightType.SLEEP_HYGIENE,
                message = "You've spent ${formatTime(usageTime)} on $appName with ${timeUntilBed}min until bedtime.",
                suggestion = "Blue light and stimulating content can delay sleep by 30+ minutes.",
                severity = InsightSeverity.HIGH,
                shouldNotify = true,
                actionButton = "Enable Bedtime Mode",
                alternativeActivity = "Try reading, meditation, or gentle stretching instead."
            )
            
            usageTime > TimeUnit.MINUTES.toMillis(15) -> ContextualInsight(
                type = InsightType.WELLNESS_REMINDER,
                message = "Wind-down time! You have ${timeUntilBed}min before bed.",
                suggestion = "Consider switching to calming activities to prepare for better sleep.",
                severity = InsightSeverity.MEDIUM,
                shouldNotify = true,
                contextualTip = "Good sleep hygiene starts 1-2 hours before bedtime."
            )
            
            else -> null
        }
    }
    
    private suspend fun generateBedtimeInsight(
        packageName: String,
        usageTime: Long
    ): ContextualInsight? {
        val appName = getAppName(packageName)
        
        return ContextualInsight(
            type = InsightType.SLEEP_HYGIENE,
            message = "It's past your bedtime. Using screens now can disrupt your sleep cycle.",
            suggestion = "Your body needs rest to recover and recharge for tomorrow.",
            severity = InsightSeverity.HIGH,
            shouldNotify = true,
            actionButton = "Goodnight Mode",
            alternativeActivity = "Try deep breathing, reading, or listening to calming music."
        )
    }
    
    private suspend fun generateMorningRoutineInsight(
        packageName: String,
        usageTime: Long
    ): ContextualInsight? {
        val appName = getAppName(packageName)
        
        return if (usageTime > TimeUnit.MINUTES.toMillis(20)) {
            ContextualInsight(
                type = InsightType.WELLNESS_REMINDER,
                message = "Good morning! You've spent ${formatTime(usageTime)} on $appName already.",
                suggestion = "Consider starting your day with intention and energy.",
                severity = InsightSeverity.LOW,
                shouldNotify = true,
                contextualTip = "Morning routines that avoid screens can improve mood and productivity throughout the day."
            )
        } else null
    }
    
    private suspend fun generateGeneralInsight(
        packageName: String,
        usageTime: Long,
        sessionCount: Int
    ): ContextualInsight? {
        val appName = getAppName(packageName)
        val dailyLimit = getDailyLimit()
        val usagePercentage = usageTime.toFloat() / dailyLimit
        
        return when {
            usageTime > dailyLimit -> ContextualInsight(
                type = InsightType.BINGE_WARNING,
                message = "You've exceeded your daily limit for $appName by ${formatTime(usageTime - dailyLimit)}.",
                suggestion = "Consider taking a break and doing something offline.",
                severity = InsightSeverity.MEDIUM,
                shouldNotify = true,
                alternativeActivity = "Try going for a walk, calling a friend, or pursuing a hobby."
            )
            
            sessionCount > 20 -> ContextualInsight(
                type = InsightType.STRESS_INDICATOR,
                message = "You've checked $appName $sessionCount times today.",
                suggestion = "Frequent checking might indicate stress or boredom.",
                severity = InsightSeverity.LOW,
                shouldNotify = true,
                contextualTip = "Try the 5-4-3-2-1 grounding technique: 5 things you see, 4 you touch, 3 you hear, 2 you smell, 1 you taste."
            )
            
            else -> null
        }
    }
    
    private fun generatePatternInsight(pattern: UsagePattern): ContextualInsight {
        return when (pattern.type) {
            PatternType.BINGE_USAGE -> ContextualInsight(
                type = InsightType.BINGE_WARNING,
                message = pattern.description,
                suggestion = pattern.suggestion,
                severity = InsightSeverity.MEDIUM,
                shouldNotify = true,
                contextualTip = "Long usage sessions can impact mental health and productivity."
            )
            
            PatternType.STRESS_USAGE -> ContextualInsight(
                type = InsightType.STRESS_INDICATOR,
                message = "Your usage patterns suggest you might be experiencing stress.",
                suggestion = pattern.suggestion,
                severity = InsightSeverity.MEDIUM,
                shouldNotify = true,
                alternativeActivity = "Try a 5-minute breathing exercise or reach out to someone you trust."
            )
            
            PatternType.SLEEP_DISRUPTION -> ContextualInsight(
                type = InsightType.SLEEP_HYGIENE,
                message = pattern.description,
                suggestion = pattern.suggestion,
                severity = InsightSeverity.HIGH,
                shouldNotify = true,
                actionButton = "Enable Bedtime Mode"
            )
            
            else -> ContextualInsight(
                type = InsightType.WELLNESS_REMINDER,
                message = pattern.description,
                suggestion = pattern.suggestion,
                severity = InsightSeverity.LOW,
                shouldNotify = true
            )
        }
    }
    
    private fun sendContextualNotification(insight: ContextualInsight) {
        val channelId = when (insight.severity) {
            InsightSeverity.HIGH -> CHANNEL_ID_WARNINGS
            InsightSeverity.MEDIUM -> CHANNEL_ID_INSIGHTS
            InsightSeverity.LOW -> CHANNEL_ID_GENTLE
        }
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(getNotificationTitle(insight.type))
            .setContentText(insight.message)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("${insight.message}\n\n💡 ${insight.suggestion}")
                .setSummaryText(insight.contextualTip))
            .setPriority(getNotificationPriority(insight.severity))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        val notificationId = when (insight.type) {
            InsightType.WORK_DISTRACTION, InsightType.WORK_INTERRUPTION -> NOTIFICATION_ID_WORK
            InsightType.SLEEP_HYGIENE -> NOTIFICATION_ID_SLEEP
            InsightType.BINGE_WARNING -> NOTIFICATION_ID_BINGE
            else -> NOTIFICATION_ID_PATTERN
        }
        
        notificationManager.notify(notificationId, notification)
    }
    
    // Helper methods
    
    private fun createNotificationChannels() {
        val channels = listOf(
            NotificationChannel(
                CHANNEL_ID_INSIGHTS,
                "Digital Wellness Insights",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Helpful insights about your digital habits"
            },
            
            NotificationChannel(
                CHANNEL_ID_WARNINGS,
                "Wellness Warnings",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Important alerts about unhealthy usage patterns"
            },
            
            NotificationChannel(
                CHANNEL_ID_GENTLE,
                "Gentle Reminders",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Subtle nudges for better digital wellness"
            }
        )
        
        channels.forEach { channel ->
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun getAppName(packageName: String): String {
        return try {
            val packageManager = context.packageManager
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            packageName.split(".").lastOrNull()?.capitalize() ?: "App"
        }
    }
    
    private fun getNotificationTitle(insightType: InsightType): String {
        return when (insightType) {
            InsightType.WORK_DISTRACTION -> "🔔 Work Focus"
            InsightType.WORK_INTERRUPTION -> "⚠️ Productivity Alert"
            InsightType.SLEEP_HYGIENE -> "🌙 Sleep Wellness"
            InsightType.BINGE_WARNING -> "⏰ Usage Alert"
            InsightType.STRESS_INDICATOR -> "💭 Wellness Check"
            InsightType.PRODUCTIVITY_TIP -> "💡 Productivity Tip"
            InsightType.WELLNESS_REMINDER -> "🌱 Wellness Reminder"
        }
    }
    
    private fun getNotificationPriority(severity: InsightSeverity): Int {
        return when (severity) {
            InsightSeverity.HIGH -> NotificationCompat.PRIORITY_HIGH
            InsightSeverity.MEDIUM -> NotificationCompat.PRIORITY_DEFAULT
            InsightSeverity.LOW -> NotificationCompat.PRIORITY_LOW
        }
    }
    
    private suspend fun getWorkTimeLimit(): Long {
        return try {
            // This would be implemented in SettingsRepository
            TimeUnit.MINUTES.toMillis(30) // Default 30 minutes during work
        } catch (e: Exception) {
            TimeUnit.MINUTES.toMillis(30)
        }
    }
    
    private suspend fun getDailyLimit(): Long {
        return settingsRepository.getDailyLimitFlow().first()
    }
    
    private suspend fun getSleepTime(): LocalTime {
        return LocalTime.of(23, 0) // Default 11 PM
    }
    
    private fun getTimeUntilBed(sleepTime: LocalTime): Long {
        val now = LocalTime.now()
        return if (now.isBefore(sleepTime)) {
            java.time.Duration.between(now, sleepTime).toMinutes()
        } else {
            0L
        }
    }
    
    private fun shouldNotifyForPattern(pattern: UsagePattern): Boolean {
        // Add logic to prevent notification spam
        // For now, only notify for high-confidence patterns
        return pattern.confidence > 0.7
    }
    
    private fun formatTime(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        
        return when {
            hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
            hours > 0 -> "${hours}h"
            minutes > 0 -> "${minutes}m"
            else -> "< 1m"
        }
    }
}