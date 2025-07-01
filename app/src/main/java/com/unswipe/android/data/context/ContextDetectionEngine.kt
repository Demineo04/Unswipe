package com.unswipe.android.data.context

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.provider.Settings
import com.unswipe.android.domain.model.*
import com.unswipe.android.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import java.time.LocalTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContextDetectionEngine @Inject constructor(
    private val context: Context,
    private val settingsRepository: SettingsRepository
) {
    
    /**
     * Detects the current life context based on time, schedule, and device state
     */
    suspend fun detectCurrentContext(): ContextType {
        val currentTime = LocalTime.now()
        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val userSchedule = getUserSchedule()
        
        return when {
            // Check bedtime first (highest priority)
            userSchedule.isSleepScheduleEnabled && isAfterBedtime(currentTime, userSchedule) -> {
                ContextType.BEDTIME
            }
            
            // Check sleep preparation
            userSchedule.isSleepScheduleEnabled && isSleepPreparationTime(currentTime, userSchedule) -> {
                ContextType.SLEEP_PREPARATION
            }
            
            // Check morning routine
            userSchedule.isSleepScheduleEnabled && isMorningRoutine(currentTime, userSchedule) -> {
                ContextType.MORNING_ROUTINE
            }
            
            // Check work hours
            userSchedule.isWorkScheduleEnabled && 
            isWorkDay(currentDay, userSchedule) && 
            isWorkHours(currentTime, userSchedule) -> {
                if (isLikelyAtWork()) ContextType.WORK_HOURS else ContextType.PERSONAL_TIME
            }
            
            else -> ContextType.PERSONAL_TIME
        }
    }
    
    /**
     * Gets current device state for context analysis
     */
    fun getCurrentDeviceState(): DeviceState {
        return DeviceState(
            isCharging = isDeviceCharging(),
            batteryLevel = getBatteryLevel(),
            screenBrightness = getScreenBrightness(),
            isConnectedToWifi = isConnectedToWifi(),
            wifiSSID = getCurrentWifiSSID(),
            isHeadphonesConnected = isHeadphonesConnected()
        )
    }
    
    /**
     * Detects location context using privacy-friendly methods
     */
    suspend fun detectLocationContext(): LocationContext {
        val workWifiSSIDs = getWorkWifiSSIDs()
        val currentSSID = getCurrentWifiSSID()
        
        return when {
            currentSSID != null && workWifiSSIDs.contains(currentSSID) -> LocationContext.WORK
            isConnectedToWifi() -> LocationContext.HOME // Assume home if on WiFi but not work
            else -> LocationContext.OTHER
        }
    }
    
    // Private helper methods
    
    private suspend fun getUserSchedule(): UserSchedule {
        return try {
            settingsRepository.getUserSchedule()
        } catch (e: Exception) {
            UserSchedule.Default
        }
    }
    
    private fun isAfterBedtime(currentTime: LocalTime, schedule: UserSchedule): Boolean {
        val sleepTime = schedule.sleepTime
        val wakeupTime = schedule.wakeupTime
        
        return if (sleepTime.isAfter(wakeupTime)) {
            // Normal case: sleep at 11 PM, wake at 7 AM
            currentTime.isAfter(sleepTime) || currentTime.isBefore(wakeupTime)
        } else {
            // Edge case: sleep after midnight
            currentTime.isAfter(sleepTime) && currentTime.isBefore(wakeupTime)
        }
    }
    
    private fun isSleepPreparationTime(currentTime: LocalTime, schedule: UserSchedule): Boolean {
        val prepTime = schedule.sleepPreparationTime
        val sleepTime = schedule.sleepTime
        
        return currentTime.isAfter(prepTime) && currentTime.isBefore(sleepTime)
    }
    
    private fun isMorningRoutine(currentTime: LocalTime, schedule: UserSchedule): Boolean {
        val wakeupTime = schedule.wakeupTime
        val routineEndTime = schedule.morningRoutineEndTime
        
        return currentTime.isAfter(wakeupTime) && currentTime.isBefore(routineEndTime)
    }
    
    private fun isWorkDay(dayOfWeek: Int, schedule: UserSchedule): Boolean {
        return schedule.workDays.contains(dayOfWeek)
    }
    
    private fun isWorkHours(currentTime: LocalTime, schedule: UserSchedule): Boolean {
        return currentTime.isAfter(schedule.workStartTime) && 
               currentTime.isBefore(schedule.workEndTime)
    }
    
    private fun isLikelyAtWork(): Boolean {
        // Use WiFi SSID patterns to detect work location (privacy-friendly)
        val currentSSID = getCurrentWifiSSID()
        val workIndicators = listOf("corp", "office", "work", "company")
        
        return currentSSID?.lowercase()?.let { ssid ->
            workIndicators.any { indicator -> ssid.contains(indicator) }
        } ?: false
    }
    
    private fun isDeviceCharging(): Boolean {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { filter ->
            context.registerReceiver(null, filter)
        }
        val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        return status == BatteryManager.BATTERY_STATUS_CHARGING ||
               status == BatteryManager.BATTERY_STATUS_FULL
    }
    
    private fun getBatteryLevel(): Int {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { filter ->
            context.registerReceiver(null, filter)
        }
        val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        return if (level != -1 && scale != -1) {
            (level * 100 / scale.toFloat()).toInt()
        } else {
            100
        }
    }
    
    private fun getScreenBrightness(): Float {
        return try {
            val brightness = Settings.System.getInt(
                context.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS
            )
            brightness / 255f
        } catch (e: Exception) {
            1.0f
        }
    }
    
    private fun isConnectedToWifi(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo?.type == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected
    }
    
    private fun getCurrentWifiSSID(): String? {
        if (!isConnectedToWifi()) return null
        
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        return wifiInfo.ssid?.replace("\"", "") // Remove quotes from SSID
    }
    
    private fun isHeadphonesConnected(): Boolean {
        // This would require audio manager integration
        // For now, return false as a placeholder
        return false
    }
    
    private suspend fun getWorkWifiSSIDs(): Set<String> {
        return try {
            settingsRepository.getWorkWifiSSIDs()
        } catch (e: Exception) {
            emptySet()
        }
    }
}