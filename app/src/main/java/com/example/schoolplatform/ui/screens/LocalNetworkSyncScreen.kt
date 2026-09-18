package com.example.schoolplatform.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.schoolplatform.data.model.*
import com.example.schoolplatform.data.network.LocalNetworkManager
import com.example.schoolplatform.data.repository.SchoolRepository
import com.example.schoolplatform.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalNetworkSyncScreen(
    onNavigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val currentNetworkType by LocalNetworkManager.currentNetworkType.collectAsState()
    val localIp by LocalNetworkManager.localIp.collectAsState()
    val gatewayIp by LocalNetworkManager.gatewayIp.collectAsState()
    val targetHost by LocalNetworkManager.targetServerHost.collectAsState()
    val targetPort by LocalNetworkManager.targetServerPort.collectAsState()
    val deviceRole by LocalNetworkManager.deviceRole.collectAsState()
    val isSyncing by LocalNetworkManager.isSyncing.collectAsState()
    val lastSyncTime by LocalNetworkManager.lastSyncTime.collectAsState()
    val diagnosticSteps by LocalNetworkManager.diagnosticSteps.collectAsState()
    val syncLogs by LocalNetworkManager.syncLogs.collectAsState()
    val currentUser by SchoolRepository.currentUser.collectAsState()

    var isRunningDiag by remember { mutableStateOf(false) }
    var hostInput by remember { mutableStateOf(targetHost) }
    var portInput by remember { mutableStateOf(targetPort.toString()) }
    var showSuccessSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }

    LaunchedEffect(targetHost) {
        hostInput = targetHost
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "فحص اتصال الحسابات والمودام",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SchoolGreenDark
                        )
                        Text(
                            "مشاركة البيانات عبر نفس المودام أو هاتف وسيط (Hotspot)",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                },
                actions = {
                    val isDark = LocalDarkTheme.current
                    IconButton(onClick = { ThemeManager.toggleDarkMode() }) {
                        Icon(
                            imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (isDark) "التبديل إلى الوضع النهاري" else "التبديل إلى الوضع الليلي",
                            tint = if (isDark) SchoolGold else SchoolGreenDark
                        )
                    }
                    IconButton(onClick = {
                        LocalNetworkManager.refreshNetworkInfo()
                        snackbarMessage = "تم تحديث معلومات محول الشبكة"
                        showSuccessSnackbar = true
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "تحديث", tint = SchoolGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PaperBackground)
            )
        },
        containerColor = PaperBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 32.dp)
        ) {
            // 1. Current Network Overview Card
            item {
                Surface(
                    color = CardSurface,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BorderLight),
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                color = SchoolGreenLight,
                                shape = CircleShape,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(currentNetworkType.iconEmoji, fontSize = 22.sp)
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    currentNetworkType.titleAr,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = SchoolGreenDark
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(SchoolGreen)
                                    )
                                    Text(
                                        "متصل بالشبكة المحلية · التبادل مجاني دون استهلاك إنترنت",
                                        fontSize = 10.sp,
                                        color = SchoolGreen
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Divider(color = BorderLight)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            NetworkMetricBadge(
                                modifier = Modifier.weight(1f),
                                title = "عنوان IP المحلي لهذا الجهاز",
                                value = localIp,
                                subtitle = "ضمن نطاق الشبكة المحلية"
                            )
                            NetworkMetricBadge(
                                modifier = Modifier.weight(1f),
                                title = "بوابة المودام / الهاتف الوسيط",
                                value = gatewayIp,
                                subtitle = "Default Gateway"
                            )
                        }

                        if (lastSyncTime != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                                Text("آخر مزامنة ناجحة للحسابات: $lastSyncTime", fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            }

            // 2. Role Selector & Target Settings
            item {
                Surface(
                    color = CardSurface,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BorderLight),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "دور هذا الجهاز في المزامنة المحلية",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = SchoolGreenDark
                        )
                        Text(
                            "حدد ما إذا كان هذا الجهاز هو خادم الإدارة (المضيف) أم جهاز طرفي (أستاذ/مطعم)",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DeviceRoleOptionCard(
                                modifier = Modifier.weight(1f),
                                title = "هاتف الإدارة (مضيف)",
                                role = DeviceSyncRole.HOST,
                                isSelected = deviceRole == DeviceSyncRole.HOST,
                                icon = "👔",
                                onSelect = {
                                    LocalNetworkManager.setDeviceRole(DeviceSyncRole.HOST)
                                }
                            )
                            DeviceRoleOptionCard(
                                modifier = Modifier.weight(1f),
                                title = "هاتف طرفي (زبون)",
                                role = DeviceSyncRole.CLIENT,
                                isSelected = deviceRole == DeviceSyncRole.CLIENT,
                                icon = "📱",
                                onSelect = {
                                    LocalNetworkManager.setDeviceRole(DeviceSyncRole.CLIENT)
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text("عنوان خادم المنصة المستهدف (Target IP & Port):", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = hostInput,
                                onValueChange = {
                                    hostInput = it
                                    LocalNetworkManager.updateTargetServer(it, portInput.toIntOrNull() ?: 8000)
                                },
                                label = { Text("عنوان IP الخادم") },
                                modifier = Modifier.weight(2f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                            )

                            OutlinedTextField(
                                value = portInput,
                                onValueChange = {
                                    portInput = it
                                    LocalNetworkManager.updateTargetServer(hostInput, it.toIntOrNull() ?: 8000)
                                },
                                label = { Text("المنفذ") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("اختصارات سريعة لشبكة المدرسة:", fontSize = 11.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(
                                selected = hostInput == "192.168.43.1",
                                onClick = {
                                    hostInput = "192.168.43.1"
                                    LocalNetworkManager.updateTargetServer("192.168.43.1", 8000)
                                },
                                label = { Text("هاتف وسيط (43.1)", fontSize = 10.sp) }
                            )
                            FilterChip(
                                selected = hostInput == "192.168.1.1",
                                onClick = {
                                    hostInput = "192.168.1.1"
                                    LocalNetworkManager.updateTargetServer("192.168.1.1", 8000)
                                },
                                label = { Text("مودام (1.1)", fontSize = 10.sp) }
                            )
                            FilterChip(
                                selected = hostInput == localIp,
                                onClick = {
                                    hostInput = localIp
                                    LocalNetworkManager.updateTargetServer(localIp, 8000)
                                },
                                label = { Text("هذا الجهاز", fontSize = 10.sp) }
                            )
                        }
                    }
                }
            }

            // 3. Action Buttons (Diagnostics & Instant Sync)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isRunningDiag = true
                                LocalNetworkManager.runFullDiagnostics(hostInput, portInput.toIntOrNull() ?: 8000)
                                isRunningDiag = false
                                snackbarMessage = "اكتمل فحص اتصال الحسابات والمودام بنجاح!"
                                showSuccessSnackbar = true
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SchoolGreen),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isRunningDiag && !isSyncing
                    ) {
                        if (isRunningDiag) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("جارٍ الفحص...", fontSize = 13.sp)
                        } else {
                            Icon(Icons.Default.NetworkCheck, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("فحص الاتصال الآن", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val success = LocalNetworkManager.performInstantSync()
                                if (success) {
                                    snackbarMessage = "تمت مزامنة البيانات بين الحسابات بنجاح!"
                                    showSuccessSnackbar = true
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SchoolGoldDark),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isRunningDiag && !isSyncing
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("جارٍ المزامنة...", fontSize = 13.sp)
                        } else {
                            Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("مزامنة الحسابات الآن", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Snackbar confirmation
            if (showSuccessSnackbar) {
                item {
                    Surface(
                        color = SchoolGreenDark,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SchoolGoldAccent)
                                Text(snackbarMessage, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            IconButton(onClick = { showSuccessSnackbar = false }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            // 4. Diagnostic Step-by-Step Results
            item {
                Text(
                    "نتائج الفحص التشخيصي للشبكة المحلية:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = SchoolGreenDark
                )
            }

            items(diagnosticSteps) { step ->
                DiagnosticStepCard(step = step)
            }

            // 5. How-To Practical School Scenarios
            item {
                Surface(
                    color = SurfaceTint,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, BorderLight)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("💡", fontSize = 20.sp)
                            Text("دليل تشغيل الحسابات المشتركة في مدرسة مزيان عمار:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SchoolGreenDark)
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        ScenarioInstructionItem(
                            number = "1",
                            title = "طريقة الهاتف الوسيط (نقطة اتصال Hotspot):",
                            desc = "يفتح هاتف وسيط (أو هاتف المدير) ميزة 'نقطة اتصال الهواتف' (Mobile Hotspot). يتصل الأستاذ ومشرف المطعم بالواي فاي المنبثق عنه، وبذلك يصبح الجميع على نفس الشبكة المحلية مجاناً."
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        ScenarioInstructionItem(
                            number = "2",
                            title = "طريقة مودام المدرسة (Wi-Fi Router):",
                            desc = "يتصل هاتف الإدارة وهواتف المعلمين بالمودام المكتبي نفسه. يُكتشف الـ IP المحلي تلقائياً وتتم مزامنة الغيابات والوجبات في ثوانٍ معدودة."
                        )
                    }
                }
            }

            // 6. Sync Activity Log
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "سجل المزامنات التشاركية السابقة:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = SchoolGreenDark
                    )
                    Text("${syncLogs.size} عملية مسجلة", fontSize = 11.sp, color = TextMuted)
                }
            }

            items(syncLogs) { log ->
                SyncLogCard(log = log)
            }
        }
    }
}

@Composable
private fun NetworkMetricBadge(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String
) {
    Surface(
        modifier = modifier,
        color = PaperBackground,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, BorderLight)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(title, fontSize = 10.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = SchoolGreenDark)
            Text(subtitle, fontSize = 9.sp, color = TextMuted)
        }
    }
}

@Composable
private fun DeviceRoleOptionCard(
    modifier: Modifier = Modifier,
    title: String,
    role: DeviceSyncRole,
    isSelected: Boolean,
    icon: String,
    onSelect: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSelect() },
        color = if (isSelected) SchoolGreenLight else PaperBackground,
        border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) SchoolGreen else BorderLight),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isSelected) SchoolGreenDark else TextPrimary)
        }
    }
}

