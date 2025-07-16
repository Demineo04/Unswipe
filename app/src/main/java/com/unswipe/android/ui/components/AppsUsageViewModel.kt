package com.unswipe.android.ui.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unswipe.android.data.apps.AppInfo
import com.unswipe.android.data.apps.AppInfoProvider
import com.unswipe.android.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class AppsUsageViewModel @Inject constructor(
    private val appInfoProvider: AppInfoProvider,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AppsUsageUiState())
    val uiState: StateFlow<AppsUsageUiState> = _uiState.asStateFlow()
    
    init {
        loadApps()
        setupDefaultTrackedApps()
    }
    
    private fun loadApps() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        
        viewModelScope.launch {
            try {
                val installedApps = appInfoProvider.getInstalledApps()
                val trackedApps = settingsRepository.getTrackedApps().first()
                val socialMediaApps = getDefaultSocialMediaApps()
                
                // Show only tracked apps (which will include social media apps and user-added apps)
                val trackedAppItems = installedApps
                    .filter { appInfo -> trackedApps.contains(appInfo.packageName) }
                    .map { appInfo ->
                        AppUsageItem(
                            packageName = appInfo.packageName,
                            appName = appInfo.appName,
                            appInfo = appInfo,
                            usageTime = generateMockUsageTime(), // Mock data for now
                            isTracked = true // All displayed apps are tracked
                        )
                    }
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    apps = trackedAppItems,
                    error = null
                )
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load apps: ${e.message}"
                )
            }
        }
    }
    
    fun toggleAppTracking(packageName: String) {
        viewModelScope.launch {
            try {
                val currentTrackedApps = settingsRepository.getTrackedApps().first().toMutableSet()
                val isCurrentlyTracked = currentTrackedApps.contains(packageName)
                
                if (isCurrentlyTracked) {
                    currentTrackedApps.remove(packageName)
                } else {
                    currentTrackedApps.add(packageName)
                }
                
                settingsRepository.setTrackedApps(currentTrackedApps)
                
                // Update UI state immediately for responsive feedback
                val updatedApps = _uiState.value.apps.map { app ->
                    if (app.packageName == packageName) {
                        app.copy(isTracked = !isCurrentlyTracked)
                    } else {
                        app
                    }
                }
                
                _uiState.value = _uiState.value.copy(apps = updatedApps)
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to update app tracking: ${e.message}"
                )
                // Reload apps to restore correct state
                loadApps()
            }
        }
    }
    
    fun refreshApps() {
        loadApps()
    }
    
    suspend fun getAvailableAppsForSelection(): List<AppInfo> {
        return try {
            val installedApps = appInfoProvider.getInstalledApps()
            val currentTrackedApps = settingsRepository.getTrackedApps().first()
            
            // Return apps that are not currently tracked
            installedApps.filter { appInfo ->
                !currentTrackedApps.contains(appInfo.packageName) &&
                appInfo.packageName != "com.unswipe.android" // Exclude our own app
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    fun addAppToTracking(packageName: String) {
        viewModelScope.launch {
            try {
                val currentTrackedApps = settingsRepository.getTrackedApps().first().toMutableSet()
                currentTrackedApps.add(packageName)
                settingsRepository.setTrackedApps(currentTrackedApps)
                
                // Refresh the apps list to show the newly added app
                loadApps()
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to add app: ${e.message}"
                )
            }
        }
    }
    
    private fun setupDefaultTrackedApps() {
        viewModelScope.launch {
            try {
                val currentTrackedApps = settingsRepository.getTrackedApps().first().toMutableSet()
                val defaultSocialMediaApps = getDefaultSocialMediaApps()
                
                // Add default social media apps if not already tracked
                var needsUpdate = false
                defaultSocialMediaApps.forEach { packageName ->
                    if (!currentTrackedApps.contains(packageName)) {
                        currentTrackedApps.add(packageName)
                        needsUpdate = true
                    }
                }
                
                if (needsUpdate) {
                    settingsRepository.setTrackedApps(currentTrackedApps)
                }
                
            } catch (e: Exception) {
                // Silent failure - don't interrupt the main flow
            }
        }
    }
    
    private fun getDefaultSocialMediaApps(): Set<String> {
        return setOf(
            "com.twitter.android",           // X (Twitter)
            "com.facebook.katana",           // Facebook
            "com.instagram.android",         // Instagram
            "com.snapchat.android",          // Snapchat
            "com.google.android.youtube",    // YouTube
            "com.zhiliaoapp.musically",      // TikTok
            "com.pinterest",                 // Pinterest
            "com.reddit.frontpage",          // Reddit (bonus)
            "com.discord",                   // Discord (bonus)
            "com.whatsapp"                   // WhatsApp (bonus)
        )
    }
    
    private fun generateMockUsageTime(): String {
        // Generate realistic mock usage times
        val minutes = (0..240).random() // 0-4 hours in minutes
        return when {
            minutes == 0 -> "Not used today"
            minutes < 60 -> "${minutes}m today"
            else -> {
                val hours = minutes / 60
                val remainingMins = minutes % 60
                if (remainingMins == 0) {
                    "${hours}h today"
                } else {
                    "${hours}h ${remainingMins}m today"
                }
            }
        }
    }
}

data class AppsUsageUiState(
    val isLoading: Boolean = false,
    val apps: List<AppUsageItem> = emptyList(),
    val error: String? = null
)

data class AppUsageItem(
    val packageName: String,
    val appName: String,
    val appInfo: AppInfo,
    val usageTime: String,
    val isTracked: Boolean
)