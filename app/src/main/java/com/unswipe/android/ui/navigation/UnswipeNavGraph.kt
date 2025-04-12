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
// Import other ViewModels and Screens as you create them (e.g., Settings)

// Sealed class defining the different screens/destinations in your app
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    // Add other screens like object Settings : Screen("settings")
}

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
    val startDestination = when (authState) {
        is AuthViewModel.AuthState.Authenticated -> Screen.Dashboard.route
        else -> Screen.Login.route // Default to Login if loading or unauthenticated
    }

    // NavHost defines the container for navigation
    NavHost(navController = navController, startDestination = startDestination) {

        // Define the Login screen destination
        composable(Screen.Login.route) {
            // Call the LoginScreen composable
            LoginScreen(
                // Pass the SAME AuthViewModel instance down
                viewModel = authViewModel,
                // Lambda function to navigate to the Register screen
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                // Lambda function to navigate to Dashboard on successful login
                onLoginSuccess = {
                    // Navigate to Dashboard and clear the auth screens from the backstack
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                        launchSingleTop = true // Avoid multiple Dashboard instances
                    }
                }
            )
        }

        // Define the Register screen destination
        composable(Screen.Register.route) {
            // Call the RegisterScreen composable
            RegisterScreen(
                // Pass the SAME AuthViewModel instance down
                viewModel = authViewModel,
                // Lambda function to navigate back (likely to Login)
                onNavigateToLogin = { navController.popBackStack() },
                // Lambda function to navigate to Dashboard on successful registration
                onRegisterSuccess = {
                    // Navigate to Dashboard and clear the auth screens from the backstack
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                        launchSingleTop = true // Avoid multiple Dashboard instances
                    }
                }
            )
        }

        // Define the Dashboard screen destination
        composable(Screen.Dashboard.route) {
            // Get the DashboardViewModel using Hilt, scoped to this destination
            val dashboardViewModel: DashboardViewModel = hiltViewModel()

            // Call the DashboardScreen composable
            DashboardScreen(
                viewModel = dashboardViewModel,
                // Lambda function to navigate to settings (implement later)
                onNavigateToSettings = { /* navController.navigate(Screen.Settings.route) */ },
                // Lambda function to handle logout
                onLogout = {
                    authViewModel.logout() // Call logout on the shared AuthViewModel
                    // Navigation back to Login will happen automatically when
                    // the 'authState' changes (observed at the top of UnswipeNavGraph)
                    // and the NavHost recomposes with the new startDestination.
                    // Optionally, add explicit navigation for immediate feedback:
                    // navController.navigate(Screen.Login.route) {
                    //    popUpTo(Screen.Dashboard.route) { inclusive = true }
                    // }
                }
            )
        }

        // Add other destinations like Settings here later
        // composable(Screen.Settings.route) {
        //      val settingsViewModel: SettingsViewModel = hiltViewModel()
        //      SettingsScreen(viewModel = settingsViewModel, ...)
        // }
    }
}