@Composable
private fun DiagnosticStepCard(step: DiagnosticStep) {
    Surface(
        color = CardSurface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderLight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            when (step.state) {
                DiagnosticStepState.IDLE -> {
                    Surface(color = PaperBackground, shape = CircleShape, modifier = Modifier.size(28.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("⏳", fontSize = 12.sp)
                        }
                    }
                }
                DiagnosticStepState.RUNNING -> {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp, color = SchoolGreen)
                }
                DiagnosticStepState.SUCCESS -> {
                    Surface(color = SuccessBg, shape = CircleShape, modifier = Modifier.size(28.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                DiagnosticStepState.WARNING -> {
                    Surface(color = WarningBg, shape = CircleShape, modifier = Modifier.size(28.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = WarningOrange, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                DiagnosticStepState.ERROR -> {
                    Surface(color = DangerBg, shape = CircleShape, modifier = Modifier.size(28.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = DangerRed, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(step.titleAr, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                Text(
                    if (step.resultMessage.isNotEmpty()) step.resultMessage else step.descriptionAr,
                    fontSize = 11.sp,
                    color = if (step.state == DiagnosticStepState.SUCCESS) SchoolGreenDark else TextSecondary
                )
                if (step.detailValue.isNotEmpty()) {
                    Text(step.detailValue, fontSize = 9.sp, color = TextMuted)
                }
            }
        }
    }
}

@Composable
private fun ScenarioInstructionItem(
    number: String,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            color = SchoolGreen,
            shape = CircleShape,
            modifier = Modifier.size(20.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(number, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SchoolGreenDark)
            Text(desc, fontSize = 11.sp, color = TextSecondary, lineHeight = 16.sp)
        }
    }
}

@Composable
private fun SyncLogCard(log: SyncLogEntry) {
    Surface(
        color = CardSurface,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, BorderLight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                color = if (log.success) SuccessBg else DangerBg,
                shape = CircleShape,
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        if (log.success) Icons.Default.CloudDone else Icons.Default.CloudOff,
                        contentDescription = null,
                        tint = if (log.success) SuccessGreen else DangerRed,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(log.sourceAccount, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                    Text("${log.latencyMs} ms", fontSize = 10.sp, color = SchoolGreen, fontWeight = FontWeight.Bold)
                }
                Text(log.message, fontSize = 11.sp, color = TextSecondary)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(log.endpoint, fontSize = 9.sp, color = TextMuted)
                    Text(log.timestamp, fontSize = 9.sp, color = TextMuted)
                }
            }
        }
    }
}
