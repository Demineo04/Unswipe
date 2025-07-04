package com.unswipe.android.ui.navigation

// Defines the different screens/destinations in the app
sealed class Screen(val route: String) {
    // Splash
    object Splash : Screen("splash")
    
    // Auth Flow
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")

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
    object EditProfile : Screen("edit_profile")
    object ResetPassword : Screen("reset_password")
    object NotificationSettings : Screen("notification_settings")
    object FocusMode : Screen("focus_mode")
    object UsageAnalytics : Screen("usage_analytics")
    object SubscriptionManagement : Screen("subscription_management")
    object Help : Screen("help")
    object Support : Screen("support")
    
    // Detail Screens
    object UnlocksDetail : Screen("unlocks_detail")
    object AppLaunchesDetail : Screen("app_launches_detail")

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