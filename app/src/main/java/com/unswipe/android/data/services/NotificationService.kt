package com.unswipe.android.data.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.unswipe.android.ui.MainActivity
import com.unswipe.android.R
import com.unswipe.android.domain.repository.OnboardingRepository
import com.unswipe.android.domain.repository.UsageRepository
import com.unswipe.android.domain.repository.CriticalPeriodType
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.work.HiltWorker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@Serializable
data class ChatGPTRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<ChatMessage>,
    val max_tokens: Int = 100,
    val temperature: Double = 0.7
)

@Serializable
data class ChatMessage(
    val role: String,
    val content: String
)

@Serializable
data class ChatGPTResponse(
    val choices: List<Choice>
)

@Serializable
data class Choice(
    val message: ChatMessage
)

class NotificationService @Inject constructor(
    private val context: Context,
    private val usageRepository: UsageRepository,
    private val onboardingRepository: OnboardingRepository
) {
    
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }
    
    companion object {
        private const val CHANNEL_ID = "unswipe_nudges"
        private const val CHANNEL_NAME = "Smart Nudges"
        private const val OPENAI_API_URL = "https://api.openai.com/v1/chat/completions"
        private const val MAX_NOTIFICATIONS_PER_DAY = 3
        
        // You'll need to add your OpenAI API key (consider using BuildConfig for security)
        private const val OPENAI_API_KEY = "" // OpenAI API key disabled for now - feature optional
    }

    init {
        createNotificationChannel()
    }

    suspend fun scheduleSmartNudges() {
        val workRequest = PeriodicWorkRequestBuilder<SmartNudgeWorker>(
            2, TimeUnit.HOURS // Check every 2 hours
        ).setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "smart_nudges",
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }

    suspend fun generateAndSendSmartNudge(): Boolean {
        try {
            // Check if we've already sent max notifications today
            if (hasReachedDailyLimit()) {
                return false
            }

            // Gather user context
            val userContext = gatherUserContext()
            
            // Generate personalized message using ChatGPT
            val nudgeMessage = generateNudgeMessage(userContext)
            
            if (nudgeMessage.isNotEmpty()) {
                sendNotification(nudgeMessage)
                recordNotificationSent()
                return true
            }
        } catch (e: Exception) {
            // Log error but don't crash
            println("Error generating smart nudge: ${e.message}")
        }
        return false
    }

    private suspend fun gatherUserContext(): UserContext {
        val schedule = onboardingRepository.getUserSchedule()
        val criticalPeriod = onboardingRepository.getCurrentCriticalPeriodType()
        
        // Get today's usage for main social media apps
        val instagramUsage = usageRepository.getAppUsageToday("com.instagram.android")
        val tiktokUsage = usageRepository.getAppUsageToday("com.zhiliaoapp.musically")
        val youtubeUsage = usageRepository.getAppUsageToday("com.google.android.youtube")
        
        val totalUsage = instagramUsage + tiktokUsage + youtubeUsage
        
        return UserContext(
            totalUsageToday = totalUsage,
            instagramUsage = instagramUsage,
            tiktokUsage = tiktokUsage,
            youtubeUsage = youtubeUsage,
            currentTime = LocalTime.now(),
            criticalPeriod = criticalPeriod,
            wakeupTime = schedule?.wakeupTime,
            sleepTime = schedule?.sleepTime,
            isWorkTime = criticalPeriod == CriticalPeriodType.WORK_HOURS
        )
    }

    private suspend fun generateNudgeMessage(context: UserContext): String {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = buildPrompt(context)
                val request = ChatGPTRequest(
                    messages = listOf(
                        ChatMessage("system", "You are a helpful digital wellness assistant. Generate short, friendly, and motivational notifications (max 50 words) to help users manage their social media time. Be encouraging, not preachy. Use emojis sparingly."),
                        ChatMessage("user", prompt)
                    )
                )

                val requestBody = json.encodeToString(ChatGPTRequest.serializer(), request)
                    .toRequestBody("application/json".toMediaType())

                val httpRequest = Request.Builder()
                    .url(OPENAI_API_URL)
                    .addHeader("Authorization", "Bearer $OPENAI_API_KEY")
                    .addHeader("Content-Type", "application/json")
                    .post(requestBody)
                    .build()

                val response = client.newCall(httpRequest).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        val chatResponse = json.decodeFromString(ChatGPTResponse.serializer(), responseBody)
                        return@withContext chatResponse.choices.firstOrNull()?.message?.content?.trim() ?: ""
                    }
                }
            } catch (e: Exception) {
                println("Error calling ChatGPT API: ${e.message}")
            }
            return@withContext ""
        }
    }

    private fun buildPrompt(context: UserContext): String {
        val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
        val currentTimeStr = context.currentTime.format(timeFormatter)
        
        return buildString {
            append("Generate a brief, friendly notification for a user who has spent ")
            append("${formatDuration(context.totalUsageToday)} on social media today. ")
            
            if (context.instagramUsage > 0) {
                append("Instagram: ${formatDuration(context.instagramUsage)}. ")
            }
            if (context.tiktokUsage > 0) {
                append("TikTok: ${formatDuration(context.tiktokUsage)}. ")
            }
            if (context.youtubeUsage > 0) {
                append("YouTube: ${formatDuration(context.youtubeUsage)}. ")
            }
            
            append("Current time: $currentTimeStr. ")
            
            when (context.criticalPeriod) {
                CriticalPeriodType.WORK_HOURS -> append("They should be focused on work. ")
                CriticalPeriodType.SLEEP_TIME -> append("They should be sleeping. ")
                CriticalPeriodType.EARLY_MORNING -> append("It's early morning, good time for productivity. ")
                CriticalPeriodType.LATE_EVENING -> append("It's late evening, time to wind down. ")
                null -> append("Regular time. ")
            }
            
            append("Make it encouraging and suggest a positive alternative activity.")
        }
    }

    private fun formatDuration(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        
        return when {
            hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
            hours > 0 -> "${hours}h"
            minutes > 0 -> "${minutes}m"
            else -> "< 1m"
        }
    }

    private fun sendNotification(message: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Digital Wellness Nudge 🌱")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Smart nudges to help with digital wellness"
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private suspend fun hasReachedDailyLimit(): Boolean {
        // TODO: Implement daily notification tracking using DataStore
        // For now, return false to allow notifications
        return false
    }

    private suspend fun recordNotificationSent() {
        // TODO: Record notification timestamp in DataStore
        // This will help enforce the 3-per-day limit
    }
}

data class UserContext(
    val totalUsageToday: Long,
    val instagramUsage: Long,
    val tiktokUsage: Long,
    val youtubeUsage: Long,
    val currentTime: LocalTime,
    val criticalPeriod: CriticalPeriodType?,
    val wakeupTime: LocalTime?,
    val sleepTime: LocalTime?,
    val isWorkTime: Boolean
)

@HiltWorker
class SmartNudgeWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val notificationService: NotificationService
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val success = notificationService.generateAndSendSmartNudge()
            if (success) Result.success() else Result.retry()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}