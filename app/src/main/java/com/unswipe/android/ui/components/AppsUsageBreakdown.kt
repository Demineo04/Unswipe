package com.unswipe.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unswipe.android.domain.repository.UsageRepository
import com.unswipe.android.ui.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppUsageData(
    val packageName: String,
    val appName: String,
    val category: AppCategory,
    val usageTimeMillis: Long,
    val sessionCount: Int,
    val lastUsedTime: Long,
    val isFrequentlyUsed: Boolean = false
) {
    val usageTimeFormatted: String
        get() = formatDuration(usageTimeMillis)
    
    val usagePercentage: Float
        get() = if (usageTimeMillis > 0) (usageTimeMillis / (8 * 60 * 60 * 1000f)) else 0f // Percentage of 8 hours
}

enum class AppCategory(
    val displayName: String,
    val icon: ImageVector,
    val color: Color
) {
    SOCIAL_MEDIA("Social Media", Icons.Default.Group, Color(0xFF1DA1F2)),
    ENTERTAINMENT("Entertainment", Icons.Default.PlayArrow, Color(0xFFFF4444)),
    PRODUCTIVITY("Productivity", Icons.Default.Work, Color(0xFF4CAF50)),
    COMMUNICATION("Communication", Icons.Default.Message, Color(0xFF9C27B0)),
    SHOPPING("Shopping", Icons.Default.ShoppingCart, Color(0xFFFF9800)),
    NEWS("News & Information", Icons.Default.Article, Color(0xFF607D8B)),
    GAMES("Games", Icons.Default.SportsEsports, Color(0xFFE91E63)),
    UTILITIES("Utilities", Icons.Default.Build, Color(0xFF795548)),
    OTHER("Other", Icons.Default.Apps, Color(0xFF9E9E9E))
}

data class CategoryUsageData(
    val category: AppCategory,
    val totalUsageMillis: Long,
    val appCount: Int,
    val topApps: List<AppUsageData>
) {
    val usageTimeFormatted: String
        get() = formatDuration(totalUsageMillis)
}

@HiltViewModel
class AppsUsageViewModel @Inject constructor(
    private val usageRepository: UsageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppsUsageUiState())
    val uiState: StateFlow<AppsUsageUiState> = _uiState.asStateFlow()

    init {
        loadAppsUsageData()
    }

    fun refreshData() {
        loadAppsUsageData()
    }

    private fun loadAppsUsageData() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                val realAppsData = getRealAppsUsageData()
                val categorizedData = categorizeAppsData(realAppsData)
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    appsData = realAppsData,
                    categorizedData = categorizedData,
                    totalUsageTime = realAppsData.sumOf { it.usageTimeMillis }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.localizedMessage ?: "Failed to load apps usage data"
                )
            }
        }
    }

    private suspend fun getRealAppsUsageData(): List<AppUsageData> {
        // Common social media and entertainment apps to track
        val appsToTrack = mapOf(
            "com.zhiliaoapp.musically" to ("TikTok" to AppCategory.SOCIAL_MEDIA),
            "com.instagram.android" to ("Instagram" to AppCategory.SOCIAL_MEDIA),
            "com.google.android.youtube" to ("YouTube" to AppCategory.ENTERTAINMENT),
            "com.facebook.katana" to ("Facebook" to AppCategory.SOCIAL_MEDIA),
            "com.snapchat.android" to ("Snapchat" to AppCategory.SOCIAL_MEDIA),
            "com.twitter.android" to ("Twitter" to AppCategory.SOCIAL_MEDIA),
            "com.reddit.frontpage" to ("Reddit" to AppCategory.SOCIAL_MEDIA),
            "com.discord" to ("Discord" to AppCategory.COMMUNICATION),
            "com.whatsapp" to ("WhatsApp" to AppCategory.COMMUNICATION),
            "com.netflix.mediaclient" to ("Netflix" to AppCategory.ENTERTAINMENT),
            "com.spotify.music" to ("Spotify" to AppCategory.ENTERTAINMENT),
            "com.microsoft.teams" to ("Teams" to AppCategory.PRODUCTIVITY),
            "com.slack" to ("Slack" to AppCategory.PRODUCTIVITY),
            "com.google.android.gm" to ("Gmail" to AppCategory.PRODUCTIVITY),
            "com.chrome.browser" to ("Chrome" to AppCategory.PRODUCTIVITY)
        )

        val appsData = mutableListOf<AppUsageData>()
        
        for ((packageName, appInfo) in appsToTrack) {
            val (appName, category) = appInfo
            val usageTime = usageRepository.getAppUsageToday(packageName)
            val sessionCount = usageRepository.getSessionCountToday(packageName)
            
            if (usageTime > 0 || sessionCount > 0) {
                appsData.add(
                    AppUsageData(
                        packageName = packageName,
                        appName = appName,
                        category = category,
                        usageTimeMillis = usageTime,
                        sessionCount = sessionCount,
                        lastUsedTime = System.currentTimeMillis(),
                        isFrequentlyUsed = usageTime > 30 * 60 * 1000 // More than 30 minutes
                    )
                )
            }
        }
        
        return appsData.sortedByDescending { it.usageTimeMillis }
    }

    private fun categorizeAppsData(appsData: List<AppUsageData>): List<CategoryUsageData> {
        return appsData
            .groupBy { it.category }
            .map { (category, apps) ->
                CategoryUsageData(
                    category = category,
                    totalUsageMillis = apps.sumOf { it.usageTimeMillis },
                    appCount = apps.size,
                    topApps = apps.sortedByDescending { it.usageTimeMillis }.take(3)
                )
            }
            .sortedByDescending { it.totalUsageMillis }
    }
}

