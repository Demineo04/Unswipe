package com.unswipe.android.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// EXAGGERATED MINIMALISM LIGHT THEME - Pure white background, pure black text
private val MinimalistLightColorScheme = lightColorScheme(
    primary = MinimalistBlack,
    onPrimary = MinimalistWhite,
    primaryContainer = MinimalistWhite,
    onPrimaryContainer = MinimalistBlack,
    secondary = MinimalistBlack,
    onSecondary = MinimalistWhite,
    secondaryContainer = MinimalistWhite,
    onSecondaryContainer = MinimalistBlack,
    tertiary = MinimalistBlack,
    onTertiary = MinimalistWhite,
    tertiaryContainer = MinimalistWhite,
    onTertiaryContainer = MinimalistBlack,
    error = MinimalistBlack,
    onError = MinimalistWhite,
    errorContainer = MinimalistWhite,
    onErrorContainer = MinimalistBlack,
    background = MinimalistWhite,
    onBackground = MinimalistBlack,
    surface = MinimalistWhite,
    onSurface = MinimalistBlack,
    surfaceVariant = MinimalistWhite,
    onSurfaceVariant = MinimalistBlack,
    outline = MinimalistBlack,
    inverseOnSurface = MinimalistWhite,
    inverseSurface = MinimalistBlack,
    inversePrimary = MinimalistWhite,
    surfaceTint = MinimalistBlack,
    outlineVariant = MinimalistBlack,
    scrim = MinimalistBlack,
)

// EXAGGERATED MINIMALISM DARK THEME - Pure black background, pure white text
private val MinimalistDarkColorScheme = darkColorScheme(
    primary = MinimalistWhite,
    onPrimary = MinimalistBlack,
    primaryContainer = MinimalistBlack,
    onPrimaryContainer = MinimalistWhite,
    secondary = MinimalistWhite,
    onSecondary = MinimalistBlack,
    secondaryContainer = MinimalistBlack,
    onSecondaryContainer = MinimalistWhite,
    tertiary = MinimalistWhite,
    onTertiary = MinimalistBlack,
    tertiaryContainer = MinimalistBlack,
    onTertiaryContainer = MinimalistWhite,
    error = MinimalistWhite,
    onError = MinimalistBlack,
    errorContainer = MinimalistBlack,
    onErrorContainer = MinimalistWhite,
    background = MinimalistBlack,
    onBackground = MinimalistWhite,
    surface = MinimalistBlack,
    onSurface = MinimalistWhite,
    surfaceVariant = MinimalistBlack,
    onSurfaceVariant = MinimalistWhite,
    outline = MinimalistWhite,
    inverseOnSurface = MinimalistBlack,
    inverseSurface = MinimalistWhite,
    inversePrimary = MinimalistBlack,
    surfaceTint = MinimalistWhite,
    outlineVariant = MinimalistWhite,
    scrim = MinimalistBlack,
)

// EXAGGERATED MINIMALISM THEME
@Composable
fun UnswipeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Disable dynamic colors for pure minimalism
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Always use our minimalist color schemes - no dynamic colors
    val colorScheme = if (darkTheme) {
        MinimalistDarkColorScheme
    } else {
        MinimalistLightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Set status bar to match background for seamless minimalism
            window.statusBarColor = colorScheme.background.toArgb()
            // Set navigation bar to match for complete immersion
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MinimalistTypography, // Use minimalist typography
        content = content
    )
}

// Alternative theme function for forcing light mode minimalism
@Composable
fun UnswipeMinimalistLightTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = MinimalistWhite.toArgb()
            window.navigationBarColor = MinimalistWhite.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = MinimalistLightColorScheme,
        typography = MinimalistTypography,
        content = content
    )
}

// Alternative theme function for forcing dark mode minimalism
@Composable
fun UnswipeMinimalistDarkTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = MinimalistBlack.toArgb()
            window.navigationBarColor = MinimalistBlack.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = MinimalistDarkColorScheme,
        typography = MinimalistTypography,
        content = content
    )
}