package com.unswipe.android.ui.navigation

// Defines the different screens/destinations in the app
sealed class Screen(val route: String) {
    object Auth : Screen("auth_graph") // Nested graph for auth flow
    object Login : Screen("login")
    object Register : Screen("register")

    object Main : Screen("main_graph") // Nested graph for main app content
    object Dashboard : Screen("dashboard")
    object Settings : Screen("settings")
    // Add other screens like AppSelection, Premium, etc.
    object AppSelection : Screen("app_selection")
    object Premium : Screen("premium")

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