package com.unswipe.android.ui.navigation

// Defines the different screens/destinations in the app
sealed class Screen(val route: String) {
    // Auth Flow
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object OtpVerification : Screen("otp_verification")

    // Onboarding Flow
    object WakeupTime : Screen("wakeup_time")
    object WorkTime : Screen("work_time")
    object SleepTime : Screen("sleep_time")

    // Main App
    object Dashboard : Screen("dashboard")
    object Settings : Screen("settings")
    object AppSelection : Screen("app_selection")
    object Premium : Screen("premium") // For future use
    
    // Permission Management
    object PermissionRequest : Screen("permission_request")
    object UsageStatsPermission : Screen("usage_stats_permission")
    object AccessibilityPermission : Screen("accessibility_permission")
    
    // Settings Screens
    object DailyLimit : Screen("daily_limit")

    // Function to create routes with arguments (example)
    // fun withArgs(vararg args: String): String {
    //     return buildString {
    //         append(route)
    //         args.forEach { arg ->
    //             append("/$arg")
    //         }
    //     }
    // }
} 