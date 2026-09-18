package com.example.schoolplatform.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalDarkTheme = compositionLocalOf { false }

// Base Algerian School Emerald and Gold Brand Palette
val SchoolGreen = Color(0xFF0F4C3A)
val SchoolGreenLight = Color(0xFF1B6B53)
val LightSchoolGreenDark = Color(0xFF07291F)
val DarkSchoolGreenDark = Color(0xFF76D6AF)

val SchoolGreenDark: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalDarkTheme.current) DarkSchoolGreenDark else LightSchoolGreenDark

val SchoolGold = Color(0xFFD59F38)
val SchoolGoldAccent = Color(0xFFE6B85C)
val SchoolGoldDark = Color(0xFF926315)

val LightSchoolGoldLight = Color(0xFFF7EFD9)
val DarkSchoolGoldLight = Color(0xFF38290E)
val SchoolGoldLight: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalDarkTheme.current) DarkSchoolGoldLight else LightSchoolGoldLight

val AcademicBlue = Color(0xFF1E3A8A)
val AcademicBlueLight = Color(0xFFDBEAFE)

// Theme Adaptive Backgrounds & Surfaces
val LightPaperBackground = Color(0xFFF8FAF8)
val DarkPaperBackground = Color(0xFF101B17)
val PaperBackground: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalDarkTheme.current) DarkPaperBackground else LightPaperBackground

val LightCardSurface = Color(0xFFFFFFFF)
val DarkCardSurface = Color(0xFF182620)
val CardSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalDarkTheme.current) DarkCardSurface else LightCardSurface

val LightSurfaceTint = Color(0xFFEDF6F0)
val DarkSurfaceTint = Color(0xFF1E332B)
val SurfaceTint: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalDarkTheme.current) DarkSurfaceTint else LightSurfaceTint

// Theme Adaptive Typography Colors
val LightTextPrimary = Color(0xFF152A22)
val DarkTextPrimary = Color(0xFFE2EBE5)
val TextPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalDarkTheme.current) DarkTextPrimary else LightTextPrimary

val LightTextSecondary = Color(0xFF5A6E66)
val DarkTextSecondary = Color(0xFFA2B5AC)
val TextSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalDarkTheme.current) DarkTextSecondary else LightTextSecondary

val LightTextMuted = Color(0xFF8A9E96)
val DarkTextMuted = Color(0xFF6F8279)
val TextMuted: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalDarkTheme.current) DarkTextMuted else LightTextMuted

// Borders & Dividers
val LightBorderLight = Color(0xFFDDE8E2)
val DarkBorderLight = Color(0xFF263D33)
val BorderLight: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalDarkTheme.current) DarkBorderLight else LightBorderLight

// Semantic Status Colors & Adaptive Backgrounds
val DangerRed = Color(0xFFC53030)
val LightDangerBg = Color(0xFFFFF5F5)
val DarkDangerBg = Color(0xFF381A1A)
val DangerBg: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalDarkTheme.current) DarkDangerBg else LightDangerBg

val SuccessGreen = Color(0xFF116149)
val LightSuccessBg = Color(0xFFE6F4EA)
val DarkSuccessBg = Color(0xFF143729)
val SuccessBg: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalDarkTheme.current) DarkSuccessBg else LightSuccessBg

val WarningOrange = Color(0xFFD97706)
val LightWarningBg = Color(0xFFFEF3C7)
val DarkWarningBg = Color(0xFF3A2B0E)
val WarningBg: Color
    @Composable
    @ReadOnlyComposable
    get() = if (LocalDarkTheme.current) DarkWarningBg else LightWarningBg

