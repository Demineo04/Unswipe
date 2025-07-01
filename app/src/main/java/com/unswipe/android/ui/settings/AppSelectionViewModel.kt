package com.unswipe.android.ui.settings

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unswipe.android.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

// This should be in its own file, but for simplicity, it's here for now.
// It will be moved later if needed.
data class AppInfoForUi(
    val name: String,
    val packageName: String,
    val applicationInfo: ApplicationInfo,
    val isTracked: Boolean
)

@HiltViewModel
class AppSelectionViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val packageManager: PackageManager
) : ViewModel() {

    private val _apps = MutableStateFlow<List<AppInfoForUi>>(emptyList())
    val apps: StateFlow<List<AppInfoForUi>> = _apps.asStateFlow()
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadInstalledApps()
    }

    private fun loadInstalledApps() {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                val blockedApps = settingsRepository.getBlockedApps().first()
                val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
                    .filter { appInfo ->
                        // Filter out system apps and apps without launch intent
                        (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0 &&
                        packageManager.getLaunchIntentForPackage(appInfo.packageName) != null
                    }
                    .map { appInfo ->
                        AppInfoForUi(
                            name = appInfo.loadLabel(packageManager).toString(),
                            packageName = appInfo.packageName,
                            applicationInfo = appInfo,
                            isTracked = blockedApps.contains(appInfo.packageName)
                        )
                    }
                    .sortedBy { it.name }

                _apps.value = installedApps
            } catch (e: Exception) {
                // Handle error - could emit an error state if needed
                _apps.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setAppTracking(packageName: String, isTracked: Boolean) {
        viewModelScope.launch {
            try {
                if (isTracked) {
                    settingsRepository.addBlockedApp(packageName)
                } else {
                    settingsRepository.removeBlockedApp(packageName)
                }
                
                // Update the UI state to reflect the change immediately
                _apps.value = _apps.value.map { app ->
                    if (app.packageName == packageName) {
                        app.copy(isTracked = isTracked)
                    } else {
                        app
                    }
                }
            } catch (e: Exception) {
                // Handle error - could show a toast or error message
                // For now, we'll just not update the UI state
            }
        }
    }
} 