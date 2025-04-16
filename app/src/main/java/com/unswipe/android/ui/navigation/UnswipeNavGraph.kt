package com.unswipe.android.ui.navigation // Ensure package is correct

// --- NECESSARY IMPORTS ---
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue // Needed for 'by collectAsState()' delegate
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.unswipe.android.ui.auth.AuthViewModel // Import ViewModel
import com.unswipe.android.ui.auth.LoginScreen // Import Screen Composable
import com.unswipe.android.ui.auth.RegisterScreen // Import Screen Composable
import com.unswipe.android.ui.dashboard.DashboardScreen // Import Screen Composable
import com.unswipe.android.ui.dashboard.DashboardViewModel // Import ViewModel
// Import your actual Screen definition:
import com.unswipe.android.ui.navigation.Screen // <-- Ensure this points to your central Screen definition

// Import other ViewModels and Screens as you create them (e.g., Settings)
// import com.unswipe.android.ui.settings.SettingsScreen
// import com.unswipe.android.ui.settings.SettingsViewModel


// --- DUPLICATE Screen definition REMOVED ---


// The main navigation graph composable
@Composable
fun UnswipeNavGraph(
    // Use rememberNavController to keep track of backstack and state
    navController: NavHostController = rememberNavController(),
    // Get the AuthViewModel using Hilt - shared across auth screens and potentially needed here
    authViewModel: AuthViewModel = hiltViewModel()
) {
    // Observe the authentication state from the AuthViewModel
    // 'getValue' import is needed for the 'by' keyword
    val authState by authViewModel.authState.collectAsState()

    // Determine the starting screen based on whether the user is authenticated
    // Use the routes defined in the central Screen sealed class
    val startDestination = when (authState) {
        is AuthViewModel.AuthState.Authenticated -> Screen.Dashboard.route // Use route from central Screen.kt
        else -> Screen.Login.route // Use route from central Screen.kt
    }

    // NavHost defines the container for navigation
    NavHost(navController = navController, startDestination = startDestination) {

        // Define the Login screen destination
        composable(Screen.Login.route) { // Use route from central Screen.kt
            // Call the LoginScreen composable
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }, // Use route from central Screen.kt
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) { // Use route from central Screen.kt
                        popUpTo(Screen.Login.route) { inclusive = true } // Use route from central Screen.kt
                        launchSingleTop = true
                    }
                }
            )
        }

        // Define the Register screen destination
        composable(Screen.Register.route) { // Use route from central Screen.kt
            // Call the RegisterScreen composable
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Screen.Dashboard.route) { // Use route from central Screen.kt
                        popUpTo(Screen.Login.route) { inclusive = true } // Use route from central Screen.kt
                        launchSingleTop = true
                    }
                }
            )
        }

        // Define the Dashboard screen destination
        composable(Screen.Dashboard.route) { // Use route from central Screen.kt
            // Get the DashboardViewModel using Hilt, scoped to this destination
            val dashboardViewModel: DashboardViewModel = hiltViewModel()

            // Call the DashboardScreen composable
            DashboardScreen(
                viewModel = dashboardViewModel,
                // Example navigation to Settings:
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }, // Use route from central Screen.kt
                onLogout = {
                    authViewModel.logout()
                    // Navigation will update automatically based on authState change
                }
            )
        }

        // Add other destinations like Settings here later
        // composable(Screen.Settings.route) { // Use route from central Screen.kt
        //      val settingsViewModel: SettingsViewModel = hiltViewModel()
        //      SettingsScreen(viewModel = settingsViewModel, ...)
        // }
    }
}