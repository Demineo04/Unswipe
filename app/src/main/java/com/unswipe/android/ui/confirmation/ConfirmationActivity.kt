package com.unswipe.android.ui.confirmation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.unswipe.android.R // Assuming you have strings defined
import com.unswipe.android.ui.theme.UnswipeTheme // Assuming Theme exists
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.asImageBitmap
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.unswipe.android.ui.components.EnhancedConfirmationDialog
import com.unswipe.android.ui.theme.*
import com.unswipe.android.util.AppNameMapper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConfirmationActivity : ComponentActivity() {

    companion object {
        private const val EXTRA_APP_NAME = "com.unswipe.android.EXTRA_APP_NAME"
        private const val EXTRA_PACKAGE_NAME = "com.unswipe.android.EXTRA_PACKAGE_NAME" // Keep track if needed

        fun newIntent(context: Context, appName: String, packageName: String): Intent {
            return Intent(context, ConfirmationActivity::class.java).apply {
                putExtra(EXTRA_APP_NAME, appName)
                putExtra(EXTRA_PACKAGE_NAME, packageName)
                // FLAG_ACTIVITY_NEW_TASK is crucial when starting from a Service
                // FLAG_ACTIVITY_CLEAR_TOP helps manage the task stack
                // FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS hides this confirmation from recent apps list
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appName = intent.getStringExtra(EXTRA_APP_NAME) ?: "this app"
        val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: ""

        // Ensure we have the best possible app name using AppNameMapper
        val finalAppName = if (packageName.isNotEmpty()) {
            AppNameMapper.getAppName(this, packageName)
        } else {
            appName
        }

        setContent {
            UnswipeTheme {
                // val viewModel: ConfirmationViewModel = hiltViewModel() // TEMPORARILY DISABLED
                // val uiState by viewModel.uiState.collectAsState() // TEMPORARILY DISABLED
                
                // Beautiful confirmation overlay
                ModernConfirmationOverlay(
                    appName = finalAppName,
                    packageName = packageName,
                    appIcon = getAppIcon(packageName),
                    onConfirm = {
                        // User confirmed - allow app to open
                        setResult(Activity.RESULT_OK)
                        finish()
                        // Launch the target app
                        launchTargetApp(packageName)
                    },
                    onCancel = {
                        setResult(Activity.RESULT_CANCELED)
                        finish()
                    }
                )
            }
        }
    }

     // Handle back press as cancellation
    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_CANCELED) // Signal cancellation
        finish()
    }

     // If activity is destroyed unexpectedly (e.g., config change while dialog is up), treat as cancel.
    override fun onDestroy() {
        // Ensure we set result if finishing for reasons other than button press
        if (!isFinishing) { // Check if it's an unexpected destruction
            setResult(Activity.RESULT_CANCELED)
        }
        super.onDestroy()
    }

    private fun getAppIcon(packageName: String): Drawable? {
        return try {
            packageManager.getApplicationIcon(packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            // Return a default icon or null if the app is not found
            null
        }
    }

    private fun launchTargetApp(packageName: String) {
        try {
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(launchIntent)
            }
        } catch (e: Exception) {
            // App might not be launchable or installed
            android.util.Log.e("ConfirmationActivity", "Failed to launch app: $packageName", e)
        }
    }
}

@Composable
fun ModernConfirmationOverlay(
    appName: String,
    packageName: String,
    appIcon: Drawable?,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    // Semi-transparent background overlay
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        // Main confirmation card
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = UnswipeCard
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Concentric circles background effect
                Box(
                    modifier = Modifier.size(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer circle
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(
                                color = UnswipeGray.copy(alpha = 0.1f),
                                shape = CircleShape
                            )
                    )
                    
                    // Middle circle
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .background(
                                color = UnswipeGray.copy(alpha = 0.15f),
                                shape = CircleShape
                            )
                    )
                    
                    // Inner circle with app icon
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(
                                color = UnswipeCard,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (appIcon != null) {
                            Image(
                                painter = rememberDrawablePainter(drawable = appIcon),
                                contentDescription = "$appName icon",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        } else {
                            // Fallback icon
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                UnswipePrimary,
                                                UnswipeSecondary
                                            )
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = appName.take(1).uppercase(),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = UnswipeBlack,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Question text with context-aware messaging
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Do you really want to open $appName?",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = UnswipeTextPrimary,
                            lineHeight = 28.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                    
                    // Add contextual message for social media apps
                    if (AppNameMapper.isSocialMediaApp(packageName)) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Consider your digital wellness goals 📱",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = UnswipeTextSecondary,
                                fontSize = 14.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                    } else if (AppNameMapper.isEntertainmentApp(packageName)) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Remember your time goals ⏰",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = UnswipeTextSecondary,
                                fontSize = 14.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Action buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Yes button
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = UnswipePrimary.copy(alpha = 0.9f),
                            contentColor = UnswipeBlack
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(
                            text = "Yes",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        )
                    }
                    
                    // No button
                    Button(
                        onClick = onCancel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = UnswipeRed,
                            contentColor = UnswipeTextPrimary
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(
                            text = "No",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConfirmationDialogContent(
    appName: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    // Use AlertDialog for standard dialog appearance consistent with Material 3
    AlertDialog(
        onDismissRequest = onCancel, // Dismissing the dialog via back press or outside click is treated as cancelling
        title = { Text(stringResource(id = R.string.confirmation_title)) }, // Use string resources
        text = { Text(stringResource(id = R.string.confirmation_message, appName)) }, // Use formatted string resource
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(stringResource(id = R.string.confirmation_confirm_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { // Use TextButton for less emphasis on cancel
                Text(stringResource(id = R.string.confirmation_cancel_button))
            }
        },
        // Ensure dialog properties match the desired behavior
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    )
} 