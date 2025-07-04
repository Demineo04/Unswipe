package com.unswipe.android.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unswipe.android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: NotificationSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notification Settings", color = UnswipeTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = UnswipeTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = UnswipeBlack
                )
            )
        },
        containerColor = UnswipeBlack
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(UnswipeBlack, UnswipeSurface)
                    )
                )
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // General Notifications
                item {
                    NotificationSection(
                        title = "General",
                        items = listOf(
                            NotificationItem(
                                title = "Push Notifications",
                                description = "Receive notifications about your app usage",
                                isEnabled = uiState.pushNotificationsEnabled,
                                onToggle = { viewModel.togglePushNotifications() }
                            ),
                            NotificationItem(
                                title = "Daily Summary",
                                description = "Get a daily summary of your usage statistics",
                                isEnabled = uiState.dailySummaryEnabled,
                                onToggle = { viewModel.toggleDailySummary() }
                            )
                        )
                    )
                }
                
                // Usage Alerts
                item {
                    NotificationSection(
                        title = "Usage Alerts",
                        items = listOf(
                            NotificationItem(
                                title = "Limit Warnings",
                                description = "Alert when approaching daily usage limit",
                                isEnabled = uiState.limitWarningsEnabled,
                                onToggle = { viewModel.toggleLimitWarnings() }
                            ),
                            NotificationItem(
                                title = "Milestone Achievements",
                                description = "Celebrate when you meet your goals",
                                isEnabled = uiState.achievementsEnabled,
                                onToggle = { viewModel.toggleAchievements() }
                            ),
                            NotificationItem(
                                title = "Usage Insights",
                                description = "Get personalized insights about your habits",
                                isEnabled = uiState.insightsEnabled,
                                onToggle = { viewModel.toggleInsights() }
                            )
                        )
                    )
                }
                
                // Smart Nudges
                item {
                    NotificationSection(
                        title = "Smart Nudges",
                        items = listOf(
                            NotificationItem(
                                title = "Contextual Reminders",
                                description = "Gentle reminders based on your context",
                                isEnabled = uiState.contextualRemindersEnabled,
                                isPremium = true,
                                onToggle = { viewModel.toggleContextualReminders() }
                            ),
                            NotificationItem(
                                title = "AI Suggestions",
                                description = "AI-powered suggestions for better habits",
                                isEnabled = uiState.aiSuggestionsEnabled,
                                isPremium = true,
                                onToggle = { viewModel.toggleAiSuggestions() }
                            )
                        )
                    )
                }
                
                // Notification Schedule
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = UnswipeCard),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Quiet Hours",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = UnswipeTextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Do not disturb",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = UnswipeTextSecondary
                                    )
                                )
                                
                                Switch(
                                    checked = uiState.quietHoursEnabled,
                                    onCheckedChange = { viewModel.toggleQuietHours() },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = UnswipePrimary,
                                        checkedTrackColor = UnswipePrimary.copy(alpha = 0.5f),
                                        uncheckedThumbColor = UnswipeGray,
                                        uncheckedTrackColor = UnswipeGray.copy(alpha = 0.5f)
                                    )
                                )
                            }
                            
                            if (uiState.quietHoursEnabled) {
                                Spacer(modifier = Modifier.height(12.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    // Start time
                                    OutlinedButton(
                                        onClick = { /* TODO: Show time picker */ },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("From: ${uiState.quietHoursStart}")
                                    }
                                    
                                    // End time
                                    OutlinedButton(
                                        onClick = { /* TODO: Show time picker */ },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("To: ${uiState.quietHoursEnd}")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationSection(
    title: String,
    items: List<NotificationItem>
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                color = UnswipeTextPrimary,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = UnswipeCard),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column {
                items.forEachIndexed { index, item ->
                    NotificationItemRow(
                        item = item,
                        showDivider = index < items.size - 1
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationItemRow(
    item: NotificationItem,
    showDivider: Boolean
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = UnswipeTextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    
                    if (item.isPremium) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = UnswipePrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "PRO",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = UnswipeBlack,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = UnswipeTextSecondary
                    )
                )
            }
            
            Switch(
                checked = item.isEnabled,
                onCheckedChange = { item.onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = UnswipePrimary,
                    checkedTrackColor = UnswipePrimary.copy(alpha = 0.5f),
                    uncheckedThumbColor = UnswipeGray,
                    uncheckedTrackColor = UnswipeGray.copy(alpha = 0.5f)
                )
            )
        }
        
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = UnswipeGray.copy(alpha = 0.2f)
            )
        }
    }
}

data class NotificationItem(
    val title: String,
    val description: String,
    val isEnabled: Boolean,
    val isPremium: Boolean = false,
    val onToggle: () -> Unit
)

// ViewModel
data class NotificationSettingsUiState(
    val pushNotificationsEnabled: Boolean = true,
    val dailySummaryEnabled: Boolean = true,
    val limitWarningsEnabled: Boolean = true,
    val achievementsEnabled: Boolean = true,
    val insightsEnabled: Boolean = true,
    val contextualRemindersEnabled: Boolean = false,
    val aiSuggestionsEnabled: Boolean = false,
    val quietHoursEnabled: Boolean = false,
    val quietHoursStart: String = "10:00 PM",
    val quietHoursEnd: String = "7:00 AM",
    val isLoading: Boolean = false,
    val error: String? = null
)

class NotificationSettingsViewModel @javax.inject.Inject constructor(
    private val settingsRepository: com.unswipe.android.domain.repository.SettingsRepository
) : androidx.lifecycle.ViewModel() {
    
    private val _uiState = kotlinx.coroutines.flow.MutableStateFlow(NotificationSettingsUiState())
    val uiState: kotlinx.coroutines.flow.StateFlow<NotificationSettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch {
            // TODO: Load actual settings from repository
            // For now, using default values
        }
    }
    
    fun togglePushNotifications() {
        _uiState.value = _uiState.value.copy(
            pushNotificationsEnabled = !_uiState.value.pushNotificationsEnabled
        )
        saveSettings()
    }
    
    fun toggleDailySummary() {
        _uiState.value = _uiState.value.copy(
            dailySummaryEnabled = !_uiState.value.dailySummaryEnabled
        )
        saveSettings()
    }
    
    fun toggleLimitWarnings() {
        _uiState.value = _uiState.value.copy(
            limitWarningsEnabled = !_uiState.value.limitWarningsEnabled
        )
        saveSettings()
    }
    
    fun toggleAchievements() {
        _uiState.value = _uiState.value.copy(
            achievementsEnabled = !_uiState.value.achievementsEnabled
        )
        saveSettings()
    }
    
    fun toggleInsights() {
        _uiState.value = _uiState.value.copy(
            insightsEnabled = !_uiState.value.insightsEnabled
        )
        saveSettings()
    }
    
    fun toggleContextualReminders() {
        _uiState.value = _uiState.value.copy(
            contextualRemindersEnabled = !_uiState.value.contextualRemindersEnabled
        )
        saveSettings()
    }
    
    fun toggleAiSuggestions() {
        _uiState.value = _uiState.value.copy(
            aiSuggestionsEnabled = !_uiState.value.aiSuggestionsEnabled
        )
        saveSettings()
    }
    
    fun toggleQuietHours() {
        _uiState.value = _uiState.value.copy(
            quietHoursEnabled = !_uiState.value.quietHoursEnabled
        )
        saveSettings()
    }
    
    private fun saveSettings() {
        viewModelScope.launch {
            // TODO: Save settings to repository
        }
    }
}