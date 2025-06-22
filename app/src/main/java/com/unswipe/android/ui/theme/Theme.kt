package com.unswipe.android.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Define the light color scheme using the colors from Color.kt
private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    tertiary = LightTertiary,
    onTertiary = LightOnTertiary,
    tertiaryContainer = LightTertiaryContainer,
    onTertiaryContainer = LightOnTertiaryContainer,
    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    inverseOnSurface = LightInverseOnSurface,
    inverseSurface = LightInverseSurface,
    inversePrimary = LightInversePrimary,
    surfaceTint = LightSurfaceTint,
    outlineVariant = LightOutlineVariant,
    scrim = LightScrim,
)

// Define the dark color scheme using the colors from Color.kt
private val DarkColorScheme = darkColorScheme(
    primary = UnswipePrimary,
    onPrimary = UnswipeBlack,
    primaryContainer = UnswipeSurface,
    onPrimaryContainer = UnswipeTextPrimary,
    secondary = UnswipeSecondary,
    onSecondary = UnswipeBlack,
    secondaryContainer = UnswipeSurface,
    onSecondaryContainer = UnswipeTextPrimary,
    tertiary = UnswipeSecondary,
    onTertiary = UnswipeBlack,
    tertiaryContainer = UnswipeSurface,
    onTertiaryContainer = UnswipeTextPrimary,
    error = UnswipeRed,
    onError = UnswipeBlack,
    errorContainer = UnswipeSurface,
    onErrorContainer = UnswipeRed,
    background = UnswipeBlack,
    onBackground = UnswipeTextPrimary,
    surface = UnswipeSurface,
    onSurface = UnswipeTextPrimary,
    surfaceVariant = UnswipeSurface,
    onSurfaceVariant = UnswipeTextSecondary,
    outline = UnswipeTextSecondary,
    inverseOnSurface = UnswipeTextPrimary,
    inverseSurface = UnswipeBlack,
    inversePrimary = UnswipePrimary,
    surfaceTint = UnswipePrimary,
    outlineVariant = UnswipeTextSecondary,
    scrim = UnswipeBlack.copy(alpha = 0.5f),
)

// Your main theme composable
@Composable
fun UnswipeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // From Type.kt
        content = content
    )
}