package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val AirNineDarkColorScheme = darkColorScheme(
    primary = CyanAccent,
    onPrimary = NavyDarkBackground,
    primaryContainer = ElectricBlue,
    onPrimaryContainer = TextPrimary,
    secondary = ElectricBlueBright,
    onSecondary = TextPrimary,
    secondaryContainer = NavySurfaceVariant,
    onSecondaryContainer = CyanAccent,
    tertiary = NeonTeal,
    onTertiary = NavyDarkBackground,
    background = NavyDarkBackground,
    onBackground = TextPrimary,
    surface = NavySurface,
    onSurface = TextPrimary,
    surfaceVariant = NavySurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = NavyBorder,
    outlineVariant = NavyCard,
    error = ErrorRed,
    onError = TextPrimary
)

private val AirNineLightColorScheme = lightColorScheme(
    primary = ElectricBlue,
    onPrimary = LightSurface,
    primaryContainer = CyanAccent,
    onPrimaryContainer = NavyDarkBackground,
    secondary = ElectricBlueBright,
    onSecondary = LightSurface,
    secondaryContainer = LightSurfaceVariant,
    onSecondaryContainer = LightTextPrimary,
    tertiary = NeonTeal,
    onTertiary = NavyDarkBackground,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorder,
    outlineVariant = LightCard,
    error = ErrorRed,
    onError = LightSurface
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to Air Nine signature Dark Navy
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) AirNineDarkColorScheme else AirNineLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
