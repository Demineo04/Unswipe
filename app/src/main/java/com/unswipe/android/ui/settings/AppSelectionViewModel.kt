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
    val icon: ApplicationInfo, // We'll resolve the drawable in the composable
    val isBlocked: Boolean
)

@HiltViewModel
class AppSelectionViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val packageManager: PackageManager
) : ViewModel() {

    private val _apps = MutableStateFlow<List<AppInfoForUi>>(emptyList())
    val apps: StateFlow<List<AppInfoForUi>> = _apps.asStateFlow()

    init {
        loadInstalledApps()
    }

    private fun loadInstalledApps() {
        viewModelScope.launch {
            val blockedApps = settingsRepository.getBlockedApps().first()
            val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
                .filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 } // Filter out system apps
                .map { appInfo ->
                    AppInfoForUi(
                        name = appInfo.loadLabel(packageManager).toString(),
                        packageName = appInfo.packageName,
                        icon = appInfo,
                        isBlocked = blockedApps.contains(appInfo.packageName)
                    )
                }
                .sortedBy { it.name }

            _apps.value = installedApps
        }
    }

    fun setAppBlocked(packageName: String, isBlocked: Boolean) {
        viewModelScope.launch {
            val currentBlocked = _apps.value
                .filter { it.isBlocked && it.packageName != packageName }
                .map { it.packageName }
                .toMutableSet()
            
            if (isBlocked) {
                currentBlocked.add(packageName)
            }
            
            settingsRepository.setBlockedApps(currentBlocked)
            
            // Update the UI state to reflect the change immediately
            _apps.value = _apps.value.map { 
                if (it.packageName == packageName) it.copy(isBlocked = isBlocked) else it
            }
        }
    }
} 