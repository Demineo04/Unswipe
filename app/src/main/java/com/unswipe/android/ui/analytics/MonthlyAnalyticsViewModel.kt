package com.unswipe.android.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unswipe.android.domain.repository.UsageRepository
import com.unswipe.android.domain.repository.SettingsRepository
import com.unswipe.android.domain.repository.PremiumRepository
import com.unswipe.android.domain.model.PremiumFeature
import com.unswipe.android.ui.components.MonthlyUsageSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class MonthlyAnalyticsViewModel @Inject constructor(
    private val usageRepository: UsageRepository,
    private val settingsRepository: SettingsRepository,
    private val premiumRepository: PremiumRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(MonthlyAnalyticsUiState(isLoading = true))
    val uiState: StateFlow<MonthlyAnalyticsUiState> = _uiState.asStateFlow()
    
    private var currentMonth: LocalDate = LocalDate.now().withDayOfMonth(1)
    
    init {
        loadMonthlyData()
    }
    
    fun selectDay(day: MonthlyUsageSummary) {
        // Handle day selection - could navigate to day detail or show popup
        // For now, just update UI state to highlight selected day
        viewModelScope.launch {
            // Implementation for day selection
        }
    }
    
    fun changeMonth(newMonth: LocalDate) {
        currentMonth = newMonth.withDayOfMonth(1)
        loadMonthlyData()
    }
    
    private fun loadMonthlyData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                // Check if user has premium features for advanced analytics
                val hasAdvancedAnalytics = premiumRepository.hasFeature(PremiumFeature.ADVANCED_ANALYTICS)
                val hasTrendAnalysis = premiumRepository.hasFeature(PremiumFeature.TREND_ANALYSIS)
                
                // Calculate month boundaries
                val monthStart = currentMonth
                val monthEnd = currentMonth.plusMonths(1).minusDays(1)
                val today = LocalDate.now()
                val daysInMonth = ChronoUnit.DAYS.between(monthStart, monthEnd) + 1
                val daysCompleted = if (today.isBefore(monthEnd)) {
                    ChronoUnit.DAYS.between(monthStart, today) + 1
                } else {
                    daysInMonth
                }.toInt()
                
                // Get usage data for current month
                val monthlyData = generateMonthlyUsageData(monthStart, monthEnd, today)
                
                // Calculate current month statistics
                val totalUsageMinutes = monthlyData.sumOf { it.usageMinutes }
                val dailyAverageMinutes = if (daysCompleted > 0) totalUsageMinutes / daysCompleted else 0
                val bestDayMinutes = monthlyData.minOfOrNull { it.usageMinutes } ?: 0
                val totalSessions = monthlyData.sumOf { it.sessionCount }
                val goalDays = monthlyData.count { it.usagePercentage <= 1.0f }
                val daysOverLimit = monthlyData.count { it.usagePercentage > 1.0f }
                
                // Get daily limit for goal calculations
                val dailyLimitMillis = settingsRepository.getDailyLimitFlow().first()
                val monthlyGoalMinutes = (dailyLimitMillis / (1000 * 60)) * daysInMonth
                val goalProgress = totalUsageMinutes.toFloat() / monthlyGoalMinutes.toFloat()
                
                // Generate insights
                val insights = generateMonthlyInsights(
                    monthlyData = monthlyData,
                    totalUsage = totalUsageMinutes,
                    goalDays = goalDays,
                    daysOverLimit = daysOverLimit,
                    hasAdvancedAnalytics = hasAdvancedAnalytics
                )
                
                // Get previous month data for comparison (if premium)
                val (previousMonthData, comparisonData) = if (hasTrendAnalysis) {
                    val prevMonth = currentMonth.minusMonths(1)
                    val prevMonthEnd = currentMonth.minusDays(1)
                    val prevDaysInMonth = ChronoUnit.DAYS.between(prevMonth, prevMonthEnd) + 1
                    
                    val prevData = generateMonthlyUsageData(prevMonth, prevMonthEnd, prevMonthEnd)
                    val prevTotalUsage = prevData.sumOf { it.usageMinutes }
                    val prevDailyAverage = if (prevDaysInMonth > 0) prevTotalUsage / prevDaysInMonth.toInt() else 0
                    val prevBestDay = prevData.minOfOrNull { it.usageMinutes } ?: 0
                    val prevTotalSessions = prevData.sumOf { it.sessionCount }
                    val prevGoalDays = prevData.count { it.usagePercentage <= 1.0f }
                    val prevDaysOverLimit = prevData.count { it.usagePercentage > 1.0f }
                    
                    // Calculate percentage changes
                    val totalUsageChange = calculatePercentageChange(prevTotalUsage, totalUsageMinutes)
                    val dailyAverageChange = calculatePercentageChange(prevDailyAverage, dailyAverageMinutes)
                    val bestDayChange = calculatePercentageChange(prevBestDay, bestDayMinutes)
                    val sessionsChange = calculatePercentageChange(prevTotalSessions, totalSessions)
                    val goalDaysChange = calculatePercentageChange(prevGoalDays, goalDays)
                    val daysOverLimitChange = calculatePercentageChange(prevDaysOverLimit, daysOverLimit)
                    
                    Pair(
                        prevData,
                        ComparisonData(
                            previousMonthTotal = formatHoursMinutes(prevTotalUsage),
                            previousDailyAverage = formatHoursMinutes(prevDailyAverage),
                            previousBestDay = formatHoursMinutes(prevBestDay),
                            previousTotalSessions = prevTotalSessions,
                            previousGoalDays = prevGoalDays,
                            previousDaysCompleted = prevDaysInMonth.toInt(),
                            previousDaysOverLimit = prevDaysOverLimit,
                            totalUsageChange = totalUsageChange,
                            dailyAverageChange = dailyAverageChange,
                            bestDayChange = bestDayChange,
                            sessionsChange = sessionsChange,
                            goalDaysChange = goalDaysChange,
                            daysOverLimitChange = daysOverLimitChange
                        )
                    )
                } else {
                    Pair(emptyList(), ComparisonData())
                }
                
                _uiState.value = MonthlyAnalyticsUiState(
                    isLoading = false,
                    currentMonth = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                    daysInMonth = daysInMonth.toInt(),
                    daysCompleted = daysCompleted,
                    monthlyTotal = formatHoursMinutes(totalUsageMinutes),
                    dailyAverage = formatHoursMinutes(dailyAverageMinutes),
                    bestDay = formatHoursMinutes(bestDayMinutes),
                    totalSessions = totalSessions,
                    goalDays = goalDays,
                    daysOverLimit = daysOverLimit,
                    monthlyData = monthlyData,
                    insights = insights,
                    hasComparisonData = hasTrendAnalysis,
                    previousMonthTotal = comparisonData.previousMonthTotal,
                    previousDailyAverage = comparisonData.previousDailyAverage,
                    previousBestDay = comparisonData.previousBestDay,
                    previousTotalSessions = comparisonData.previousTotalSessions,
                    previousGoalDays = comparisonData.previousGoalDays,
                    previousDaysCompleted = comparisonData.previousDaysCompleted,
                    previousDaysOverLimit = comparisonData.previousDaysOverLimit,
                    totalUsageChange = comparisonData.totalUsageChange,
                    dailyAverageChange = comparisonData.dailyAverageChange,
                    bestDayChange = comparisonData.bestDayChange,
                    sessionsChange = comparisonData.sessionsChange,
                    goalDaysChange = comparisonData.goalDaysChange,
                    daysOverLimitChange = comparisonData.daysOverLimitChange,
                    monthlyGoal = formatHoursMinutes(monthlyGoalMinutes.toInt()),
                    goalProgress = goalProgress
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Unknown error occurred"
                )
            }
        }
    }
    
    private suspend fun generateMonthlyUsageData(
        monthStart: LocalDate,
        monthEnd: LocalDate,
        today: LocalDate
    ): List<MonthlyUsageSummary> {
        val dailyLimitMillis = settingsRepository.getDailyLimitFlow().first()
        val dailyLimitMinutes = (dailyLimitMillis / (1000 * 60)).toInt()
        
        val usageData = mutableListOf<MonthlyUsageSummary>()
        var currentDate = monthStart
        
        while (!currentDate.isAfter(monthEnd)) {
            val isToday = currentDate == today
            val isFutureDate = currentDate.isAfter(today)
            
            // For demo purposes, generate realistic usage data
            // In real implementation, this would fetch from UsageRepository
            val usageMinutes = if (isFutureDate) {
                0
            } else {
                generateRealisticUsageMinutes(currentDate, dailyLimitMinutes)
            }
            
            val sessionCount = if (isFutureDate) {
                0
            } else {
                (usageMinutes / 15).coerceAtLeast(1).coerceAtMost(30) // Realistic session count
            }
            
            val usagePercentage = if (dailyLimitMinutes > 0) {
                usageMinutes.toFloat() / dailyLimitMinutes.toFloat()
            } else {
                0f
            }
            
            usageData.add(
                MonthlyUsageSummary(
                    date = currentDate,
                    usageMinutes = usageMinutes,
                    usagePercentage = usagePercentage,
                    sessionCount = sessionCount,
                    isToday = isToday,
                    dayOfMonth = currentDate.dayOfMonth,
                    isWeekend = currentDate.dayOfWeek.value >= 6
                )
            )
            
            currentDate = currentDate.plusDays(1)
        }
        
        return usageData
    }
    
    private fun generateRealisticUsageMinutes(date: LocalDate, dailyLimitMinutes: Int): Int {
        // Generate realistic usage patterns based on day of week and other factors
        val dayOfWeek = date.dayOfWeek.value
        val isWeekend = dayOfWeek >= 6
        
        val baseUsage = dailyLimitMinutes * 0.7 // Most days are under limit
        val weekendMultiplier = if (isWeekend) 1.3 else 1.0
        val randomVariation = (0.7..1.4).random()
        
        return (baseUsage * weekendMultiplier * randomVariation).toInt()
            .coerceAtLeast(10) // Minimum 10 minutes
            .coerceAtMost(dailyLimitMinutes * 2) // Maximum 2x daily limit
    }
    
    private fun generateMonthlyInsights(
        monthlyData: List<MonthlyUsageSummary>,
        totalUsage: Int,
        goalDays: Int,
        daysOverLimit: Int,
        hasAdvancedAnalytics: Boolean
    ): List<MonthlyInsight> {
        val insights = mutableListOf<MonthlyInsight>()
        
        // Achievement insights
        if (goalDays > monthlyData.size * 0.7) {
            insights.add(
                MonthlyInsight(
                    type = MonthlyInsightType.ACHIEVEMENT,
                    title = "Excellent Self-Control! 🎉",
                    description = "You stayed within your daily limit on ${goalDays} out of ${monthlyData.size} days. That's ${(goalDays.toFloat() / monthlyData.size * 100).toInt()}% success rate!"
                )
            )
        }
        
        // Improvement insights
        if (hasAdvancedAnalytics) {
            val weekendDays = monthlyData.filter { it.isWeekend }
            val weekdayDays = monthlyData.filter { !it.isWeekend }
            
            if (weekendDays.isNotEmpty() && weekdayDays.isNotEmpty()) {
                val weekendAvg = weekendDays.map { it.usageMinutes }.average()
                val weekdayAvg = weekdayDays.map { it.usageMinutes }.average()
                
                if (weekendAvg > weekdayAvg * 1.5) {
                    insights.add(
                        MonthlyInsight(
                            type = MonthlyInsightType.PATTERN,
                            title = "Weekend Usage Pattern",
                            description = "Your weekend usage is ${((weekendAvg / weekdayAvg - 1) * 100).toInt()}% higher than weekdays. Consider setting weekend-specific goals."
                        )
                    )
                }
            }
        }
        
        // Challenge insights
        if (daysOverLimit > monthlyData.size * 0.3) {
            insights.add(
                MonthlyInsight(
                    type = MonthlyInsightType.CHALLENGE,
                    title = "Monthly Goal Challenge",
                    description = "You exceeded your daily limit on ${daysOverLimit} days. Try setting more realistic daily limits or using focus modes during challenging times."
                )
            )
        }
        
        // Improvement insights
        val recentDays = monthlyData.takeLast(7)
        val earlyDays = monthlyData.take(7)
        
        if (recentDays.isNotEmpty() && earlyDays.isNotEmpty()) {
            val recentAvg = recentDays.map { it.usageMinutes }.average()
            val earlyAvg = earlyDays.map { it.usageMinutes }.average()
            
            if (recentAvg < earlyAvg * 0.8) {
                insights.add(
                    MonthlyInsight(
                        type = MonthlyInsightType.IMPROVEMENT,
                        title = "Great Progress This Month! 📈",
                        description = "Your usage in the last week is ${((1 - recentAvg / earlyAvg) * 100).toInt()}% lower than the beginning of the month. Keep up the momentum!"
                    )
                )
            }
        }
        
        return insights
    }
    
    private fun calculatePercentageChange(oldValue: Int, newValue: Int): Float? {
        return if (oldValue > 0) {
            ((newValue - oldValue).toFloat() / oldValue.toFloat()) * 100
        } else {
            null
        }
    }
    
    private fun formatHoursMinutes(minutes: Int): String {
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        
        return when {
            hours > 0 && remainingMinutes > 0 -> "${hours}h ${remainingMinutes}m"
            hours > 0 -> "${hours}h"
            else -> "${remainingMinutes}m"
        }
    }
    
    private data class ComparisonData(
        val previousMonthTotal: String? = null,
        val previousDailyAverage: String? = null,
        val previousBestDay: String? = null,
        val previousTotalSessions: Int? = null,
        val previousGoalDays: Int? = null,
        val previousDaysCompleted: Int = 0,
        val previousDaysOverLimit: Int? = null,
        val totalUsageChange: Float? = null,
        val dailyAverageChange: Float? = null,
        val bestDayChange: Float? = null,
        val sessionsChange: Float? = null,
        val goalDaysChange: Float? = null,
        val daysOverLimitChange: Float? = null
    )
}