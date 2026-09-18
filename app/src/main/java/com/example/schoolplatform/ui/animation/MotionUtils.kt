package com.example.schoolplatform.ui.animation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.schoolplatform.ui.theme.*

/**
 * Adds an interactive, tactile spring bounce animation to any clickable component.
 * Upon press, smoothly scales down, then springs back with organic elasticity.
 */
fun Modifier.bounceClick(
    scaleDown: Float = 0.94f,
    onClick: (() -> Unit)? = null
): Modifier = composed {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleDown else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "bounceScale"
    )

    this
        .scale(scale)
        .then(
            if (onClick != null) {
                Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
            } else {
                Modifier
            }
        )
}

/**
 * Animated school menu tab row featuring organic pill transitions,
 * spring-loaded scaling, and the Algerian school emerald & gold identity.
 */
@Composable
fun AnimatedSchoolTabRow(
    tabs: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    icons: List<String>? = null
) {
    val scrollState = rememberScrollState()

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = CardSurface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, title ->
                val isSelected = selectedTabIndex == index

                val animatedBgColor by animateColorAsState(
                    targetValue = if (isSelected) SchoolGreen else PaperBackground,
                    animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing),
                    label = "tabBgColor"
                )

                val animatedTextColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else TextSecondary,
                    animationSpec = tween(durationMillis = 200),
                    label = "tabTextColor"
                )

                val animatedScale by animateFloatAsState(
                    targetValue = if (isSelected) 1.02f else 0.98f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    label = "tabScale"
                )

                val animatedBorderColor by animateColorAsState(
                    targetValue = if (isSelected) SchoolGoldAccent else BorderLight,
                    animationSpec = tween(durationMillis = 240),
                    label = "tabBorderColor"
                )

                Surface(
                    modifier = Modifier
                        .scale(animatedScale)
                        .bounceClick(scaleDown = 0.93f) {
                            onTabSelected(index)
                        },
                    color = animatedBgColor,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, animatedBorderColor),
                    shadowElevation = if (isSelected) 3.dp else 0.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (icons != null && index < icons.size) {
                            Text(
                                icons[index],
                                fontSize = if (isSelected) 14.sp else 12.sp
                            )
                        }

                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = animatedTextColor
                        )

                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(SchoolGoldAccent)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Pulsing live status indicator (e.g. for Wi-Fi, Hotspot, and active sync in menus).
 */
@Composable
fun PulsingStatusDot(
    modifier: Modifier = Modifier,
    color: Color = SchoolGreen,
    size: Dp = 8.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(pulseScale)
            .clip(CircleShape)
            .background(color.copy(alpha = pulseAlpha))
    )
}

/**
 * Animated interactive card with subtle hover/press spring and gold-emerald edge glow.
 */
@Composable
fun AnimatedInteractiveCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isActive: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "cardScale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isPressed) 1.dp else if (isActive) 4.dp else 2.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "cardElevation"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isActive) SchoolGold else if (isPressed) SchoolGreenLight else BorderLight,
        animationSpec = tween(durationMillis = 180),
        label = "cardBorderColor"
    )

    Surface(
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        color = CardSurface,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(if (isActive) 1.5.dp else 1.dp, borderColor),
        shadowElevation = elevation
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            content = content
        )
    }
}
