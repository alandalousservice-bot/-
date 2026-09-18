package com.example.schoolplatform.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.schoolplatform.data.model.*
import com.example.schoolplatform.data.repository.SchoolRepository
import com.example.schoolplatform.ui.animation.bounceClick
import com.example.schoolplatform.ui.components.SchoolTopBar
import com.example.schoolplatform.ui.components.StudentAvatar
import com.example.schoolplatform.ui.theme.*

@Composable
fun RestaurantScreen(
    onNavigateLanding: () -> Unit,
    onNavigateLogin: () -> Unit,
    onNavigateNetworkSync: () -> Unit = {},
    onNavigateTvKiosk: () -> Unit = {}
) {
    val currentUser by SchoolRepository.currentUser.collectAsState()
    val dailyMeal by SchoolRepository.dailyMeal.collectAsState()
    val mealConsumptions by SchoolRepository.mealConsumptions.collectAsState()
    val students by SchoolRepository.students.collectAsState()
    val inventory by SchoolRepository.inventory.collectAsState()

    var barcodeInput by remember { mutableStateOf("") }
    var scanResult by remember { mutableStateOf<MealScanResult?>(null) }
    var selectedMealPeriod by remember { mutableStateOf(MealPeriod.LUNCH) }

    Scaffold(
        containerColor = PaperBackground,
        topBar = {
            SchoolTopBar(
                title = "ماسح المطعم المدرسي",
                currentUser = currentUser,
                onNavigateLanding = onNavigateLanding,
                onNavigateLogin = onNavigateLogin,
                onNavigateNetworkSync = onNavigateNetworkSync,
                onNavigateTvKiosk = onNavigateTvKiosk
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Meal Info Header
            item {
                Surface(
                    color = CardSurface,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, BorderLight),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("🍲", fontSize = 24.sp)
                                Column {
                                    Text("قائمة وجبة اليوم", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(dailyMeal.description, fontSize = 11.sp, color = TextSecondary)
                                }
                            }

                            // Period selector chips
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                FilterChip(
                                    selected = selectedMealPeriod == MealPeriod.LUNCH,
                                    onClick = { selectedMealPeriod = MealPeriod.LUNCH },
                                    label = { Text("الغداء", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = SchoolGreen,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Stats counters
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = SurfaceTint,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("${mealConsumptions.size}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = SchoolGreen)
                                    Text("وجبات قُدمت اليوم", fontSize = 10.sp, color = TextSecondary)
                                }
                            }
                            Surface(
                                color = SchoolGoldLight,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("${students.size}", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF785408))
                                    Text("تلاميذ مسجلين", fontSize = 10.sp, color = TextSecondary)
                                }
                            }
                        }
                    }
                }
            }

            // Scanner Input Card
            item {
                Surface(
                    color = CardSurface,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, BorderLight),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "مسح باركود بطاقة التلميذ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = SchoolGreenDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = barcodeInput,
                                onValueChange = { barcodeInput = it },
                                placeholder = { Text("أدخل أو امسح الباركود...", fontSize = 12.sp) },
                                leadingIcon = { Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = SchoolGreen) },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Button(
                                onClick = {
                                    if (barcodeInput.isNotBlank()) {
                                        scanResult = SchoolRepository.scanMeal(barcodeInput, selectedMealPeriod)
                                        barcodeInput = ""
                                    }
                                },
                                modifier = Modifier.bounceClick(),
                                colors = ButtonDefaults.buttonColors(containerColor = SchoolGreen),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
                            ) {
                                Text("تأكيد", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Test Chips
                        Text("أو اختر تلميذاً للاختبار السريع:", fontSize = 11.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            students.take(5).forEach { student ->
                                OutlinedButton(
                                    onClick = {
                                        scanResult = SchoolRepository.scanMeal(student.barcode, selectedMealPeriod)
                                    },
                                    modifier = Modifier.bounceClick(scaleDown = 0.94f),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    border = BorderStroke(1.dp, BorderLight)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        StudentAvatar(
                                            fullName = student.fullName,
                                            studentId = student.id,
                                            size = 20.dp,
                                            showBorder = false
                                        )
                                        Text("${student.fullName} (${student.barcode})", fontSize = 10.sp, color = TextPrimary)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Scan Result Card with Animated Visibility
            scanResult?.let { result ->
                item {
                    val (bg, border, icon, titleColor) = when (result) {
                        is MealScanResult.Success -> Quad(SuccessBg, SuccessGreen, Icons.Default.CheckCircle, SuccessGreen)
                        is MealScanResult.AlreadyTaken -> Quad(WarningBg, WarningOrange, Icons.Default.Warning, WarningOrange)
                        is MealScanResult.AbsentWarning -> Quad(WarningBg, WarningOrange, Icons.Default.Info, WarningOrange)
                        is MealScanResult.NotFound -> Quad(DangerBg, DangerRed, Icons.Default.Error, DangerRed)
                    }

                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(tween(250)) + expandVertically(tween(250)),
                        exit = fadeOut(tween(200)) + shrinkVertically(tween(200))
                    ) {
                        Surface(
                            color = bg,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, border),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(icon, contentDescription = null, tint = titleColor, modifier = Modifier.size(32.dp))
                                Column {
                                    result.studentName?.let { name ->
                                        Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                                    }
                                    Text(result.message, fontSize = 12.sp, color = titleColor, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }

            // Consumed Meals Log
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("سجل الوجبات المصروفة اليوم", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SchoolGreenDark)
                    Text("${mealConsumptions.size} مستفيد", fontSize = 11.sp, color = TextSecondary)
                }
            }

            if (mealConsumptions.isEmpty()) {
                item {
                    Text("لم يتم مسح أي وجبة بعد لهذا اليوم.", fontSize = 12.sp, color = TextMuted)
                }
            } else {
                items(mealConsumptions, key = { it.id }) { c ->
                    Surface(
                        color = CardSurface,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, BorderLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("🍱", fontSize = 16.sp)
                                Text(c.studentName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            Text(c.scannedAt, fontSize = 11.sp, color = TextMuted)
                        }
                    }
                }
            }

            // Canteen Inventory Summary
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text("حالة مخزون المطعم", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SchoolGreenDark)
            }

            items(inventory, key = { it.id }) { item ->
                Surface(
                    color = CardSurface,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, if (item.isLow) DangerRed.copy(alpha = 0.4f) else BorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("الحد الأدنى للتنبيه: ${item.lowStockThreshold} ${item.unit}", fontSize = 10.sp, color = TextMuted)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (item.isLow) {
                                Surface(color = DangerBg, shape = RoundedCornerShape(6.dp)) {
                                    Text("مخزون منخفض!", color = DangerRed, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                            Text("${item.quantity} ${item.unit}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = if (item.isLow) DangerRed else SchoolGreenDark)
                        }
                    }
                }
            }
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
