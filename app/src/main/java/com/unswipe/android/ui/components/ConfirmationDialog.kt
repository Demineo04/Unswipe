package com.unswipe.android.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.unswipe.android.R
import com.unswipe.android.ui.confirmation.ConfirmationUiState

@Composable
fun ConfirmationDialog(
    appName: String,
    appIcon: Painter,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    // Legacy simple dialog - kept for backward compatibility
    EnhancedConfirmationDialog(
        uiState = ConfirmationUiState(
            isLoading = false,
            appName = appName,
            usageMessage = "Do you really want to open $appName?",
            motivationalMessage = "Consider your digital wellness goals 🎯"
        ),
        appIcon = appIcon,
        onConfirm = onConfirm,
        onCancel = onCancel
    )
}

@Composable
fun EnhancedConfirmationDialog(
    uiState: ConfirmationUiState,
    appIcon: Painter,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onTakeBreak: (() -> Unit)? = null
) {
    // Animation for the dialog appearance
    val scale by animateFloatAsState(
        targetValue = if (uiState.isLoading) 0.8f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black.copy(alpha = 0.85f)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .scale(scale)
                    .clip(RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                if (uiState.isLoading) {
                    LoadingContent()
                } else {
                    DialogContent(
                        uiState = uiState,
                        appIcon = appIcon,
                        onConfirm = onConfirm,
                        onCancel = onCancel,
                        onTakeBreak = onTakeBreak
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun DialogContent(
    uiState: ConfirmationUiState,
    appIcon: Painter,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onTakeBreak: (() -> Unit)?
) {
    Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App icon with warning indicator for over-limit usage
        Box(
            contentAlignment = Alignment.TopEnd
        ) {
            Image(
                painter = appIcon,
                contentDescription = "${uiState.appName} icon",
                modifier = Modifier.size(80.dp)
            )
            
            if (uiState.isOverLimit) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Over limit warning",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            MaterialTheme.colorScheme.surface,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // App name
        Text(
            text = uiState.appName,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Usage message
        Text(
            text = uiState.usageMessage,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = if (uiState.isOverLimit) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )

        // Usage progress bar (if we have limit data)
        if (uiState.dailyLimitMillis > 0) {
            Spacer(modifier = Modifier.height(16.dp))
            
            UsageProgressIndicator(
                usagePercentage = uiState.usagePercentage,
                isOverLimit = uiState.isOverLimit
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Motivational message
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (uiState.isOverLimit) {
                    MaterialTheme.colorScheme.errorContainer
                } else {
                    MaterialTheme.colorScheme.primaryContainer
                }
            )
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = if (uiState.isOverLimit) {
                        MaterialTheme.colorScheme.onErrorContainer
                    } else {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    },
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = uiState.motivationalMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (uiState.isOverLimit) {
                        MaterialTheme.colorScheme.onErrorContainer
                    } else {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action buttons
        ActionButtons(
            uiState = uiState,
            onConfirm = onConfirm,
            onCancel = onCancel,
            onTakeBreak = onTakeBreak
        )

        // Premium bypass option
        if (uiState.isPremium && uiState.canBypass) {
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Premium: Skip confirmation",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun UsageProgressIndicator(
    usagePercentage: Float,
    isOverLimit: Boolean
) {
    Column {
        LinearProgressIndicator(
            progress = usagePercentage.coerceIn(0f, 1f),
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = if (isOverLimit) {
                MaterialTheme.colorScheme.error
            } else if (usagePercentage > 0.8f) {
                MaterialTheme.colorScheme.tertiary
            } else {
                MaterialTheme.colorScheme.primary
            }
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = "${(usagePercentage * 100).toInt()}% of daily limit",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ActionButtons(
    uiState: ConfirmationUiState,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onTakeBreak: (() -> Unit)?
) {
    when {
        uiState.isOverLimit && onTakeBreak != null -> {
            // Over limit: Show "Take a Break" and "Continue Anyway"
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onTakeBreak,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Take a Break Instead")
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Go Back")
                    }
                    
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Continue Anyway")
                    }
                }
            }
        }
        else -> {
            // Normal confirmation: "Go Back" and "Continue"
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Go Back")
                }
                
                OutlinedButton(
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Continue")
                }
            }
        }
    }
} 