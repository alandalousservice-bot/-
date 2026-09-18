package com.example.schoolplatform.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.schoolplatform.data.model.Role
import com.example.schoolplatform.data.model.User
import com.example.schoolplatform.data.network.LocalNetworkManager
import com.example.schoolplatform.data.repository.SchoolRepository
import com.example.schoolplatform.ui.animation.PulsingStatusDot
import com.example.schoolplatform.ui.animation.bounceClick
import com.example.schoolplatform.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolTopBar(
    title: String,
    currentUser: User?,
    onNavigateLanding: () -> Unit,
    onNavigateLogin: () -> Unit,
    onNavigateNetworkSync: (() -> Unit)? = null,
    onNavigateTvKiosk: (() -> Unit)? = null
) {
    val users by SchoolRepository.users.collectAsState()
    val currentNetworkType by LocalNetworkManager.currentNetworkType.collectAsState()
    val localIp by LocalNetworkManager.localIp.collectAsState()
    var showUserMenu by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    val isDark = LocalDarkTheme.current

    Surface(
        color = SchoolGreen,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SchoolGreen,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SchoolBrandSeal(size = 38.dp)
                        Column {
                            Text(
                                text = "مدرسة مزيان عمار",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                color = Color.White
                            )
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                color = SchoolGold
                            )
                        }
                    }
                },
                actions = {
                    // Quick Dark Mode Toggle Button with dynamic Icon
                    IconButton(
                        onClick = { ThemeManager.toggleDarkMode() },
                        modifier = Modifier.bounceClick(),
                        colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                    ) {
                        Surface(
                            color = Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                                    contentDescription = if (isDark) "التبديل إلى الوضع النهاري" else "التبديل إلى الوضع الليلي",
                                    tint = if (isDark) SchoolGold else Color.White,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }
                    }

                    if (onNavigateTvKiosk != null) {
                        IconButton(
                            onClick = onNavigateTvKiosk,
                            modifier = Modifier.bounceClick(),
                            colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                        ) {
                            Surface(
                                color = SchoolGold.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Tv, contentDescription = "عرض الشاشة الذكية Smart TV", tint = SchoolGold, modifier = Modifier.size(19.dp))
                                }
                            }
                        }
                    }

                    if (onNavigateNetworkSync != null) {
                        IconButton(
                            onClick = onNavigateNetworkSync,
                            modifier = Modifier.bounceClick(),
                            colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                        ) {
                            Box {
                                Surface(
                                    color = Color.White.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Wifi, contentDescription = "فحص اتصال المودام وHotspot", modifier = Modifier.size(19.dp))
                                    }
                                }
                                PulsingStatusDot(
                                    color = SchoolGold,
                                    size = 8.dp,
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 2.dp, y = (-2).dp)
                                )
                            }
                        }
                    }

                    if (currentUser != null) {
                        // User role badge & Switcher
                        Box {
                            TextButton(
                                onClick = { showUserMenu = true },
                                modifier = Modifier.bounceClick(),
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = Color.White
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        color = SchoolGold.copy(alpha = 0.25f),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = currentUser.role.titleAr,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SchoolGold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.SwapHoriz,
                                        contentDescription = "تبديل الحساب",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = showUserMenu,
                                onDismissRequest = { showUserMenu = false }
                            ) {
                                Text(
                                    text = "التبديل السريع للمستخدم:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SchoolGreenDark,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                )
                                users.forEach { user ->
                                    val isSelected = user.id == currentUser.id
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column {
                                                    Text(
                                                        user.fullName,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                        fontSize = 13.sp,
                                                        color = if (isSelected) SchoolGreenDark else Color.Unspecified
                                                    )
                                                    Text(
                                                        "${user.role.titleAr} (${user.username})",
                                                        fontSize = 11.sp,
                                                        color = if (isSelected) SchoolGoldDark else Color.Gray
                                                    )
                                                }
                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = null,
                                                        tint = SchoolGold,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                        },
                                        onClick = {
                                            SchoolRepository.switchUser(user)
                                            showUserMenu = false
                                        }
                                    )
                                }
                                HorizontalDivider()
                                if (onNavigateTvKiosk != null) {
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text("شاشة العرض التلفزيوني Smart TV", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                Text("عرض معلومات المؤسسة على شاشات التلفاز", fontSize = 10.sp, color = SchoolGoldDark)
                                            }
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Default.Tv, contentDescription = null, tint = SchoolGoldDark)
                                        },
                                        onClick = {
                                            showUserMenu = false
                                            onNavigateTvKiosk()
                                        }
                                    )
                                    HorizontalDivider()
                                }
                                if (onNavigateNetworkSync != null) {
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text("فحص اتصال المودام والـ Hotspot", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                Text("IP: $localIp (${currentNetworkType.titleAr})", fontSize = 10.sp, color = SchoolGreenDark)
                                            }
                                        },
                                        leadingIcon = {
                                            Icon(Icons.Default.Wifi, contentDescription = null, tint = SchoolGreen)
                                        },
                                        onClick = {
                                            showUserMenu = false
                                            onNavigateNetworkSync()
                                        }
                                    )
                                    HorizontalDivider()
                                }
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text("إعدادات المنصة (الوضع الليلي)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                Text(
                                                    if (isDark) "الوضع الليلي مفعّل 🌙" else "الوضع النهاري مفعّل ☀️",
                                                    fontSize = 10.sp,
                                                    color = SchoolGoldDark
                                                )
                                            }
                                            Switch(
                                                checked = isDark,
                                                onCheckedChange = { ThemeManager.toggleDarkMode() },
                                                modifier = Modifier.scale(0.8f)
                                            )
                                        }
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Settings,
                                            contentDescription = null,
                                            tint = SchoolGold
                                        )
                                    },
                                    onClick = {
                                        showUserMenu = false
                                        showSettingsDialog = true
                                    }
                                )
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text("تسجيل الخروج", color = MaterialTheme.colorScheme.error) },
                                    leadingIcon = {
                                        Icon(Icons.Default.ExitToApp, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                    },
                                    onClick = {
                                        SchoolRepository.logout()
                                        showUserMenu = false
                                        onNavigateLogin()
                                    }
                                )
                            }
                        }
                    } else {
                        Button(
                            onClick = onNavigateLogin,
                            modifier = Modifier.bounceClick(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SchoolGold
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("دخول", fontSize = 12.sp, color = SchoolGreenDark, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            )
        }
    }

    if (showSettingsDialog) {
        PlatformSettingsDialog(
            onDismiss = { showSettingsDialog = false }
        )
    }
}
