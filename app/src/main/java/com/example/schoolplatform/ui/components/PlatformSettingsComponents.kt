package com.example.schoolplatform.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.schoolplatform.ui.animation.bounceClick
import com.example.schoolplatform.ui.theme.*

/**
 * Dialog to configure Platform Settings with focus on Dark Mode and visual ergonomics.
 */
@Composable
fun PlatformSettingsDialog(
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = CardSurface,
            shadowElevation = 8.dp,
            border = BorderStroke(1.dp, BorderLight),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.88f)
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Dialog Header
                Surface(
                    color = SchoolGreen,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                color = SchoolGold.copy(alpha = 0.25f),
                                shape = CircleShape,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = null,
                                        tint = SchoolGold,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "إعدادات المنصة وتجربة العرض",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "تخصيص الوضع الليلي والإضاءة المريحة",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.bounceClick()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "إغلاق",
                                tint = Color.White
                            )
                        }
                    }
                }

                // Scrollable Content
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    PlatformSettingsContent()
                }

                // Dialog Footer
                Surface(
                    color = SurfaceTint,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SchoolGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "يتم تطبيق وحفظ التغييرات فورياً",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.bounceClick(),
                            colors = ButtonDefaults.buttonColors(containerColor = SchoolGreen),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
                        ) {
                            Text("تم والإغلاق", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Reusable full settings content (used in dialog and in Admin Dashboard settings tab).
 */
@Composable
fun PlatformSettingsContent() {
    val themeMode by ThemeManager.themeMode.collectAsState()
    val isDark = LocalDarkTheme.current

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Hero Card: Main Dark Mode Switch with Visual Glow
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isDark) Color(0xFF14241E) else Color(0xFFF1F8F4),
            border = BorderStroke(1.5.dp, if (isDark) SchoolGoldDark else SchoolGreen.copy(alpha = 0.3f)),
            shadowElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Animated Moon/Sun Avatar
                        Surface(
                            shape = CircleShape,
                            color = if (isDark) Color(0xFF2E3E34) else SchoolGoldLight,
                            border = BorderStroke(1.dp, if (isDark) SchoolGold else SchoolGoldAccent),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (isDark) "🌙" else "☀️",
                                    fontSize = 22.sp
                                )
                            }
                        }

                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "الوضع الليلي (Dark Mode)",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary
                                )
                                Surface(
                                    color = if (isDark) SchoolGold.copy(alpha = 0.2f) else SchoolGreen.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = if (isDark) "مفعّل" else "غير مفعّل",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) SchoolGold else SchoolGreen,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "راحة مثالية للعينين في ظروف الإضاءة الخافتة والمسائية",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    // Material 3 Switch with animated colors
                    Switch(
                        checked = isDark,
                        onCheckedChange = { checked ->
                            ThemeManager.setThemeMode(if (checked) ThemeMode.DARK else ThemeMode.LIGHT)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = SchoolGold,
                            uncheckedThumbColor = SchoolGreen,
                            uncheckedTrackColor = SurfaceTint
                        ),
                        thumbContent = {
                            Icon(
                                imageVector = if (isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (isDark) SchoolGreenDark else Color.White
                            )
                        },
                        modifier = Modifier.bounceClick()
                    )
                }

                HorizontalDivider(color = BorderLight)

                // 3 Selectable Modes (Light, Dark, System)
                Text(
                    text = "اختر وضع العرض المفضل للمؤسسة:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeModeCard(
                        title = "نهاري",
                        subtitle = "إضاءة ساطعة",
                        emoji = "☀️",
                        isSelected = themeMode == ThemeMode.LIGHT,
                        modifier = Modifier.weight(1f),
                        onClick = { ThemeManager.setThemeMode(ThemeMode.LIGHT) }
                    )

                    ThemeModeCard(
                        title = "ليلي",
                        subtitle = "حماية للعين",
                        emoji = "🌙",
                        isSelected = themeMode == ThemeMode.DARK,
                        modifier = Modifier.weight(1f),
                        onClick = { ThemeManager.setThemeMode(ThemeMode.DARK) }
                    )

                    ThemeModeCard(
                        title = "تلقائي",
                        subtitle = "حسب الجهاز",
                        emoji = "📱",
                        isSelected = themeMode == ThemeMode.SYSTEM,
                        modifier = Modifier.weight(1f),
                        onClick = { ThemeManager.setThemeMode(ThemeMode.SYSTEM) }
                    )
                }
            }
        }

        // Live Lighting Environment Preview Card
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = CardSurface,
            border = BorderStroke(1.dp, BorderLight),
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            tint = SchoolGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "معاينة حية للمظهر والتباين البصري",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Text(
                        text = if (isDark) "بيئة إضاءة منخفضة 🌙" else "بيئة إضاءة نهارية ☀️",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) SchoolGold else SchoolGreen
                    )
                }

                // Sample Miniature Platform Card to display real contrast
                Surface(
                    color = SurfaceTint,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = SchoolGreen,
                            shape = CircleShape,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🎓", fontSize = 16.sp)
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "متوسطة بن باديس - قاعة الأساتذة",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "نموذج وضوح النصوص والألوان الرسمية الخضراء والذهبية",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                        Surface(
                            color = SuccessBg,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "تباين ممتاز",
                                fontSize = 10.sp,
                                color = SuccessGreen,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }

        // Lighting Conditions & Health Guidance
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = CardSurface,
            border = BorderStroke(1.dp, BorderLight),
            shadowElevation = 1.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "فوائد الوضع الليلي لمختلف الأدوار المدرسية:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                LightingBenefitItem(
                    icon = "👨‍🏫",
                    title = "للأساتذة والمعلمين",
                    description = "تخفيف إجهاد العينين عند إدخال كشوف النقاط وتدوين الغيابات في الفترات المسائية المتأخرة بالمنزل."
                )

                LightingBenefitItem(
                    icon = "🍲",
                    title = "لمسيّر المطعم والمخازن",
                    description = "شاشات أوضح وتشتيت أقل أثناء التحضير الصباحي الباكر (الساعة 6:30 صباحاً) أو فحص المخزون."
                )

                LightingBenefitItem(
                    icon = "🛡️",
                    title = "للحراسة والإدارة الليلية",
                    description = "تقليل انبعاث الضوء الأزرق أثناء المداومات الليلية للحراس والعمال المهنيين مع إطالة عمر البطارية."
                )
            }
        }

        // Platform Information & Architecture
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = SurfaceTint,
            border = BorderStroke(1.dp, BorderLight),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "منظومة الرقمنة المدرسية الجزائرية",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SchoolGreenDark
                    )
                    Text(
                        text = "إصدار v2.4 (محلي وبدون إنترنت)",
                        fontSize = 10.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = "دعم كامل للربط الشبكي عبر المودام/Hotspot مع ميزة تبديل المظهر النهاري/الليلي فورياً دون الحاجة لإعادة التشغيل.",
                    fontSize = 10.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
private fun ThemeModeCard(
    title: String,
    subtitle: String,
    emoji: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) SchoolGold else BorderLight
    val bgColor = if (isSelected) {
        if (LocalDarkTheme.current) Color(0xFF24362E) else SchoolGoldLight
    } else {
        CardSurface
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = bgColor,
        border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, borderColor),
        shadowElevation = if (isSelected) 2.dp else 0.dp,
        modifier = modifier
            .bounceClick(scaleDown = 0.94f) { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(emoji, fontSize = 20.sp)
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) SchoolGreenDark else TextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 9.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LightingBenefitItem(
    icon: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(icon, fontSize = 16.sp, modifier = Modifier.padding(top = 2.dp))
        Column {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = description,
                fontSize = 10.sp,
                color = TextSecondary,
                lineHeight = 14.sp
            )
        }
    }
}
