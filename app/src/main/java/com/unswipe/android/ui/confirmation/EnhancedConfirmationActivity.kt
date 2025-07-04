package com.unswipe.android.ui.confirmation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.unswipe.android.domain.model.ContextType
import com.unswipe.android.domain.model.InterventionDecision
import com.unswipe.android.domain.model.InterventionUrgency
import com.unswipe.android.domain.model.InterventionAction
import com.unswipe.android.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class EnhancedConfirmationActivity : ComponentActivity() {
    
    private val viewModel: EnhancedConfirmationViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: ""
        val appName = getAppName(packageName)
        
        viewModel.initialize(packageName)
        
        setContent {
            UnswipeTheme {
                EnhancedConfirmationScreen(
                    packageName = packageName,
                    appName = appName,
                    viewModel = viewModel,
                    onAllow = {
                        viewModel.recordDecision(true, packageName)
                        setResult(Activity.RESULT_OK)
                        finish()
                    },
                    onBlock = {
                        viewModel.recordDecision(false, packageName)
                        setResult(Activity.RESULT_CANCELED)
                        finish()
                    }
                )
            }
        }
    }
    
    private fun getAppName(packageName: String): String {
        return try {
            val packageManager = packageManager
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(applicationInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            packageName.substringAfterLast(".")
        }
    }
    
    companion object {
        private const val EXTRA_PACKAGE_NAME = "package_name"
        
        fun createIntent(context: Context, packageName: String): Intent {
            return Intent(context, EnhancedConfirmationActivity::class.java).apply {
                putExtra(EXTRA_PACKAGE_NAME, packageName)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }
        }
    }
}

@Composable
fun EnhancedConfirmationScreen(
    packageName: String,
    appName: String,
    viewModel: EnhancedConfirmationViewModel,
    onAllow: () -> Unit,
    onBlock: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        when (uiState) {
            is EnhancedConfirmationUiState.Loading -> {
                CircularProgressIndicator(color = UnswipePrimary)
            }
            
            is EnhancedConfirmationUiState.Ready -> {
                ContextAwareConfirmationDialog(
                    appName = appName,
                    decision = (uiState as EnhancedConfirmationUiState.Ready).interventionDecision,
                    usageTimeToday = (uiState as EnhancedConfirmationUiState.Ready).usageStats,
                    onAllow = onAllow,
                    onBlock = onBlock
                )
            }
            
            is EnhancedConfirmationUiState.Error -> {
                // Fallback to simple confirmation on error
                SimpleConfirmationDialog(
                    appName = appName,
                    onAllow = onAllow,
                    onBlock = onBlock
                )
            }
        }
    }
}

@Composable
private fun ContextAwareConfirmationDialog(
    appName: String,
    decision: InterventionDecision,
    usageTimeToday: Long,
    onAllow: () -> Unit,
    onBlock: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(containerColor = UnswipeCard),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App icon and warning indicator
            Box(
                contentAlignment = Alignment.Center
            ) {
                // App icon background
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    getUrgencyColor(decision.urgency).copy(alpha = 0.2f),
                                    getUrgencyColor(decision.urgency).copy(alpha = 0.1f)
                                )
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getAppIcon(appName),
                        contentDescription = null,
                        tint = getUrgencyColor(decision.urgency),
                        modifier = Modifier.size(40.dp)
                    )
                }
                
                // Warning indicator
                if (decision.urgency != InterventionUrgency.LOW) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = getUrgencyColor(decision.urgency),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .align(Alignment.TopEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (decision.urgency) {
                                InterventionUrgency.HIGH -> Icons.Default.Warning
                                InterventionUrgency.CRITICAL -> Icons.Default.Error
                                else -> Icons.Default.Info
                            },
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // App name
            Text(
                text = appName,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = UnswipeTextPrimary
                )
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Context-aware message
            Text(
                text = decision.message,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = UnswipeTextSecondary,
                    textAlign = TextAlign.Center
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Usage statistics
            UsageStatsCard(usageTimeToday)
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Contextual tip or alternative activity
            if (decision.contextualTip?.isNotEmpty() == true) {
                ContextualTipCard(decision.contextualTip!!)
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            if (decision.alternativeActivity?.isNotEmpty() == true) {
                AlternativeActivityCard(decision.alternativeActivity!!)
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Action buttons
            ConfirmationActionButtons(
                decision = decision,
                onAllow = onAllow,
                onBlock = onBlock
            )
        }
    }
}

@Composable
private fun UsageStatsCard(usageTimeToday: Long) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = UnswipeSurface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            UsageStatItem(
                label = "Today",
                value = formatDuration(usageTimeToday),
                icon = Icons.Default.Today,
                color = if (usageTimeToday > 0) UnswipeRed else UnswipePrimary
            )
            
            UsageStatItem(
                label = "Sessions",
                value = "1",
                icon = Icons.Default.TouchApp,
                color = UnswipeSecondary
            )
            
            UsageStatItem(
                label = "Progress",
                value = "100%",
                icon = Icons.Default.TrendingUp,
                color = UnswipeGreen
            )
        }
    }
}

