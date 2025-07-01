package com.unswipe.android.domain.repository

import com.unswipe.android.domain.model.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface PremiumRepository {
    
    // Subscription Management
    fun getPremiumSubscriptionFlow(): Flow<PremiumSubscription>
    suspend fun getPremiumSubscription(): PremiumSubscription
    suspend fun updatePremiumSubscription(subscription: PremiumSubscription)
    suspend fun hasFeature(feature: PremiumFeature): Boolean
    suspend fun checkSubscriptionStatus(): PremiumSubscription
    
    // Bypass Credits System
    suspend fun getBypassCredits(): BypassCredits
    suspend fun useBypassCredit(): Boolean
    suspend fun earnBypassCredit(reason: String): Boolean
    suspend fun resetDailyCredits()
    fun getBypassCreditsFlow(): Flow<BypassCredits>
    
    // Smart Focus Modes
    suspend fun getSmartFocusModes(): List<SmartFocusMode>
    suspend fun saveSmartFocusMode(focusMode: SmartFocusMode)
    suspend fun deleteSmartFocusMode(id: String)
    suspend fun getActiveFocusMode(): SmartFocusMode?
    suspend fun activateFocusMode(id: String)
    suspend fun deactivateFocusMode()
    fun getActiveFocusModeFlow(): Flow<SmartFocusMode?>
    
    // Family Management
    suspend fun getFamilyMembers(): List<FamilyMember>
    suspend fun addFamilyMember(member: FamilyMember)
    suspend fun updateFamilyMember(member: FamilyMember)
    suspend fun removeFamilyMember(id: String)
    suspend fun getFamilyMember(id: String): FamilyMember?
    fun getFamilyMembersFlow(): Flow<List<FamilyMember>>
    
    // Advanced Analytics
    suspend fun getAdvancedAnalytics(startDate: LocalDateTime, endDate: LocalDateTime): AdvancedAnalytics
    suspend fun getExtendedUsageHistory(days: Int): List<TrendData>
    suspend fun getProductivityScore(date: LocalDateTime): Float
    suspend fun getPersonalizedRecommendations(): List<PersonalizedRecommendation>
    suspend fun exportUsageData(format: ExportFormat): String
    
    // Calendar Integration
    suspend fun syncCalendarEvents()
    suspend fun getCalendarBasedFocusModes(): List<SmartFocusMode>
    suspend fun shouldActivateFocusModeForEvent(eventTitle: String): SmartFocusMode?
    
    // Health Integration
    suspend fun syncHealthData()
    suspend fun getHealthCorrelations(): Map<String, Float>
    suspend fun getSleepQualityCorrelation(): Float
    
    // Temporary Adjustments
    suspend fun createTemporaryAdjustment(adjustment: TemporaryAdjustment)
    suspend fun getActiveAdjustments(): List<TemporaryAdjustment>
    suspend fun removeTemporaryAdjustment(id: String)
    
    // Custom Messages
    suspend fun getCustomInterventionMessages(): Map<String, String>
    suspend fun setCustomInterventionMessage(packageName: String, message: String)
    suspend fun removeCustomInterventionMessage(packageName: String)
}

enum class ExportFormat {
    CSV, PDF, JSON
}

data class TemporaryAdjustment(
    val id: String,
    val name: String,
    val description: String,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val adjustments: Map<String, Any>, // Flexible adjustments
    val isActive: Boolean = true
)