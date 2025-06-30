package com.unswipe.android.ui.confirmation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.unswipe.android.R // Assuming you have strings defined
import com.unswipe.android.ui.theme.UnswipeTheme // Assuming Theme exists
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.asImageBitmap
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.unswipe.android.ui.components.EnhancedConfirmationDialog
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

        setContent {
            UnswipeTheme {
                val viewModel: ConfirmationViewModel = hiltViewModel()
                val uiState by viewModel.uiState.collectAsState()
                
                // Load app data when the activity starts
                LaunchedEffect(appName, packageName) {
                    if (packageName.isNotEmpty()) {
                        viewModel.loadAppData(appName, packageName)
                    }
                }
                
                val appIcon = getAppIcon(packageName)
                
                EnhancedConfirmationDialog(
                    uiState = uiState,
                    appIcon = if (appIcon != null) {
                        rememberDrawablePainter(drawable = appIcon)
                    } else {
                        painterResource(id = R.drawable.ic_launcher_foreground) // Fallback icon
                    },
                    onConfirm = {
                        viewModel.recordUserDecision(didProceed = true)
                        setResult(Activity.RESULT_OK)
                        finish()
                    },
                    onCancel = {
                        viewModel.recordUserDecision(didProceed = false)
                        setResult(Activity.RESULT_CANCELED)
                        finish()
                    },
                    onTakeBreak = {
                        viewModel.recordUserDecision(didProceed = false)
                        // TODO: Launch break activity or suggestions
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