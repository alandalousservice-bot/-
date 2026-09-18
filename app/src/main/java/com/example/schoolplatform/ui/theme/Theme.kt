package com.example.schoolplatform.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = SchoolGreen,
    onPrimary = Color.White,
    primaryContainer = LightSurfaceTint,
    onPrimaryContainer = LightSchoolGreenDark,
    secondary = SchoolGold,
    onSecondary = Color.White,
    secondaryContainer = LightSchoolGoldLight,
    onSecondaryContainer = Color(0xFF523B06),
    tertiary = AcademicBlue,
    background = LightPaperBackground,
    surface = LightCardSurface,
    onBackground = LightTextPrimary,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceTint,
    onSurfaceVariant = LightTextSecondary,
    outline = LightBorderLight,
    error = DangerRed,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF76D6AF),
    onPrimary = Color(0xFF003827),
    primaryContainer = Color(0xFF0F4C3A),
    onPrimaryContainer = Color(0xFF93F2CB),
    secondary = Color(0xFFF5C564),
    onSecondary = Color(0xFF412D00),
    secondaryContainer = DarkSchoolGoldLight,
    onSecondaryContainer = Color(0xFFFFE088),
    tertiary = AcademicBlueLight,
    background = DarkPaperBackground,
    surface = DarkCardSurface,
    onBackground = DarkTextPrimary,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceTint,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorderLight,
    error = DangerRed,
    onError = Color.White
)

@Composable
fun SchoolPlatformTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(
        LocalDarkTheme provides darkTheme
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}

