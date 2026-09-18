package com.example.schoolplatform.ui.theme

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode(
    val titleAr: String,
    val subtitleAr: String,
    val iconEmoji: String
) {
    LIGHT(
        titleAr = "الوضع النهاري",
        subtitleAr = "ألوان مدرسية ساطعة ومثالية لظروف الإضاءة العادية بالنهار",
        iconEmoji = "☀️"
    ),
    DARK(
        titleAr = "الوضع الليلي",
        subtitleAr = "واجهة داكنة مريحة للعينين في المناوبات المسائية وتوفر طاقة البطارية",
        iconEmoji = "🌙"
    ),
    SYSTEM(
        titleAr = "تلقائي (حسب النظام)",
        subtitleAr = "يتطابق تلقائياً مع مظهر وتفضيلات نظام تشغيل الجهاز",
        iconEmoji = "⚙️"
    )
}

/**
 * Manages the current theme mode across the entire Algerian School Platform applet.
 */
object ThemeManager {
    private val _themeMode = MutableStateFlow(ThemeMode.LIGHT)
    val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
    }

    fun toggleDarkMode() {
        _themeMode.value = if (_themeMode.value == ThemeMode.DARK) {
            ThemeMode.LIGHT
        } else {
            ThemeMode.DARK
        }
    }
}
