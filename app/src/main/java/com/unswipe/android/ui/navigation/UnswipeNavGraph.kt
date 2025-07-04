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
import com.unswipe.android.ui.details.UnlocksDetailScreen
import com.unswipe.android.ui.details.AppLaunchesDetailScreen
import com.unswipe.android.ui.splash.SplashScreen
import com.unswipe.android.ui.onboarding.*

// Import other ViewModels and Screens as you create them (e.g., Settings)
// import com.unswipe.android.ui.settings.SettingsScreen
// import com.unswipe.android.ui.settings.SettingsViewModel


// --- DUPLICATE Screen definition REMOVED ---


// The main navigation graph composable
@Composable
fun UnswipeNavGraph(
    // Use rememberNavController to keep track of backstack and state
    navController: NavHostController = rememberNavController()
) {
    // Get the AuthViewModel at the navigation level so it can be shared across auth screens
    val authViewModel: AuthViewModel = hiltViewModel()
    
    // Start with splash screen for first launch experience
    val startDestination = Screen.Splash.route

    // NavHost defines the container for navigation
    NavHost(navController = navController, startDestination = startDestination) {

        // Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.WakeupTime.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

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
                    // Go directly to onboarding after successful registration for new users
                    navController.navigate(Screen.WakeupTime.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
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
                },
                onSkip = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.WakeupTime.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.WorkTime.route) {
            WorkTimeScreen(
                onNavigateToNext = {
                    navController.navigate(Screen.SleepTime.route)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.SleepTime.route) {
            SleepTimeScreen(
                onNavigateToNext = {
                    navController.navigate(Screen.PermissionRequest.route)
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // --- PERMISSIONS ---
        composable(Screen.PermissionRequest.route) {
            OnboardingPermissionsScreen(
                onNavigateToLogin = {
                    // After completing onboarding, go to dashboard
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.WakeupTime.route) { inclusive = true } // Clear the entire onboarding back stack
                        launchSingleTop = true
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        // Define the Dashboard screen destination
        composable(Screen.Dashboard.route) {
            val dashboardViewModel: DashboardViewModel = hiltViewModel()
            DashboardScreen(
                viewModel = dashboardViewModel,
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToUnlocksDetail = { navController.navigate(Screen.UnlocksDetail.route) },
                onNavigateToAppLaunchesDetail = { navController.navigate(Screen.AppLaunchesDetail.route) },
                onLogout = {
                    authViewModel.logout()
                }
            )
        }

        // --- SETTINGS ---
        composable(Screen.AppSelection.route) {
            Text("App Selection Screen - Working!")
            // AppSelectionScreen()
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateTo = { route -> navController.navigate(route) },
                onLogout = { 
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
        
        composable(Screen.DailyLimit.route) {
            Text("Daily Limit Screen - Working!")
            /*
            DailyLimitScreen(
                onNavigateBack = { navController.popBackStack() }
            )
            */
        }
        
        // Detail Screens
        composable(Screen.UnlocksDetail.route) {
            UnlocksDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.AppLaunchesDetail.route) {
            AppLaunchesDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}