@Composable
private fun UsageStatItem(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(20.dp)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = color
            )
        )
        
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = UnswipeTextSecondary
            )
        )
    }
}

@Composable
private fun ContextualTipCard(tip: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = UnswipePrimary.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                tint = UnswipePrimary,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = tip,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = UnswipeTextPrimary
                )
            )
        }
    }
}

@Composable
private fun AlternativeActivityCard(activity: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = UnswipeGreen.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.SelfImprovement,
                contentDescription = null,
                tint = UnswipeGreen,
                modifier = Modifier.size(20.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Column {
                Text(
                    text = "Try this instead:",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = UnswipeGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                Text(
                    text = activity,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = UnswipeTextPrimary
                    )
                )
            }
        }
    }
}

@Composable
private fun ConfirmationActionButtons(
    decision: InterventionDecision,
    onAllow: () -> Unit,
    onBlock: () -> Unit
) {
    val (primaryText, secondaryText) = getButtonTexts(decision)
    val (primaryAction, secondaryAction) = when (decision.suggestedAction) {
        InterventionAction.STRONG_BLOCK -> onBlock to onAllow
        InterventionAction.FIRM_BLOCK -> onBlock to onAllow
        InterventionAction.GENTLE_REMINDER -> onAllow to onBlock
        else -> onAllow to onBlock
    }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Secondary button (usually "Continue" or "Allow")
        OutlinedButton(
            onClick = secondaryAction,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = UnswipeTextSecondary
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = Brush.horizontalGradient(
                    colors = listOf(UnswipeGray, UnswipeGray)
                )
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = secondaryText,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }
        
        // Primary button (usually "Go Back" or "Block")
        Button(
            onClick = primaryAction,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = when (decision.urgency) {
                    InterventionUrgency.HIGH, InterventionUrgency.CRITICAL -> UnswipeRed
                    InterventionUrgency.MEDIUM -> UnswipeYellow
                    else -> UnswipePrimary
                },
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = primaryText,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
private fun SimpleConfirmationDialog(
    appName: String,
    onAllow: () -> Unit,
    onBlock: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(containerColor = UnswipeCard),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Apps,
                contentDescription = null,
                tint = UnswipePrimary,
                modifier = Modifier.size(48.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Open $appName?",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = UnswipeTextPrimary
                )
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Take a moment to consider if you really want to open this app right now.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = UnswipeTextSecondary,
                    textAlign = TextAlign.Center
                )
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onAllow,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Continue")
                }
                
                Button(
                    onClick = onBlock,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = UnswipePrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Go Back")
                }
            }
        }
    }
}

// Helper functions
private fun getUrgencyColor(urgency: InterventionUrgency): Color = when (urgency) {
    InterventionUrgency.CRITICAL -> UnswipeRed
    InterventionUrgency.HIGH -> UnswipeOrange
    InterventionUrgency.MEDIUM -> UnswipeYellow
    InterventionUrgency.LOW -> UnswipeGreen
}

private fun getAppIcon(appName: String): ImageVector = when {
    appName.contains("Instagram", ignoreCase = true) -> Icons.Default.PhotoCamera
    appName.contains("TikTok", ignoreCase = true) -> Icons.Default.VideoLibrary
    appName.contains("YouTube", ignoreCase = true) -> Icons.Default.PlayArrow
    appName.contains("Facebook", ignoreCase = true) -> Icons.Default.Group
    appName.contains("Twitter", ignoreCase = true) -> Icons.Default.Chat
    else -> Icons.Default.Apps
}

private fun getButtonTexts(decision: InterventionDecision): Pair<String, String> {
    return when (decision.suggestedAction) {
        InterventionAction.STRONG_BLOCK -> "Stay Focused" to "Continue Anyway"
        InterventionAction.FIRM_BLOCK -> "Take a Break" to "Quick Check"
        InterventionAction.GENTLE_REMINDER -> "Continue" to "Go Back"
        else -> "Go Back" to "Continue"
    }
}

private fun formatDuration(millis: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(millis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
    
    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> "${minutes}m"
        else -> "<1m"
    }
}

// Data classes
data class UsageStats(
    val todayUsage: Long,
    val sessionCount: Int,
    val usagePercentage: Float,
    val isOverLimit: Boolean
) 