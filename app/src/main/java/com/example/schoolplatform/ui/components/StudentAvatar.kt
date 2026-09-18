package com.example.schoolplatform.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

/**
 * Avatar gradient palettes tailored to Algerian school educational theme,
 * creating vibrant, accessible, and polished circular placeholders.
 */
private data class AvatarPalette(
    val bgStart: Color,
    val bgEnd: Color,
    val textColor: Color,
    val borderColor: Color,
    val emojiBadge: String
)

private val AvatarPalettes = listOf(
    // Emerald School Green
    AvatarPalette(
        bgStart = Color(0xFF105C46),
        bgEnd = Color(0xFF073327),
        textColor = Color(0xFFFFFFFF),
        borderColor = Color(0xFF1B6B53).copy(alpha = 0.4f),
        emojiBadge = "🎒"
    ),
    // Warm Amber Gold
    AvatarPalette(
        bgStart = Color(0xFFE29B27),
        bgEnd = Color(0xFF9E6510),
        textColor = Color(0xFFFFFFFF),
        borderColor = Color(0xFFE6B85C).copy(alpha = 0.5f),
        emojiBadge = "⭐"
    ),
    // Mediterranean Deep Sky Blue
    AvatarPalette(
        bgStart = Color(0xFF2563EB),
        bgEnd = Color(0xFF1E3A8A),
        textColor = Color(0xFFFFFFFF),
        borderColor = Color(0xFF60A5FA).copy(alpha = 0.4f),
        emojiBadge = "📚"
    ),
    // Warm Coral Terracotta
    AvatarPalette(
        bgStart = Color(0xFFE15B46),
        bgEnd = Color(0xFFA83422),
        textColor = Color(0xFFFFFFFF),
        borderColor = Color(0xFFF87171).copy(alpha = 0.4f),
        emojiBadge = "🎨"
    ),
    // Royal Violet Plum
    AvatarPalette(
        bgStart = Color(0xFF7C3AED),
        bgEnd = Color(0xFF4C1D95),
        textColor = Color(0xFFFFFFFF),
        borderColor = Color(0xFFA78BFA).copy(alpha = 0.4f),
        emojiBadge = "✏️"
    ),
    // Teal Oasis
    AvatarPalette(
        bgStart = Color(0xFF0D9488),
        bgEnd = Color(0xFF115E59),
        textColor = Color(0xFFFFFFFF),
        borderColor = Color(0xFF2DD4BF).copy(alpha = 0.4f),
        emojiBadge = "📐"
    ),
    // Indigo Blue
    AvatarPalette(
        bgStart = Color(0xFF4F46E5),
        bgEnd = Color(0xFF312E81),
        textColor = Color(0xFFFFFFFF),
        borderColor = Color(0xFF818CF8).copy(alpha = 0.4f),
        emojiBadge = "🔬"
    ),
    // Forest Sage
    AvatarPalette(
        bgStart = Color(0xFF15803D),
        bgEnd = Color(0xFF14532D),
        textColor = Color(0xFFFFFFFF),
        borderColor = Color(0xFF4ADE80).copy(alpha = 0.4f),
        emojiBadge = "🌱"
    )
)

/**
 * Extracts initials from student name (first letter of first and last word).
 */
private fun extractInitials(fullName: String): String {
    val clean = fullName.trim()
    if (clean.isEmpty()) return "ت"
    val parts = clean.split("\\s+".toRegex()).filter { it.isNotBlank() }
    return when {
        parts.size >= 2 -> "${parts.first().take(1)}·${parts.last().take(1)}"
        else -> parts.first().take(2)
    }
}

/**
 * Circular student avatar placeholder with harmonious palette gradients,
 * Arabic monogram initials, subtle depth glow, and crisp high-contrast styling.
 */
@Composable
fun StudentAvatar(
    fullName: String,
    studentId: Int,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    showBorder: Boolean = true
) {
    val palette = remember(studentId, fullName) {
        val seed = abs(studentId * 31 + fullName.hashCode())
        AvatarPalettes[seed % AvatarPalettes.size]
    }
    val initials = remember(fullName) { extractInitials(fullName) }

    val fontSize = (size.value * 0.35f).sp

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .then(
                if (showBorder) {
                    Modifier.border(1.5.dp, palette.borderColor, CircleShape)
                } else Modifier
            )
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(palette.bgStart, palette.bgEnd)
                )
            )
    ) {
        Text(
            text = initials,
            color = palette.textColor,
            fontSize = fontSize,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = fontSize
        )
    }
}