data class AppsUsageUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val appsData: List<AppUsageData> = emptyList(),
    val categorizedData: List<CategoryUsageData> = emptyList(),
    val totalUsageTime: Long = 0L
)

@Composable
fun AppsUsageBreakdown(
    modifier: Modifier = Modifier,
    viewModel: AppsUsageViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            Text(
                text = "Apps Usage Breakdown",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = UnswipeTextPrimary
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        when {
            uiState.isLoading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = UnswipePrimary,
                            strokeWidth = 3.dp
                        )
                    }
                }
            }

            uiState.error != null -> {
                item {
                    ErrorCard(
                        error = uiState.error,
                        onRetry = { viewModel.refreshData() }
                    )
                }
            }

            uiState.appsData.isEmpty() -> {
                item {
                    EmptyStateCard()
                }
            }

            else -> {
                // Total usage summary
                item {
                    TotalUsageCard(totalUsageTime = uiState.totalUsageTime)
                }

                // Category breakdown
                item {
                    Text(
                        text = "Usage by Category",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = UnswipeTextPrimary
                        ),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(uiState.categorizedData) { categoryData ->
                    CategoryUsageCard(categoryData = categoryData)
                }

                // Individual apps
                item {
                    Text(
                        text = "Individual Apps",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = UnswipeTextPrimary
                        ),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(uiState.appsData.take(10)) { appData ->
                    AppUsageCard(appData = appData)
                }
            }
        }
    }
}

@Composable
private fun TotalUsageCard(totalUsageTime: Long) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = UnswipeCard),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box {
            // Background gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                UnswipePrimary.copy(alpha = 0.1f),
                                UnswipeSecondary.copy(alpha = 0.1f)
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = formatDuration(totalUsageTime),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = UnswipePrimary
                    )
                )
                Text(
                    text = "Total screen time today",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = UnswipeTextSecondary
                    )
                )
            }
        }
    }
}

@Composable
private fun CategoryUsageCard(categoryData: CategoryUsageData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = UnswipeCard),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = categoryData.category.color.copy(alpha = 0.2f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = categoryData.category.icon,
                            contentDescription = null,
                            tint = categoryData.category.color,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = categoryData.category.displayName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = UnswipeTextPrimary
                            )
                        )
                        Text(
                            text = "${categoryData.appCount} apps",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = UnswipeTextSecondary
                            )
                        )
                    }
                }

                Text(
                    text = categoryData.usageTimeFormatted,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = categoryData.category.color
                    )
                )
            }

            if (categoryData.topApps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(
                    color = UnswipeGray.copy(alpha = 0.3f),
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.height(12.dp))

                categoryData.topApps.forEach { app ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = app.appName,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = UnswipeTextPrimary
                            ),
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = app.usageTimeFormatted,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = UnswipeTextSecondary
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AppUsageCard(appData: AppUsageData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = UnswipeCard),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = appData.category.color.copy(alpha = 0.2f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = appData.category.icon,
                        contentDescription = null,
                        tint = appData.category.color,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = appData.appName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = UnswipeTextPrimary
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${appData.sessionCount} sessions",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = UnswipeTextSecondary
                        )
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = appData.usageTimeFormatted,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (appData.isFrequentlyUsed) UnswipeWarning else UnswipeTextPrimary
                    )
                )
                if (appData.isFrequentlyUsed) {
                    Text(
                        text = "High usage",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = UnswipeWarning
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorCard(
    error: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = UnswipeRed.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = UnswipeRed,
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Unable to load apps data",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = UnswipeTextPrimary
                ),
                textAlign = TextAlign.Center
            )
            
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = UnswipeTextSecondary
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = UnswipePrimary,
                    contentColor = UnswipeBlack
                )
            ) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun EmptyStateCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = UnswipeCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📱",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 48.sp
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "No usage data yet",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = UnswipeTextPrimary
                ),
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Start using your apps and your usage data will appear here",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = UnswipeTextSecondary
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

private fun formatDuration(milliseconds: Long): String {
    val totalMinutes = milliseconds / (1000 * 60)
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    
    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> "${minutes}m"
        else -> "< 1m"
    }
}