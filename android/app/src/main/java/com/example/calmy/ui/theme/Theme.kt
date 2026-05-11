package com.example.calmy.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = CalmyPrimary,
    onPrimary = CalmySurface,
    primaryContainer = CalmyPrimaryLight,
    onPrimaryContainer = CalmyTextPrimary,
    secondary = CalmySecondary,
    onSecondary = CalmyTextPrimary,
    secondaryContainer = CalmySecondarySoft,
    onSecondaryContainer = CalmyTextPrimary,
    tertiary = CalmyAccent,
    onTertiary = CalmyTextPrimary,
    background = CalmyBackgroundPrimary,
    onBackground = CalmyTextPrimary,
    surface = CalmySurface,
    onSurface = CalmyTextPrimary,
    surfaceVariant = CalmySurfaceSoft,
    onSurfaceVariant = CalmyTextSecondary,
    outline = CalmyStroke,
    error = CalmyError,
    onError = CalmySurface
)

@Composable
fun CalmyTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
