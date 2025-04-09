package com.unswipe.android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.unswipe.android.ui.auth.AuthViewModel
import com.unswipe.android.ui.auth.LoginScreen
import com.unswipe.android.ui.auth.RegisterScreen
import com.unswipe.android.ui.dashboard.DashboardScreen
import com.unswipe.android.ui.dashboard.DashboardViewModel
import com.unswipe.android.ui.settings.SettingsScreen // Assuming SettingsScreen exists
import com.unswipe.android.ui.settings.SettingsViewModel // Assuming SettingsViewModel exists

@Composable
fun UnswipeNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = hiltViewModel(), // Shared AuthViewModel for auth state
    startDestination: String = Screen.Auth.route // Default to auth flow
) {
    val authState by authViewModel.authState.collectAsState()

    // Observe auth state and navigate accordingly
    LaunchedEffect(authState) {
        if (authState is AuthViewModel.AuthState.Authenticated) {
            // Navigate to main app graph, clearing auth backstack
            navController.navigate(Screen.Main.route) {
                popUpTo(Screen.Auth.route) { inclusive = true }
                launchSingleTop = true // Avoid multiple copies
            }
        } else if (authState is AuthViewModel.AuthState.Unauthenticated) {
            // Navigate to login screen within auth graph, clearing main backstack if user logs out
            if (navController.currentBackStackEntry?.destination?.route?.startsWith(Screen.Main.route) == true) {
                navController.navigate(Screen.Login.route) {
                     popUpTo(Screen.Main.route) { inclusive = true }
                     launchSingleTop = true
                }
            }
             // If already in auth graph, popping backstack might handle it, or stay put.
        }
    }


    NavHost(
        navController = navController,
        startDestination = startDestination, // Start with Auth or Main based on initial check? Auth is safer.
        modifier = modifier
    ) {
        authGraph(navController, authViewModel)
        mainGraph(navController, authViewModel) // Pass authViewModel for logout action
    }
}

// --- Authentication Flow ---
fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel // Receive the shared ViewModel
) {
    navigation(startDestination = Screen.Login.route, route = Screen.Auth.route) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel, // Use the shared instance
                onNavigateToRegister = { navController.navigate(Screen.Register.route) { launchSingleTop = true } },
                onLoginSuccess = { /* Navigation handled by LaunchedEffect */ }
            )
        }
        composable(Screen.Register.route) {
             RegisterScreen(
                 viewModel = authViewModel, // Use the shared instance
                 onNavigateToLogin = { navController.popBackStack() }, // Go back to login
                 onRegisterSuccess = { /* Navigation handled by LaunchedEffect */ }
             )
        }
    }
}

// --- Main App Content Flow ---
fun NavGraphBuilder.mainGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel // Needed for logout action
) {
    navigation(startDestination = Screen.Dashboard.route, route = Screen.Main.route) {
        composable(Screen.Dashboard.route) {
            val dashboardViewModel: DashboardViewModel = hiltViewModel()
            DashboardScreen(
                viewModel = dashboardViewModel,
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) { launchSingleTop = true } },
                // Add navigation actions for other dashboard items if needed
            )
        }
        composable(Screen.Settings.route) {
             val settingsViewModel: SettingsViewModel = hiltViewModel()
             SettingsScreen(
                 viewModel = settingsViewModel,
                 onLogout = {
                     authViewModel.logout()
                     // Navigation back to login is handled by LaunchedEffect observing authState
                 },
                 onNavigateBack = { navController.popBackStack() },
                 onNavigateToAppSelection = { /* navController.navigate(...) */ },
                 onNavigateToPremium = { /* navController.navigate(...) */ }
             )
        }
        // Add other destinations within the main app flow:
        // composable(Screen.AppSelection.route) { ... }
        // composable(Screen.Premium.route) { ... }
    }
} 