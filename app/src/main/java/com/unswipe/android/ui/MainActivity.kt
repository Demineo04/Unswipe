package com.unswipe.android.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.unswipe.android.ui.auth.AuthViewModel
import com.unswipe.android.ui.navigation.Screen
import com.unswipe.android.ui.navigation.UnswipeNavGraph
import com.unswipe.android.ui.theme.UnswipeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        installSplashScreen() // Call before super.onCreate()

        super.onCreate(savedInstanceState)

        setContent {
            UnswipeTheme {
                 // Determine start destination based on initial auth state check
                 // This prevents flickering between auth/main screens on startup
                 val authViewModel: AuthViewModel = hiltViewModel()
                 val authState by authViewModel.authState.collectAsState() // Observe state

                 // Decide start destination before NavHost is composed
                 val startDest = when (authState) {
                     is AuthViewModel.AuthState.Authenticated -> Screen.Main.route
                     else -> Screen.Auth.route // Default to Auth flow (Login/Register)
                 }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Pass the determined start destination
                    UnswipeNavGraph(authViewModel = authViewModel, startDestination = startDest)
                }
            }
        }
    }
} 