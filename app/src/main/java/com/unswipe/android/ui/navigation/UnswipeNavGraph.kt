package com.unswipe.android.ui.navigation // Ensure package is correct

// --- NECESSARY IMPORTS ---
import androidx.compose.runtime.*
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
import com.unswipe.android.domain.repository.OnboardingRepository
// Import your actual Screen definition:
import com.unswipe.android.ui.navigation.Screen // <-- Ensure this points to your central Screen definition
import androidx.compose.material3.Text
import com.unswipe.android.ui.onboarding.WakeupTimeScreen
import com.unswipe.android.ui.onboarding.WorkTimeScreen
import com.unswipe.android.ui.onboarding.SleepTimeScreen
import com.unswipe.android.ui.settings.AppSelectionScreen
import com.unswipe.android.ui.settings.SettingsScreen
import com.unswipe.android.ui.settings.DailyLimitScreen
import com.unswipe.android.ui.permissions.PermissionRequestScreen

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
    authViewModel: AuthViewModel = hiltViewModel(),
    onboardingRepository: OnboardingRepository = hiltViewModel()
) {
    // Observe the authentication state from the AuthViewModel
    // 'getValue' import is needed for the 'by' keyword
    val authState by authViewModel.authState.collectAsState()
    
    // Check onboarding completion status
    var isOnboardingComplete by remember { mutableStateOf<Boolean?>(null) }
    
    LaunchedEffect(authState) {
        if (authState is AuthViewModel.AuthState.Authenticated) {
            isOnboardingComplete = onboardingRepository.isOnboardingComplete()
        }
    }

    // Determine the starting screen based on auth and onboarding status
    val startDestination = when {
        authState !is AuthViewModel.AuthState.Authenticated -> Screen.Login.route
        isOnboardingComplete == false -> Screen.WakeupTime.route // Start onboarding
        isOnboardingComplete == true -> Screen.Dashboard.route // Go to dashboard
        else -> Screen.Login.route // Loading state, default to login
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
                },
                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) }
            )
        }

        // Define the Register screen destination
        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = {
                    // Go directly to onboarding after successful registration
                    navController.navigate(Screen.WakeupTime.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        // Define the Forgot Password screen placeholder
        composable(Screen.ForgotPassword.route) {
            // You can build this screen out later
            Text("Forgot Password Screen - Coming Soon!")
        }



        // --- ONBOARDING ---
        composable(Screen.WakeupTime.route) {
            WakeupTimeScreen(
                onNavigateToNext = {
                    navController.navigate(Screen.WorkTime.route)
                }
            )
        }

        composable(Screen.WorkTime.route) {
            WorkTimeScreen(
                onNavigateToNext = {
                    navController.navigate(Screen.SleepTime.route)
                }
            )
        }

        composable(Screen.SleepTime.route) {
            SleepTimeScreen(
                onNavigateToNext = {
                    navController.navigate(Screen.PermissionRequest.route)
                }
            )
        }

        // --- PERMISSIONS ---
        composable(Screen.PermissionRequest.route) {
            PermissionRequestScreen(
                onNavigateNext = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true } // Clear the entire auth/onboarding back stack
                        launchSingleTop = true
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Define the Dashboard screen destination
        composable(Screen.Dashboard.route) {
            val dashboardViewModel: DashboardViewModel = hiltViewModel()
            DashboardScreen(
                viewModel = dashboardViewModel,
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onLogout = {
                    authViewModel.logout()
                }
            )
        }

        // --- SETTINGS ---
        composable(Screen.AppSelection.route) {
            AppSelectionScreen()
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateTo = { route -> navController.navigate(route) },
                onLogout = { authViewModel.logout() }
            )
        }
        
        composable(Screen.DailyLimit.route) {
            DailyLimitScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}