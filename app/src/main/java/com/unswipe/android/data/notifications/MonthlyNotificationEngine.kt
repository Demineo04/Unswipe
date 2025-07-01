package com.unswipe.android.data.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.unswipe.android.R
import com.unswipe.android.domain.repository.UsageRepository
import com.unswipe.android.domain.repository.SettingsRepository
import com.unswipe.android.domain.repository.PremiumRepository
import com.unswipe.android.domain.model.PremiumFeature
import com.unswipe.android.ui.MainActivity
import com.unswipe.android.util.NotificationPermissionHelper
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MonthlyNotificationEngine @Inject constructor(
    private val context: Context,
    private val usageRepository: UsageRepository,
    private val settingsRepository: SettingsRepository,
    private val premiumRepository: PremiumRepository,
    private val notificationManager: NotificationManagerCompat
) {
    
    companion object {
        private const val CHANNEL_ID_MONTHLY_SUMMARY = "monthly_summary"
        private const val CHANNEL_ID_MONTHLY_ACHIEVEMENTS = "monthly_achievements"
        private const val CHANNEL_ID_MONTHLY_INSIGHTS = "monthly_insights"
        
        private const val NOTIFICATION_ID_MONTHLY_RECAP = 2001
        private const val NOTIFICATION_ID_MONTHLY_ACHIEVEMENT = 2002
        private const val NOTIFICATION_ID_MONTHLY_GOAL = 2003
        private const val NOTIFICATION_ID_MONTHLY_INSIGHT = 2004
    }
    
    init {
        createNotificationChannels()
    }
    
    /**
     * Sends end-of-month summary notification
     */
    suspend fun sendMonthlyRecap() {
        if (!NotificationPermissionHelper.hasNotificationPermission(context)) return
        
        val lastMonth = LocalDate.now().minusMonths(1)
        val monthName = lastMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
        
        // Calculate monthly statistics
        val monthlyStats = calculateMonthlyStats(lastMonth)
        
        val title = "📊 Your $monthName Summary"
        val message = buildMonthlyRecapMessage(monthlyStats, monthName)
        
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Add extra to navigate to monthly analytics
            putExtra("navigate_to", "monthly_analytics")
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_MONTHLY_SUMMARY)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(buildDetailedMonthlyRecap(monthlyStats, monthName)))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID_MONTHLY_RECAP, notification)
    }
    
    /**
     * Sends monthly achievement notifications
     */
    suspend fun sendMonthlyAchievements() {
        if (!NotificationPermissionHelper.hasNotificationPermission(context)) return
        
        val lastMonth = LocalDate.now().minusMonths(1)
        val achievements = detectMonthlyAchievements(lastMonth)
        
        achievements.forEach { achievement ->
            sendAchievementNotification(achievement)
        }
    }
    
    /**
     * Sends monthly goal progress notification
     */
    suspend fun sendMonthlyGoalProgress() {
        if (!NotificationPermissionHelper.hasNotificationPermission(context)) return
        
        val currentMonth = LocalDate.now().withDayOfMonth(1)
        val today = LocalDate.now()
        val daysInMonth = currentMonth.lengthOfMonth()
        val dayOfMonth = today.dayOfMonth
        
        // Only send on specific days (15th and last day of month)
        if (dayOfMonth != 15 && dayOfMonth != daysInMonth) return
        
        val monthlyStats = calculateMonthlyStats(currentMonth, today)
        val dailyLimitMillis = settingsRepository.getDailyLimitFlow().first()
        val monthlyGoalMinutes = (dailyLimitMillis / (1000 * 60)) * daysInMonth
        val progressPercentage = (monthlyStats.totalUsageMinutes.toFloat() / monthlyGoalMinutes * 100).toInt()
        
        val (title, message) = when {
            dayOfMonth == 15 -> {
                "🎯 Mid-Month Check-in" to 
                "You're ${progressPercentage}% through your monthly goal. ${
                    if (progressPercentage <= 50) "Great pacing!" 
                    else "Consider adjusting your habits for the rest of the month."
                }"
            }
            progressPercentage <= 100 -> {
                "🎉 Monthly Goal Achieved!" to 
                "Congratulations! You stayed within your monthly goal of ${formatTime(monthlyGoalMinutes)}."
            }
            else -> {
                "📈 Monthly Goal Update" to 
                "You used ${formatTime(monthlyStats.totalUsageMinutes)} this month (${progressPercentage}% of goal). Every step towards awareness counts!"
            }
        }
        
        sendGoalProgressNotification(title, message)
    }
    
    /**
     * Sends monthly insights for premium users
     */
    suspend fun sendMonthlyInsights() {
        if (!premiumRepository.hasFeature(PremiumFeature.ADVANCED_ANALYTICS)) return
        if (!NotificationPermissionHelper.hasNotificationPermission(context)) return
        
        val lastMonth = LocalDate.now().minusMonths(1)
        val insights = generateMonthlyInsights(lastMonth)
        
        insights.forEach { insight ->
            sendInsightNotification(insight)
        }
    }
    
    /**
     * Sends new month motivation notification
     */
    suspend fun sendNewMonthMotivation() {
        if (!NotificationPermissionHelper.hasNotificationPermission(context)) return
        
        val currentMonth = LocalDate.now()
        val monthName = currentMonth.format(DateTimeFormatter.ofPattern("MMMM"))
        
        val motivationalMessages = listOf(
            "🌟 Welcome to $monthName! Ready for a fresh start with your digital wellness goals?",
            "🚀 New month, new opportunities! Set your intentions for $monthName.",
            "💪 $monthName is here! Time to build on your progress and create healthy habits.",
            "🎯 Fresh month, fresh goals! What do you want to achieve in $monthName?",
            "✨ $monthName begins! Continue your journey towards mindful technology use."
        )
        
        val message = motivationalMessages.random()
        
        sendMotivationNotification("New Month, New Goals! 🌟", message)
    }
    
    private suspend fun calculateMonthlyStats(month: LocalDate, endDate: LocalDate = month.plusMonths(1).minusDays(1)): MonthlyStats {
        // In real implementation, this would fetch actual usage data
        // For now, generating realistic demo data
        
        val daysInPeriod = ChronoUnit.DAYS.between(month, endDate.plusDays(1)).toInt()
        val dailyLimitMillis = settingsRepository.getDailyLimitFlow().first()
        val dailyLimitMinutes = (dailyLimitMillis / (1000 * 60)).toInt()
        
        // Generate realistic monthly statistics
        val averageDailyUsage = (dailyLimitMinutes * 0.8).toInt() // 80% of daily limit on average
        val totalUsageMinutes = averageDailyUsage * daysInPeriod
        val goalDays = (daysInPeriod * 0.7).toInt() // 70% success rate
        val daysOverLimit = daysInPeriod - goalDays
        val totalSessions = totalUsageMinutes / 12 // Average 12 minutes per session
        val bestDayMinutes = (dailyLimitMinutes * 0.4).toInt()
        val worstDayMinutes = (dailyLimitMinutes * 1.5).toInt()
        
        return MonthlyStats(
            totalUsageMinutes = totalUsageMinutes,
            averageDailyMinutes = averageDailyUsage,
            goalDays = goalDays,
            daysOverLimit = daysOverLimit,
            totalSessions = totalSessions,
            bestDayMinutes = bestDayMinutes,
            worstDayMinutes = worstDayMinutes,
            daysInPeriod = daysInPeriod
        )
    }
    
    private suspend fun detectMonthlyAchievements(month: LocalDate): List<MonthlyAchievement> {
        val achievements = mutableListOf<MonthlyAchievement>()
        val stats = calculateMonthlyStats(month)
        
        // Perfect week achievement
        if (stats.goalDays >= stats.daysInPeriod * 0.9) {
            achievements.add(
                MonthlyAchievement(
                    title = "🏆 Monthly Champion!",
                    description = "You stayed within your daily limit ${stats.goalDays} out of ${stats.daysInPeriod} days!",
                    type = AchievementType.CONSISTENCY
                )
            )
        }
        
        // Improvement achievement
        val previousMonth = month.minusMonths(1)
        val previousStats = calculateMonthlyStats(previousMonth)
        
        if (stats.totalUsageMinutes < previousStats.totalUsageMinutes * 0.9) {
            val improvement = ((previousStats.totalUsageMinutes - stats.totalUsageMinutes).toFloat() / previousStats.totalUsageMinutes * 100).toInt()
            achievements.add(
                MonthlyAchievement(
                    title = "📉 Great Improvement!",
                    description = "You reduced your usage by ${improvement}% compared to last month!",
                    type = AchievementType.IMPROVEMENT
                )
            )
        }
        
        // Streak achievement
        if (stats.goalDays >= 21) {
            achievements.add(
                MonthlyAchievement(
                    title = "🔥 Habit Master!",
                    description = "You've built a strong habit with ${stats.goalDays} successful days!",
                    type = AchievementType.STREAK
                )
            )
        }
        
        return achievements
    }
    
    private suspend fun generateMonthlyInsights(month: LocalDate): List<MonthlyInsightNotification> {
        val insights = mutableListOf<MonthlyInsightNotification>()
        val stats = calculateMonthlyStats(month)
        
        // Usage pattern insight
        insights.add(
            MonthlyInsightNotification(
                title = "📊 Monthly Pattern Analysis",
                description = "Your average session length was ${stats.totalUsageMinutes / stats.totalSessions} minutes. Consider shorter, more intentional sessions.",
                type = InsightType.PATTERN
            )
        )
        
        // Productivity insight
        if (stats.goalDays > stats.daysInPeriod * 0.6) {
            insights.add(
                MonthlyInsightNotification(
                    title = "⚡ Productivity Boost",
                    description = "Your controlled usage likely improved your focus and productivity this month!",
                    type = InsightType.PRODUCTIVITY
                )
            )
        }
        
        // Wellness insight
        insights.add(
            MonthlyInsightNotification(
                title = "🧘 Digital Wellness Score",
                description = "Your mindful approach to social media is contributing to better digital wellness. Keep it up!",
                type = InsightType.WELLNESS
            )
        )
        
        return insights
    }
    
    private fun sendAchievementNotification(achievement: MonthlyAchievement) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_MONTHLY_ACHIEVEMENTS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(achievement.title)
            .setContentText(achievement.description)
            .setStyle(NotificationCompat.BigTextStyle().bigText(achievement.description))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID_MONTHLY_ACHIEVEMENT, notification)
    }
    
    private fun sendGoalProgressNotification(title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "monthly_goals")
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_MONTHLY_SUMMARY)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID_MONTHLY_GOAL, notification)
    }
    
    private fun sendInsightNotification(insight: MonthlyInsightNotification) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to", "monthly_analytics")
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_MONTHLY_INSIGHTS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(insight.title)
            .setContentText(insight.description)
            .setStyle(NotificationCompat.BigTextStyle().bigText(insight.description))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID_MONTHLY_INSIGHT, notification)
    }
    
    private fun sendMotivationNotification(title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_MONTHLY_SUMMARY)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID_MONTHLY_RECAP, notification)
    }
    
    private fun buildMonthlyRecapMessage(stats: MonthlyStats, monthName: String): String {
        return "Total: ${formatTime(stats.totalUsageMinutes)} • Daily avg: ${formatTime(stats.averageDailyMinutes)} • Goal days: ${stats.goalDays}/${stats.daysInPeriod}"
    }
    
    private fun buildDetailedMonthlyRecap(stats: MonthlyStats, monthName: String): String {
        return """
            Your $monthName Summary:
            
            📊 Total Usage: ${formatTime(stats.totalUsageMinutes)}
            📅 Daily Average: ${formatTime(stats.averageDailyMinutes)}
            🎯 Goal Days: ${stats.goalDays} out of ${stats.daysInPeriod}
            📱 Total Sessions: ${stats.totalSessions}
            ⭐ Best Day: ${formatTime(stats.bestDayMinutes)}
            
            ${if (stats.goalDays > stats.daysInPeriod * 0.7) "Great job staying consistent! 🎉" else "Keep working towards your goals! 💪"}
        """.trimIndent()
    }
    
    private fun createNotificationChannels() {
        val channels = listOf(
            NotificationChannel(
                CHANNEL_ID_MONTHLY_SUMMARY,
                "Monthly Summary",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Monthly usage summaries and recaps"
            },
            
            NotificationChannel(
                CHANNEL_ID_MONTHLY_ACHIEVEMENTS,
                "Monthly Achievements",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Celebrate your monthly accomplishments"
            },
            
            NotificationChannel(
                CHANNEL_ID_MONTHLY_INSIGHTS,
                "Monthly Insights",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Monthly insights and analytics for premium users"
            }
        )
        
        channels.forEach { channel ->
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun formatTime(minutes: Int): String {
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        
        return when {
            hours > 0 && remainingMinutes > 0 -> "${hours}h ${remainingMinutes}m"
            hours > 0 -> "${hours}h"
            else -> "${remainingMinutes}m"
        }
    }
    
    // Data classes
    private data class MonthlyStats(
        val totalUsageMinutes: Int,
        val averageDailyMinutes: Int,
        val goalDays: Int,
        val daysOverLimit: Int,
        val totalSessions: Int,
        val bestDayMinutes: Int,
        val worstDayMinutes: Int,
        val daysInPeriod: Int
    )
    
    private data class MonthlyAchievement(
        val title: String,
        val description: String,
        val type: AchievementType
    )
    
    private data class MonthlyInsightNotification(
        val title: String,
        val description: String,
        val type: InsightType
    )
    
    private enum class AchievementType {
        CONSISTENCY, IMPROVEMENT, STREAK, MILESTONE
    }
    
    private enum class InsightType {
        PATTERN, PRODUCTIVITY, WELLNESS, COMPARISON
    }
}