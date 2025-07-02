// Location: app/src/main/java/com/unswipe/android/ui/MainActivity.kt

package com.unswipe.android.ui // Ensure this package matches your file location

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme // Import MaterialTheme
import androidx.compose.material3.Surface // Import Surface
import androidx.compose.ui.Modifier // Import Modifier
import com.unswipe.android.ui.navigation.UnswipeNavGraph // Import your NavGraph
import com.unswipe.android.ui.theme.UnswipeTheme // Import your Theme
import dagger.hilt.android.AndroidEntryPoint // Import AndroidEntryPoint

@AndroidEntryPoint // REQUIRED for Hilt injection in Activities
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        android.util.Log.d("MainActivity", "MainActivity onCreate() called")
        try {
            setContent { // setContent provides the initial Composable context
                // Apply your app's theme
                UnswipeTheme { // <-- Needs 'import com.unswipe.android.ui.theme.UnswipeTheme'
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(), // <-- Needs 'import androidx.compose.ui.Modifier'
                        color = MaterialTheme.colorScheme.background // <-- Needs 'import androidx.compose.material3.MaterialTheme'
                    ) {
                        // Set up the navigation graph for the app
                        // UnswipeNavGraph is responsible for getting ViewModels and state now
                        UnswipeNavGraph() // <-- Needs 'import com.unswipe.android.ui.navigation.UnswipeNavGraph'
                    }
                }
            }
            android.util.Log.d("MainActivity", "setContent completed successfully")
        } catch (e: Exception) {
            android.util.Log.e("MainActivity", "Error in onCreate: ${e.message}", e)
        }
    }
}