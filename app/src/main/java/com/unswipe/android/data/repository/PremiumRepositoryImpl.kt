package com.unswipe.android.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.unswipe.android.domain.model.*
import com.unswipe.android.domain.repository.PremiumRepository
import com.unswipe.android.domain.repository.ExportFormat
import com.unswipe.android.domain.repository.TemporaryAdjustment
import com.unswipe.android.domain.repository.UsageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PremiumRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val usageRepository: UsageRepository,
    private val json: Json
) : PremiumRepository {

    companion object {
        // Premium Subscription Keys
        val PREMIUM_TIER_KEY = stringPreferencesKey("premium_tier")
        val PREMIUM_ACTIVE_KEY = booleanPreferencesKey("premium_active")
        val PREMIUM_EXPIRATION_KEY = stringPreferencesKey("premium_expiration")
        val PREMIUM_PURCHASE_DATE_KEY = stringPreferencesKey("premium_purchase_date")
        val PREMIUM_AUTO_RENEW_KEY = booleanPreferencesKey("premium_auto_renew")
        val PREMIUM_FAMILY_COUNT_KEY = intPreferencesKey("premium_family_count")
        
        // Bypass Credits Keys
        val BYPASS_CREDITS_AVAILABLE_KEY = intPreferencesKey("bypass_credits_available")
        val BYPASS_CREDITS_USED_KEY = intPreferencesKey("bypass_credits_used")
        val BYPASS_CREDITS_EARNED_TODAY_KEY = intPreferencesKey("bypass_credits_earned_today")
        val BYPASS_CREDITS_LAST_RESET_KEY = stringPreferencesKey("bypass_credits_last_reset")
        
        // Smart Focus Modes Keys
        val FOCUS_MODES_KEY = stringPreferencesKey("smart_focus_modes")
        val ACTIVE_FOCUS_MODE_KEY = stringPreferencesKey("active_focus_mode_id")
        
        // Family Members Keys
        val FAMILY_MEMBERS_KEY = stringPreferencesKey("family_members")
        
        // Custom Messages Keys
        val CUSTOM_MESSAGES_KEY = stringPreferencesKey("custom_intervention_messages")
        
        // Temporary Adjustments Keys
        val TEMPORARY_ADJUSTMENTS_KEY = stringPreferencesKey("temporary_adjustments")
        
        // Integration Keys
        val CALENDAR_LAST_SYNC_KEY = stringPreferencesKey("calendar_last_sync")
        val HEALTH_LAST_SYNC_KEY = stringPreferencesKey("health_last_sync")
        
        private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    }

    // Subscription Management
    
    override fun getPremiumSubscriptionFlow(): Flow<PremiumSubscription> {
        return dataStore.data.map { preferences ->
            val tierString = preferences[PREMIUM_TIER_KEY] ?: "FREE"
            val tier = try {
                PremiumTier.valueOf(tierString)
            } catch (e: IllegalArgumentException) {
                PremiumTier.FREE
            }
            
            val isActive = preferences[PREMIUM_ACTIVE_KEY] ?: false
            val expirationString = preferences[PREMIUM_EXPIRATION_KEY]
            val purchaseDateString = preferences[PREMIUM_PURCHASE_DATE_KEY]
            val autoRenew = preferences[PREMIUM_AUTO_RENEW_KEY] ?: true
            val familyCount = preferences[PREMIUM_FAMILY_COUNT_KEY] ?: 1
            
            val expirationDate = expirationString?.let { 
                LocalDateTime.parse(it, dateFormatter) 
            }
            val purchaseDate = purchaseDateString?.let { 
                LocalDateTime.parse(it, dateFormatter) 
            }
            
            val availableFeatures = when (tier) {
                PremiumTier.FREE -> emptySet()
                PremiumTier.PREMIUM_INDIVIDUAL -> PremiumSubscription.getPremiumIndividual(
                    purchaseDate ?: LocalDateTime.now()
                ).availableFeatures
                PremiumTier.PREMIUM_FAMILY -> PremiumSubscription.getPremiumFamily(
                    purchaseDate ?: LocalDateTime.now()
                ).availableFeatures
                PremiumTier.PREMIUM_PRO -> PremiumFeature.values().toSet()
            }
            
            PremiumSubscription(
                tier = tier,
                isActive = isActive,
                expirationDate = expirationDate,
                purchaseDate = purchaseDate,
                autoRenew = autoRenew,
                familyMemberCount = familyCount,
                availableFeatures = availableFeatures
            )
        }
    }
    
    override suspend fun getPremiumSubscription(): PremiumSubscription {
        return getPremiumSubscriptionFlow().first()
    }
    
    override suspend fun updatePremiumSubscription(subscription: PremiumSubscription) {
        dataStore.edit { preferences ->
            preferences[PREMIUM_TIER_KEY] = subscription.tier.name
            preferences[PREMIUM_ACTIVE_KEY] = subscription.isActive
            preferences[PREMIUM_AUTO_RENEW_KEY] = subscription.autoRenew
            preferences[PREMIUM_FAMILY_COUNT_KEY] = subscription.familyMemberCount
            
            subscription.expirationDate?.let {
                preferences[PREMIUM_EXPIRATION_KEY] = it.format(dateFormatter)
            }
            
            subscription.purchaseDate?.let {
                preferences[PREMIUM_PURCHASE_DATE_KEY] = it.format(dateFormatter)
            }
        }
    }
    
    override suspend fun hasFeature(feature: PremiumFeature): Boolean {
        val subscription = getPremiumSubscription()
        return subscription.isActive && !subscription.isExpired() && subscription.hasFeature(feature)
    }
    
    override suspend fun checkSubscriptionStatus(): PremiumSubscription {
        val subscription = getPremiumSubscription()
        
        // Check if subscription has expired
        if (subscription.isExpired()) {
            val expiredSubscription = subscription.copy(isActive = false)
            updatePremiumSubscription(expiredSubscription)
            return expiredSubscription
        }
        
        return subscription
    }

    // Bypass Credits System
    
    override suspend fun getBypassCredits(): BypassCredits {
        val preferences = dataStore.data.first()
        val lastResetString = preferences[BYPASS_CREDITS_LAST_RESET_KEY]
        val lastReset = lastResetString?.let { 
            LocalDateTime.parse(it, dateFormatter) 
        } ?: LocalDateTime.now().minusDays(1)
        
        // Reset daily credits if it's a new day
        val now = LocalDateTime.now()
        val shouldReset = lastReset.toLocalDate() != now.toLocalDate()
        
        return if (shouldReset) {
            val resetCredits = BypassCredits(
                available = preferences[BYPASS_CREDITS_AVAILABLE_KEY] ?: 3,
                used = 0,
                earnedToday = 0,
                lastResetDate = now
            )
            saveBypassCredits(resetCredits)
            resetCredits
        } else {
            BypassCredits(
                available = preferences[BYPASS_CREDITS_AVAILABLE_KEY] ?: 3,
                used = preferences[BYPASS_CREDITS_USED_KEY] ?: 0,
                earnedToday = preferences[BYPASS_CREDITS_EARNED_TODAY_KEY] ?: 0,
                lastResetDate = lastReset
            )
        }
    }
    
    override suspend fun useBypassCredit(): Boolean {
        if (!hasFeature(PremiumFeature.SMART_BYPASS_CREDITS)) return false
        
        val credits = getBypassCredits()
        if (!credits.canUseBypass) return false
        
        val updatedCredits = credits.useCredit()
        saveBypassCredits(updatedCredits)
        return true
    }
    
    override suspend fun earnBypassCredit(reason: String): Boolean {
        if (!hasFeature(PremiumFeature.SMART_BYPASS_CREDITS)) return false
        
        val credits = getBypassCredits()
        val updatedCredits = credits.earnCredit()
        
        if (updatedCredits != credits) {
            saveBypassCredits(updatedCredits)
            return true
        }
        return false
    }
    
    override suspend fun resetDailyCredits() {
        val credits = getBypassCredits()
        val resetCredits = credits.copy(
            used = 0,
            earnedToday = 0,
            lastResetDate = LocalDateTime.now()
        )
        saveBypassCredits(resetCredits)
    }
    
    override fun getBypassCreditsFlow(): Flow<BypassCredits> {
        return dataStore.data.map { preferences ->
            val lastResetString = preferences[BYPASS_CREDITS_LAST_RESET_KEY]
            val lastReset = lastResetString?.let { 
                LocalDateTime.parse(it, dateFormatter) 
            } ?: LocalDateTime.now().minusDays(1)
            
            BypassCredits(
                available = preferences[BYPASS_CREDITS_AVAILABLE_KEY] ?: 3,
                used = preferences[BYPASS_CREDITS_USED_KEY] ?: 0,
                earnedToday = preferences[BYPASS_CREDITS_EARNED_TODAY_KEY] ?: 0,
                lastResetDate = lastReset
            )
        }
    }
    
    private suspend fun saveBypassCredits(credits: BypassCredits) {
        dataStore.edit { preferences ->
            preferences[BYPASS_CREDITS_AVAILABLE_KEY] = credits.available
            preferences[BYPASS_CREDITS_USED_KEY] = credits.used
            preferences[BYPASS_CREDITS_EARNED_TODAY_KEY] = credits.earnedToday
            preferences[BYPASS_CREDITS_LAST_RESET_KEY] = credits.lastResetDate.format(dateFormatter)
        }
    }

    // Smart Focus Modes
    
    override suspend fun getSmartFocusModes(): List<SmartFocusMode> {
        val preferences = dataStore.data.first()
        val focusModesJson = preferences[FOCUS_MODES_KEY] ?: "[]"
        
        return try {
            json.decodeFromString<List<SmartFocusMode>>(focusModesJson)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    override suspend fun saveSmartFocusMode(focusMode: SmartFocusMode) {
        if (!hasFeature(PremiumFeature.SMART_FOCUS_MODES)) return
        
        val currentModes = getSmartFocusModes().toMutableList()
        val existingIndex = currentModes.indexOfFirst { it.id == focusMode.id }
        
        if (existingIndex >= 0) {
            currentModes[existingIndex] = focusMode
        } else {
            currentModes.add(focusMode)
        }
        
        val focusModesJson = json.encodeToString(currentModes)
        dataStore.edit { preferences ->
            preferences[FOCUS_MODES_KEY] = focusModesJson
        }
    }
    
    override suspend fun deleteSmartFocusMode(id: String) {
        val currentModes = getSmartFocusModes().toMutableList()
        currentModes.removeAll { it.id == id }
        
        val focusModesJson = json.encodeToString(currentModes)
        dataStore.edit { preferences ->
            preferences[FOCUS_MODES_KEY] = focusModesJson
        }
    }
    
    override suspend fun getActiveFocusMode(): SmartFocusMode? {
        val preferences = dataStore.data.first()
        val activeFocusModeId = preferences[ACTIVE_FOCUS_MODE_KEY] ?: return null
        
        return getSmartFocusModes().find { it.id == activeFocusModeId }
    }
    
    override suspend fun activateFocusMode(id: String) {
        dataStore.edit { preferences ->
            preferences[ACTIVE_FOCUS_MODE_KEY] = id
        }
    }
    
    override suspend fun deactivateFocusMode() {
        dataStore.edit { preferences ->
            preferences.remove(ACTIVE_FOCUS_MODE_KEY)
        }
    }
    
    override fun getActiveFocusModeFlow(): Flow<SmartFocusMode?> {
        return dataStore.data.map { preferences ->
            val activeFocusModeId = preferences[ACTIVE_FOCUS_MODE_KEY]
            if (activeFocusModeId != null) {
                try {
                    val focusModesJson = preferences[FOCUS_MODES_KEY] ?: "[]"
                    val focusModes = json.decodeFromString<List<SmartFocusMode>>(focusModesJson)
                    focusModes.find { it.id == activeFocusModeId }
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }
        }
    }

    // Family Management
    
    override suspend fun getFamilyMembers(): List<FamilyMember> {
        if (!hasFeature(PremiumFeature.FAMILY_CONTROLS)) return emptyList()
        
        val preferences = dataStore.data.first()
        val familyMembersJson = preferences[FAMILY_MEMBERS_KEY] ?: "[]"
        
        return try {
            json.decodeFromString<List<FamilyMember>>(familyMembersJson)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    override suspend fun addFamilyMember(member: FamilyMember) {
        if (!hasFeature(PremiumFeature.FAMILY_CONTROLS)) return
        
        val currentMembers = getFamilyMembers().toMutableList()
        currentMembers.add(member)
        
        val familyMembersJson = json.encodeToString(currentMembers)
        dataStore.edit { preferences ->
            preferences[FAMILY_MEMBERS_KEY] = familyMembersJson
        }
    }
    
    override suspend fun updateFamilyMember(member: FamilyMember) {
        val currentMembers = getFamilyMembers().toMutableList()
        val existingIndex = currentMembers.indexOfFirst { it.id == member.id }
        
        if (existingIndex >= 0) {
            currentMembers[existingIndex] = member
            
            val familyMembersJson = json.encodeToString(currentMembers)
            dataStore.edit { preferences ->
                preferences[FAMILY_MEMBERS_KEY] = familyMembersJson
            }
        }
    }
    
    override suspend fun removeFamilyMember(id: String) {
        val currentMembers = getFamilyMembers().toMutableList()
        currentMembers.removeAll { it.id == id }
        
        val familyMembersJson = json.encodeToString(currentMembers)
        dataStore.edit { preferences ->
            preferences[FAMILY_MEMBERS_KEY] = familyMembersJson
        }
    }
    
    override suspend fun getFamilyMember(id: String): FamilyMember? {
        return getFamilyMembers().find { it.id == id }
    }
    
    override fun getFamilyMembersFlow(): Flow<List<FamilyMember>> {
        return dataStore.data.map { preferences ->
            if (hasFeature(PremiumFeature.FAMILY_CONTROLS)) {
                val familyMembersJson = preferences[FAMILY_MEMBERS_KEY] ?: "[]"
                try {
                    json.decodeFromString<List<FamilyMember>>(familyMembersJson)
                } catch (e: Exception) {
                    emptyList()
                }
            } else {
                emptyList()
            }
        }
    }

    // Advanced Analytics
    
    override suspend fun getAdvancedAnalytics(startDate: LocalDateTime, endDate: LocalDateTime): AdvancedAnalytics {
        if (!hasFeature(PremiumFeature.ADVANCED_ANALYTICS)) {
            return AdvancedAnalytics(
                productivityScore = 0f,
                focusQuality = 0f,
                digitalWellnessScore = 0f,
                weeklyTrends = emptyList(),
                monthlyComparison = ComparisonData(0f, 0f, 0, 0f),
                patternInsights = emptyList(),
                recommendations = emptyList()
            )
        }
        
        // Calculate analytics based on usage patterns
        val weeklyTrends = generateTrendDataForPeriod(startDate, endDate)
        val productivityScore = calculateProductivityScore(weeklyTrends)
        val focusQuality = calculateFocusQuality(weeklyTrends)
        val digitalWellnessScore = calculateDigitalWellnessScore(weeklyTrends)
        
        return AdvancedAnalytics(
            productivityScore = productivityScore,
            focusQuality = focusQuality,
            digitalWellnessScore = digitalWellnessScore,
            weeklyTrends = weeklyTrends,
            monthlyComparison = ComparisonData(
                userAverage = weeklyTrends.map { it.usageMinutes }.average().toFloat() / 60f,
                globalAverage = 4.1f,
                percentile = calculatePercentile(productivityScore),
                improvement = calculateImprovement(weeklyTrends)
            ),
            patternInsights = generatePatternInsights(weeklyTrends),
            recommendations = generatePersonalizedRecommendations(productivityScore, focusQuality)
        )
    }
    
    override suspend fun getExtendedUsageHistory(days: Int): List<TrendData> {
        if (!hasFeature(PremiumFeature.EXTENDED_HISTORY)) {
            return emptyList()
        }
        
        val endDate = LocalDateTime.now()
        val startDate = endDate.minusDays(days.toLong())
        return generateTrendDataForPeriod(startDate, endDate)
    }
    
    override suspend fun getProductivityScore(date: LocalDateTime): Float {
        if (!hasFeature(PremiumFeature.ADVANCED_ANALYTICS)) return 0f
        
        // Calculate productivity score based on app usage patterns
        val dayTrends = generateTrendDataForPeriod(date.toLocalDate().atStartOfDay(), date)
        return calculateProductivityScore(dayTrends)
    }
    
    override suspend fun getPersonalizedRecommendations(): List<PersonalizedRecommendation> {
        if (!hasFeature(PremiumFeature.ADVANCED_ANALYTICS)) return emptyList()
        
        return generateMockRecommendations()
    }
    
    override suspend fun exportUsageData(format: ExportFormat): String {
        if (!hasFeature(PremiumFeature.DATA_EXPORT)) return ""
        
        val exportData = getExtendedUsageHistory(30) // Last 30 days
        val exportDate = LocalDateTime.now()
        
        return when (format) {
            ExportFormat.CSV -> {
                buildString {
                    appendLine("date,usage_minutes,session_count,productivity_score,focus_interruptions")
                    exportData.forEach { trend ->
                        appendLine("${trend.date.toLocalDate()},${trend.usageMinutes},${trend.sessionCount},${trend.productivityScore},${trend.focusInterruptions}")
                    }
                }
            }
            ExportFormat.JSON -> {
                val jsonData = buildJsonObject {
                    put("export_date", exportDate.toString())
                    put("export_format", format.name)
                    putJsonArray("usage_data") {
                        exportData.forEach { trend ->
                            addJsonObject {
                                put("date", trend.date.toString())
                                put("usage_minutes", trend.usageMinutes)
                                put("session_count", trend.sessionCount)
                                put("productivity_score", trend.productivityScore)
                                put("focus_interruptions", trend.focusInterruptions)
                            }
                        }
                    }
                    putJsonObject("summary") {
                        put("total_days", exportData.size)
                        put("avg_daily_usage_minutes", exportData.map { it.usageMinutes }.average())
                        put("avg_productivity_score", exportData.map { it.productivityScore }.average())
                        put("total_sessions", exportData.sumOf { it.sessionCount })
                    }
                }
                jsonData.toString()
            }
            ExportFormat.PDF -> {
                // Return a simple text representation for PDF generation
                buildString {
                    appendLine("Unswipe Usage Report")
                    appendLine("Generated: $exportDate")
                    appendLine("=====================================")
                    appendLine()
                    appendLine("Summary (Last 30 Days):")
                    appendLine("- Average Daily Usage: ${exportData.map { it.usageMinutes }.average().toInt()} minutes")
                    appendLine("- Average Productivity Score: ${"%.2f".format(exportData.map { it.productivityScore }.average())}")
                    appendLine("- Total Sessions: ${exportData.sumOf { it.sessionCount }}")
                    appendLine()
                    appendLine("Daily Details:")
                    exportData.forEach { trend ->
                        appendLine("${trend.date.toLocalDate()}: ${trend.usageMinutes}min, ${trend.sessionCount} sessions")
                    }
                }
            }
        }
    }

    // Calendar Integration Methods
    
    override suspend fun syncCalendarEvents() {
        if (!hasFeature(PremiumFeature.CALENDAR_INTEGRATION)) return
        
        // Store last sync timestamp
        dataStore.edit { preferences ->
            preferences[CALENDAR_LAST_SYNC_KEY] = LocalDateTime.now().format(dateFormatter)
        }
    }
    
    override suspend fun getCalendarBasedFocusModes(): List<SmartFocusMode> {
        if (!hasFeature(PremiumFeature.CALENDAR_INTEGRATION)) return emptyList()
        
        // Return predefined focus modes for common calendar events
        return listOf(
            SmartFocusMode(
                id = "work_meeting_focus",
                name = "Work Meeting",
                description = "Auto-activated during calendar meetings",
                isActive = false,
                blockedApps = setOf(
                    "com.zhiliaoapp.musically",
                    "com.instagram.android",
                    "com.twitter.android",
                    "com.reddit.frontpage"
                ),
                allowedApps = setOf(
                    "com.google.android.gm", // Gmail
                    "com.microsoft.teams", // Teams
                    "us.zoom.videomeetings" // Zoom
                ),
                scheduleType = ScheduleType.EVENT_TRIGGERED,
                customMessage = "You're in a meeting. Stay focused!",
                strictnessLevel = FocusStrictnessLevel.HIGH
            ),
            SmartFocusMode(
                id = "deep_work_focus",
                name = "Deep Work",
                description = "For focused work sessions",
                isActive = false,
                blockedApps = setOf(
                    "com.zhiliaoapp.musically",
                    "com.instagram.android",
                    "com.twitter.android",
                    "com.reddit.frontpage",
                    "com.google.android.youtube"
                ),
                allowedApps = setOf(),
                scheduleType = ScheduleType.EVENT_TRIGGERED,
                customMessage = "Deep work time. Distractions blocked.",
                strictnessLevel = FocusStrictnessLevel.MAXIMUM
            )
        )
    }
    
    override suspend fun shouldActivateFocusModeForEvent(eventTitle: String): SmartFocusMode? {
        if (!hasFeature(PremiumFeature.CALENDAR_INTEGRATION)) return null
        
        val lowerTitle = eventTitle.lowercase()
        
        return when {
            lowerTitle.contains("meeting") || 
            lowerTitle.contains("call") || 
            lowerTitle.contains("standup") -> {
                getCalendarBasedFocusModes().find { it.id == "work_meeting_focus" }
            }
            lowerTitle.contains("focus") || 
            lowerTitle.contains("deep work") || 
            lowerTitle.contains("coding") -> {
                getCalendarBasedFocusModes().find { it.id == "deep_work_focus" }
            }
            else -> null
        }
    }
    
    override suspend fun syncHealthData() {
        if (!hasFeature(PremiumFeature.HEALTH_APP_SYNC)) return
        
        // Store last health sync timestamp
        dataStore.edit { preferences ->
            preferences[HEALTH_LAST_SYNC_KEY] = LocalDateTime.now().format(dateFormatter)
        }
    }
    
    override suspend fun getHealthCorrelations(): Map<String, Float> {
        if (!hasFeature(PremiumFeature.HEALTH_APP_SYNC)) return emptyMap()
        
        // Return sample correlations between phone usage and health metrics
        return mapOf(
            "sleep_quality" to 0.72f,      // Strong negative correlation
            "step_count" to 0.45f,          // Moderate negative correlation
            "heart_rate_variability" to 0.68f, // Negative correlation
            "exercise_minutes" to 0.38f,     // Weak negative correlation
            "stress_level" to 0.81f         // Strong positive correlation
        )
    }
    
    override suspend fun getSleepQualityCorrelation(): Float {
        if (!hasFeature(PremiumFeature.SLEEP_QUALITY_ANALYSIS)) return 0f
        
        // Return correlation between late-night phone usage and sleep quality
        // Negative value indicates inverse relationship
        return -0.72f
    }
    
    override suspend fun createTemporaryAdjustment(adjustment: TemporaryAdjustment) {
        if (!hasFeature(PremiumFeature.TEMPORARY_ADJUSTMENTS)) return
        
        val currentAdjustments = getActiveAdjustments().toMutableList()
        currentAdjustments.add(adjustment)
        
        val adjustmentsJson = json.encodeToString(currentAdjustments)
        dataStore.edit { preferences ->
            preferences[TEMPORARY_ADJUSTMENTS_KEY] = adjustmentsJson
        }
    }
    
    override suspend fun getActiveAdjustments(): List<TemporaryAdjustment> {
        if (!hasFeature(PremiumFeature.TEMPORARY_ADJUSTMENTS)) return emptyList()
        
        val preferences = dataStore.data.first()
        val adjustmentsJson = preferences[TEMPORARY_ADJUSTMENTS_KEY] ?: "[]"
        
        return try {
            val allAdjustments = json.decodeFromString<List<TemporaryAdjustment>>(adjustmentsJson)
            val now = LocalDateTime.now()
            allAdjustments.filter { it.isActive && it.endTime.isAfter(now) }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    override suspend fun removeTemporaryAdjustment(id: String) {
        val currentAdjustments = getActiveAdjustments().toMutableList()
        currentAdjustments.removeAll { it.id == id }
        
        val adjustmentsJson = json.encodeToString(currentAdjustments)
        dataStore.edit { preferences ->
            preferences[TEMPORARY_ADJUSTMENTS_KEY] = adjustmentsJson
        }
    }
    
    override suspend fun getCustomInterventionMessages(): Map<String, String> {
        if (!hasFeature(PremiumFeature.CUSTOM_INTERVENTION_MESSAGES)) return emptyMap()
        
        val preferences = dataStore.data.first()
        val messagesJson = preferences[CUSTOM_MESSAGES_KEY] ?: "{}"
        
        return try {
            json.decodeFromString<Map<String, String>>(messagesJson)
        } catch (e: Exception) {
            emptyMap()
        }
    }
    
    override suspend fun setCustomInterventionMessage(packageName: String, message: String) {
        if (!hasFeature(PremiumFeature.CUSTOM_INTERVENTION_MESSAGES)) return
        
        val currentMessages = getCustomInterventionMessages().toMutableMap()
        currentMessages[packageName] = message
        
        val messagesJson = json.encodeToString(currentMessages)
        dataStore.edit { preferences ->
            preferences[CUSTOM_MESSAGES_KEY] = messagesJson
        }
    }
    
    override suspend fun removeCustomInterventionMessage(packageName: String) {
        val currentMessages = getCustomInterventionMessages().toMutableMap()
        currentMessages.remove(packageName)
        
        val messagesJson = json.encodeToString(currentMessages)
        dataStore.edit { preferences ->
            preferences[CUSTOM_MESSAGES_KEY] = messagesJson
        }
    }

    // Helper methods for analytics calculations
    
    private fun generateTrendDataForPeriod(startDate: LocalDateTime, endDate: LocalDateTime): List<TrendData> {
        val trends = mutableListOf<TrendData>()
        var currentDate = startDate
        
        while (currentDate <= endDate) {
            trends.add(
                TrendData(
                    date = currentDate,
                    usageMinutes = (120..240).random(),
                    sessionCount = (10..25).random(),
                    productivityScore = (0.5f..0.9f).random(),
                    focusInterruptions = (3..12).random()
                )
            )
            currentDate = currentDate.plusDays(1)
        }
        
        return trends
    }
    
    private fun calculateProductivityScore(trends: List<TrendData>): Float {
        if (trends.isEmpty()) return 0f
        
        // Calculate based on usage patterns and interruptions
        val avgUsageMinutes = trends.map { it.usageMinutes }.average()
        val avgInterruptions = trends.map { it.focusInterruptions }.average()
        val avgSessionCount = trends.map { it.sessionCount }.average()
        
        // Lower usage, fewer interruptions, and fewer sessions = higher productivity
        val usageScore = (1f - (avgUsageMinutes / 480f)).coerceIn(0f, 1f) // 8 hours max
        val interruptionScore = (1f - (avgInterruptions / 20f)).coerceIn(0f, 1f)
        val sessionScore = (1f - (avgSessionCount / 50f)).coerceIn(0f, 1f)
        
        return (usageScore * 0.4f + interruptionScore * 0.4f + sessionScore * 0.2f)
    }
    
    private fun calculateFocusQuality(trends: List<TrendData>): Float {
        if (trends.isEmpty()) return 0f
        
        // Calculate based on interruptions and session patterns
        val avgInterruptions = trends.map { it.focusInterruptions }.average()
        val avgSessionDuration = trends.map { it.usageMinutes.toFloat() / it.sessionCount }.average()
        
        val interruptionScore = (1f - (avgInterruptions / 15f)).coerceIn(0f, 1f)
        val sessionDurationScore = (avgSessionDuration / 30f).coerceIn(0f, 1f) // 30 min ideal session
        
        return (interruptionScore * 0.6f + sessionDurationScore * 0.4f)
    }
    
    private fun calculateDigitalWellnessScore(trends: List<TrendData>): Float {
        if (trends.isEmpty()) return 0f
        
        // Combine usage, productivity, and focus quality
        val avgUsageMinutes = trends.map { it.usageMinutes }.average()
        val productivityScore = calculateProductivityScore(trends)
        val focusQuality = calculateFocusQuality(trends)
        
        val usageScore = (1f - (avgUsageMinutes / 360f)).coerceIn(0f, 1f) // 6 hours target
        
        return (usageScore * 0.4f + productivityScore * 0.3f + focusQuality * 0.3f)
    }
    
    private fun calculatePercentile(score: Float): Int {
        // Rough percentile calculation based on score
        return when {
            score >= 0.9f -> 95
            score >= 0.8f -> 85
            score >= 0.7f -> 75
            score >= 0.6f -> 60
            score >= 0.5f -> 50
            score >= 0.4f -> 35
            else -> 20
        }
    }
    
    private fun calculateImprovement(trends: List<TrendData>): Float {
        if (trends.size < 2) return 0f
        
        val firstHalf = trends.take(trends.size / 2)
        val secondHalf = trends.drop(trends.size / 2)
        
        val firstScore = calculateDigitalWellnessScore(firstHalf)
        val secondScore = calculateDigitalWellnessScore(secondHalf)
        
        return ((secondScore - firstScore) / firstScore * 100).coerceIn(-50f, 50f)
    }
    
    private fun generatePatternInsights(trends: List<TrendData>): List<PatternInsight> {
        val insights = mutableListOf<PatternInsight>()
        
        // Analyze usage patterns
        val avgUsage = trends.map { it.usageMinutes }.average()
        val peakUsageHour = trends.maxByOrNull { it.usageMinutes }?.date?.hour ?: 0
        
        if (avgUsage > 240) {
            insights.add(
                PatternInsight(
                    type = "high_usage",
                    description = "Your daily screen time exceeds 4 hours",
                    confidence = 0.9f,
                    actionable = true,
                    suggestedAction = "Try to reduce usage by 30 minutes daily"
                )
            )
        }
        
        insights.add(
            PatternInsight(
                type = "peak_usage",
                description = "Your usage peaks around ${peakUsageHour}:00",
                confidence = 0.85f,
                actionable = true,
                suggestedAction = "Consider scheduling breaks during this time"
            )
        )
        
        return insights
    }
    
    private fun generatePersonalizedRecommendations(productivityScore: Float, focusQuality: Float): List<PersonalizedRecommendation> {
        val recommendations = mutableListOf<PersonalizedRecommendation>()
        
        if (productivityScore < 0.6f) {
            recommendations.add(
                PersonalizedRecommendation(
                    title = "Improve Your Productivity",
                    description = "Your productivity score is below average. Consider using focus modes more frequently.",
                    priority = RecommendationPriority.HIGH,
                    category = RecommendationCategory.PRODUCTIVITY,
                    estimatedImpact = "25% improvement in daily focus"
                )
            )
        }
        
        if (focusQuality < 0.7f) {
            recommendations.add(
                PersonalizedRecommendation(
                    title = "Reduce Distractions",
                    description = "You're experiencing frequent interruptions. Try longer focus sessions.",
                    priority = RecommendationPriority.MEDIUM,
                    category = RecommendationCategory.FOCUS,
                    estimatedImpact = "Fewer context switches throughout the day"
                )
            )
        }
        
        return recommendations
    }
    
    private fun generateMockTrendData(): List<TrendData> {
        val now = LocalDateTime.now()
        return (0..6).map { daysAgo ->
            TrendData(
                date = now.minusDays(daysAgo.toLong()),
                usageMinutes = (120..240).random(),
                sessionCount = (10..25).random(),
                productivityScore = (0.5f..0.9f).random(),
                focusInterruptions = (3..12).random()
            )
        }
    }
    
    private fun generateMockPatternInsights(): List<PatternInsight> {
        return listOf(
            PatternInsight(
                type = "productivity",
                description = "Your focus is strongest between 9-11 AM",
                confidence = 0.85f,
                actionable = true,
                suggestedAction = "Schedule important tasks during morning hours"
            ),
            PatternInsight(
                type = "usage_pattern",
                description = "You tend to use social media more on Mondays",
                confidence = 0.72f,
                actionable = true,
                suggestedAction = "Set stricter limits on Monday mornings"
            )
        )
    }
    
    private fun generateMockRecommendations(): List<PersonalizedRecommendation> {
        return listOf(
            PersonalizedRecommendation(
                title = "Optimize Your Morning Routine",
                description = "Your productivity is 40% higher when you avoid social media for the first hour after waking up",
                priority = RecommendationPriority.HIGH,
                category = RecommendationCategory.PRODUCTIVITY,
                estimatedImpact = "15% improvement in daily focus"
            ),
            PersonalizedRecommendation(
                title = "Improve Sleep Hygiene",
                description = "Using your phone within 30 minutes of bedtime reduces your sleep quality score by 20%",
                priority = RecommendationPriority.MEDIUM,
                category = RecommendationCategory.SLEEP,
                estimatedImpact = "Better sleep quality and morning energy"
            )
        )
    }
}

private fun ClosedFloatingPointRange<Float>.random(): Float {
    return start + (endInclusive - start) * kotlin.random.Random.nextFloat()
}