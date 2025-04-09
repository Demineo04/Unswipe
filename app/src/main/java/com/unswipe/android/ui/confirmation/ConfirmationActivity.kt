package com.unswipe.android.ui.confirmation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.unswipe.android.R // Assuming you have strings defined
import com.unswipe.android.ui.theme.UnswipeTheme // Assuming Theme exists

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
        // val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME) // Use if needed

        // Apply dialog theme via AndroidManifest.xml or programmatically before setContent
        // See themes.xml example later

        setContent {
            UnswipeTheme { // Use your app's theme
                ConfirmationDialogContent(
                    appName = appName,
                    onConfirm = {
                        // User confirmed, allow original app launch attempt to proceed
                        setResult(Activity.RESULT_OK) // Signal confirmation (optional)
                        finish() // Close this confirmation activity
                    },
                    onCancel = {
                        // User cancelled, block original app launch
                        setResult(Activity.RESULT_CANCELED) // Signal cancellation (optional)
                        finish() // Close this confirmation activity
                        // The Accessibility Service *should not* perform actions here.
                        // Its job was to show this confirmation. Finishing this activity
                        // effectively stops the user flow towards the target app.
                        // Optional: Redirect to home screen explicitly? Can feel abrupt.
                        // val homeIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        // startActivity(homeIntent)
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