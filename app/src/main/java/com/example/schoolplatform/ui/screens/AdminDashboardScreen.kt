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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.schoolplatform.data.model.*
import com.example.schoolplatform.data.network.LocalNetworkManager
import com.example.schoolplatform.data.repository.SchoolRepository
import com.example.schoolplatform.ui.animation.AnimatedSchoolTabRow
import com.example.schoolplatform.ui.animation.bounceClick
import com.example.schoolplatform.ui.components.PlatformSettingsContent
import com.example.schoolplatform.ui.components.SchoolTopBar
import com.example.schoolplatform.ui.components.StudentAvatar
import com.example.schoolplatform.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigateLanding: () -> Unit,
    onNavigateLogin: () -> Unit,
    onNavigateNetworkSync: () -> Unit = {},
    onNavigateTvKiosk: () -> Unit = {}
) {
    val currentUser by SchoolRepository.currentUser.collectAsState()
    val students by SchoolRepository.students.collectAsState()
    val classrooms by SchoolRepository.classrooms.collectAsState()
    val absences by SchoolRepository.absences.collectAsState()
    val mealConsumptions by SchoolRepository.mealConsumptions.collectAsState()
    val inventory by SchoolRepository.inventory.collectAsState()
    val budgetLines by SchoolRepository.budgetLines.collectAsState()
    val budgetTransactions by SchoolRepository.budgetTransactions.collectAsState()
    val grades by SchoolRepository.grades.collectAsState()
    val schedules by SchoolRepository.schedules.collectAsState()
    val facilities by SchoolRepository.facilities.collectAsState()
    val quotas by SchoolRepository.quotas.collectAsState()
    val users by SchoolRepository.users.collectAsState()
    val auditLogs by SchoolRepository.auditLogs.collectAsState()
    val supportStaff by SchoolRepository.supportStaff.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(
        "لوحة القيادة",
        "العمال المهنيين",
        "الميزانية والاعتمادات",
        "إدارة الحضور",
        "التلاميذ والأقسام",
        "اعتماد النقاط",
        "التوزيع الأسبوعي",
        "المرافق والمواقيت",
        "مخزون المطعم",
        "حسابات الطاقم",
        "شاشة التلفاز الذكية",
        "سجل التدقيق",
        "الربط الشبكي والمودام",
        "إعدادات المنصة والمظهر"
    )

    val tabIcons = listOf(
        "📊", "👷", "💰", "📋", "🎒", "📝", "🗓️", "🏫", "🍲", "👥", "📺", "📜", "📶", "⚙️"
    )

    var showAddBudgetDialog by remember { mutableStateOf(false) }
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var showAddClassDialog by remember { mutableStateOf(false) }
    var showAddStudentDialog by remember { mutableStateOf(false) }
    var showAddInventoryDialog by remember { mutableStateOf(false) }
    var showAddUserDialog by remember { mutableStateOf(false) }
    var showAddFacilityDialog by remember { mutableStateOf(false) }
    var showAddQuotaDialog by remember { mutableStateOf(false) }
    var showAddSupportStaffDialog by remember { mutableStateOf(false) }
    var selectedStaffForCard by remember { mutableStateOf<SupportStaff?>(null) }
    var showAddAnnouncementDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = PaperBackground,
        topBar = {
            SchoolTopBar(
                title = "لوحة القيادة المركزية",
                currentUser = currentUser,
                onNavigateLanding = onNavigateLanding,
                onNavigateLogin = onNavigateLogin,
                onNavigateNetworkSync = { selectedTab = 12 },
                onNavigateTvKiosk = onNavigateTvKiosk
            )
        }
    )
 { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Animated School Sub-Navigation Tab Row
            AnimatedSchoolTabRow(
                tabs = tabs,
                icons = tabIcons,
                selectedTabIndex = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    val isForward = targetState > initialState
                    (fadeIn(animationSpec = tween(220)) + slideInHorizontally(
                        animationSpec = tween(240)
                    ) { if (isForward) it / 3 else -it / 3 }) togetherWith
                    (fadeOut(animationSpec = tween(180)) + slideOutHorizontally(
                        animationSpec = tween(200)
                    ) { if (isForward) -it / 3 else it / 3 })
                },
                label = "adminTabContentTransition",
                modifier = Modifier.fillMaxSize()
            ) { targetTab ->
                when (targetTab) {
                    0 -> AdminOverviewTab(
                        students = students,
                        absences = absences,
                        mealCount = mealConsumptions.size,
                        inventory = inventory,
                        budgetLines = budgetLines,
                        supportStaff = supportStaff,
                        auditLogs = auditLogs,
                        onNavigateTab = { selectedTab = it }
                    )
                    1 -> AdminSupportStaffTab(
                        staffList = supportStaff,
                        onToggleAttendance = { SchoolRepository.toggleStaffAttendance(it) },
                        onAddStaffClick = { showAddSupportStaffDialog = true },
                        onViewCard = { selectedStaffForCard = it }
                    )
                    2 -> AdminBudgetTab(
                        lines = budgetLines,
                        transactions = budgetTransactions,
                        onAddLineClick = { showAddBudgetDialog = true },
                        onAddExpenseClick = { showAddExpenseDialog = true }
                    )
                    3 -> AdminAttendanceTab(
                        absences = absences,
                        students = students,
                        classrooms = classrooms
                    )
                    4 -> AdminStudentsTab(
                        students = students,
                        classrooms = classrooms,
                        onAddClassClick = { showAddClassDialog = true },
                        onAddStudentClick = { showAddStudentDialog = true }
                    )
                    5 -> AdminGradesTab(
                        grades = grades,
                        onApproveAll = {
                            SchoolRepository.approveGrades("الفصل الثالث", "التربية البدنية والرياضية")
                        }
                    )
                    6 -> AdminSchedulesTab(
                        schedules = schedules,
                        onGenerate = { SchoolRepository.autoGenerateSchedules() },
                        onApproveAll = { SchoolRepository.approveAllSchedules() }
                    )
                    7 -> AdminFacilitiesTab(
                        facilities = facilities,
                        quotas = quotas,
                        onAddFacilityClick = { showAddFacilityDialog = true },
                        onAddQuotaClick = { showAddQuotaDialog = true }
                    )
                    8 -> AdminInventoryTab(
                        inventory = inventory,
                        onAddClick = { showAddInventoryDialog = true },
                        onAdjustStock = { id, delta -> SchoolRepository.updateInventoryStock(id, delta) }
                    )
                    9 -> AdminUsersTab(
                        users = users,
                        onAddUserClick = { showAddUserDialog = true },
                        onToggleActive = { SchoolRepository.toggleUserActive(it) }
                    )
                    10 -> AdminTvControlTab(
                        onLaunchTvScreen = onNavigateTvKiosk
                    )
                    11 -> AdminAuditTab(
                        logs = auditLogs
                    )
                    12 -> LocalNetworkSyncScreen(
                        onNavigateBack = { selectedTab = 0 }
                    )
                    13 -> AdminSettingsTab()
                }
            }
        }
    }

    // Dialogs
    if (showAddBudgetDialog) {
        AddBudgetLineDialog(onDismiss = { showAddBudgetDialog = false })
    }
    if (showAddExpenseDialog) {
        AddExpenseDialog(lines = budgetLines, onDismiss = { showAddExpenseDialog = false })
    }
    if (showAddClassDialog) {
        AddClassDialog(onDismiss = { showAddClassDialog = false })
    }
    if (showAddStudentDialog) {
        AddStudentDialog(classrooms = classrooms, onDismiss = { showAddStudentDialog = false })
    }
    if (showAddInventoryDialog) {
        AddInventoryDialog(onDismiss = { showAddInventoryDialog = false })
    }
    if (showAddUserDialog) {
        AddUserDialog(onDismiss = { showAddUserDialog = false })
    }
    if (showAddFacilityDialog) {
        AddFacilityDialog(onDismiss = { showAddFacilityDialog = false })
    }
    if (showAddQuotaDialog) {
        AddQuotaDialog(onDismiss = { showAddQuotaDialog = false })
    }
    if (showAddSupportStaffDialog) {
        AddSupportStaffDialog(
            onDismiss = { showAddSupportStaffDialog = false },
            onConfirm = { fullName, jobTitle, rank, phone, assignedArea, shiftType, notes ->
                SchoolRepository.addSupportStaff(fullName, jobTitle, rank, phone, assignedArea, shiftType, notes)
            }
        )
    }

    selectedStaffForCard?.let { staff ->
        SupportStaffCardDialog(
            staff = staff,
            onDismiss = { selectedStaffForCard = null }
        )
    }
}

// 1. Overview Tab
@Composable
private fun AdminOverviewTab(
    students: List<Student>,
    absences: List<Absence>,
    mealCount: Int,
    inventory: List<InventoryItem>,
    budgetLines: List<BudgetLine>,
    supportStaff: List<SupportStaff>,
    auditLogs: List<AuditLog>,
    onNavigateTab: (Int) -> Unit
) {
    val totalAllocated = budgetLines.sumOf { it.allocatedAmount }
    val totalSpent = budgetLines.sumOf { it.spentAmount }
    val utilization = if (totalAllocated > 0) ((totalSpent / totalAllocated) * 100).toInt() else 0
    val lowStockCount = inventory.count { it.isLow }
    val staffPresent = supportStaff.count { it.isPresentToday }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Welcome Header
            Surface(
                color = CardSurface,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, BorderLight)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("ملخص اليوم الدراسي", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SchoolGreenDark)
                    Text("ولاية سطيف · مدرسة مزيان عمار الابتدائية · 2026/2027", fontSize = 11.sp, color = TextSecondary)
                }
            }
        }

        // Metrics Grid (Row 1)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "إجمالي التلاميذ",
                    value = "${students.size}",
                    icon = Icons.Default.Groups,
                    color = SchoolGreen,
                    bgColor = SurfaceTint,
                    onClick = { onNavigateTab(4) }
                )
                AdminMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "غيابات اليوم",
                    value = "${absences.size}",
                    icon = Icons.Default.PersonOff,
                    color = DangerRed,
                    bgColor = DangerBg,
                    onClick = { onNavigateTab(3) }
                )
            }
        }

        // Metrics Grid (Row 2)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "وجبات المطعم",
                    value = "$mealCount",
                    icon = Icons.Default.Restaurant,
                    color = WarningOrange,
                    bgColor = WarningBg,
                    onClick = { onNavigateTab(8) }
                )
                AdminMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "تنبيه المخزون",
                    value = "$lowStockCount مواد",
                    icon = Icons.Default.Inventory2,
                    color = if (lowStockCount > 0) DangerRed else SuccessGreen,
                    bgColor = if (lowStockCount > 0) DangerBg else SuccessBg,
                    onClick = { onNavigateTab(8) }
                )
            }
        }

        // Metrics Grid (Row 3: Support Staff & Budget)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "العمال المهنيين",
                    value = "$staffPresent/${supportStaff.size} حاضرون",
                    icon = Icons.Default.Engineering,
                    color = Color(0xFFB45309),
                    bgColor = SchoolGoldLight,
                    onClick = { onNavigateTab(1) }
                )
                AdminMetricCard(
                    modifier = Modifier.weight(1f),
                    title = "اعتمادات الميزانية",
                    value = "${budgetLines.size} بنود",
                    icon = Icons.Default.AccountBalanceWallet,
                    color = Color(0xFF1E3A8A),
                    bgColor = Color(0xFFDBEAFE),
                    onClick = { onNavigateTab(2) }
                )
            }
        }

        // Network & Hotspot Connectivity Hub Card
        item {
            val netType by LocalNetworkManager.currentNetworkType.collectAsState()
            val myIp by LocalNetworkManager.localIp.collectAsState()
            val lastSync by LocalNetworkManager.lastSyncTime.collectAsState()

            Surface(
                color = CardSurface,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, SchoolGreen.copy(alpha = 0.35f)),
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .bounceClick(scaleDown = 0.97f) { onNavigateTab(11) }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = SchoolGreenLight,
                                shape = CircleShape,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(netType.iconEmoji, fontSize = 18.sp)
                                }
                            }
                            Column {
                                Text(
                                    "اتصال ومزامنة الحسابات (مودام / Hotspot)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = SchoolGreenDark
                                )
                                Text(
                                    "IP المحلي: $myIp · ${netType.titleAr}",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                        Surface(
                            color = SchoolGreenLight,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "فحص وإعدادات ←",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SchoolGreenDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    if (lastSync != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "آخر مزامنة مع هواتف الأساتذة والمطعم: $lastSync",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }

        item {
            // Budget Summary card
            Surface(
                color = CardSurface,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, BorderLight),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateTab(2) }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("مؤشر استهلاك الميزانية", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("$utilization%", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = SchoolGreen)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { (utilization / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = SchoolGreen,
                        trackColor = SurfaceTint
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("الاعتماد: %,d دج".format(totalAllocated.toLong()), fontSize = 11.sp, color = TextSecondary)
                        Text("المنفق: %,d دج".format(totalSpent.toLong()), fontSize = 11.sp, color = DangerRed, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Recent Audit Activity
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("آخر العمليات في النظام", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SchoolGreenDark)
                TextButton(onClick = { onNavigateTab(10) }) {
                    Text("عرض الكل", fontSize = 11.sp, color = SchoolGreen)
                }
            }
        }

        items(auditLogs.take(4), key = { it.id }) { log ->
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
                    Column {
                        Text(log.details, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("${log.userName} · ${log.actionAr}", fontSize = 10.sp, color = TextSecondary)
                    }
                    Text(log.createdAt.split(" ").getOrNull(1) ?: "", fontSize = 10.sp, color = TextMuted)
                }
            }
        }
    }
}

@Composable
private fun AdminMetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    bgColor: Color,
    onClick: () -> Unit
) {
    Surface(
        color = CardSurface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderLight),
        shadowElevation = 1.dp,
        modifier = modifier.bounceClick(scaleDown = 0.95f) { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                color = bgColor,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                }
            }
            Column {
                Text(value, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = TextPrimary)
                Text(title, fontSize = 10.sp, color = TextSecondary)
            }
        }
    }
}

// 2. Budget Tab
@Composable
private fun AdminBudgetTab(
    lines: List<BudgetLine>,
    transactions: List<BudgetTransaction>,
    onAddLineClick: () -> Unit,
    onAddExpenseClick: () -> Unit
) {
    val totalAllocated = lines.sumOf { it.allocatedAmount }
    val totalSpent = lines.sumOf { it.spentAmount }
    val totalRemaining = totalAllocated - totalSpent

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("السنة المالية: 2026/2027", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SchoolGreenDark)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = onAddLineClick,
                        colors = ButtonDefaults.buttonColors(containerColor = SchoolGreen),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("+ إضافة بند", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onAddExpenseClick,
                        colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("تسجيل نفقة", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Summary Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                BudgetStatCard(Modifier.weight(1f), "إجمالي الاعتماد", "%,d دج".format(totalAllocated.toLong()), SchoolGreen, SurfaceTint)
                BudgetStatCard(Modifier.weight(1f), "إجمالي المنفق", "%,d دج".format(totalSpent.toLong()), DangerRed, DangerBg)
                BudgetStatCard(Modifier.weight(1f), "المتبقي", "%,d دج".format(totalRemaining.toLong()), Color(0xFF1E3A8A), Color(0xFFDBEAFE))
            }
        }

        item {
            Text("بنود الميزانية والاعتمادات المفتوحة", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SchoolGreenDark)
        }

        items(lines, key = { it.id }) { line ->
            Surface(
                color = CardSurface,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(line.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(line.category.titleAr, fontSize = 10.sp, color = TextSecondary)
                        }
                        Surface(
                            color = if (line.utilizationPercent >= 80) DangerBg else SuccessBg,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                "${line.utilizationPercent}% مستهلك",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (line.utilizationPercent >= 80) DangerRed else SuccessGreen,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { (line.utilizationPercent / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (line.utilizationPercent >= 80) DangerRed else SchoolGreen,
                        trackColor = SurfaceTint
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("الاعتماد: %,d دج".format(line.allocatedAmount.toLong()), fontSize = 10.sp, color = TextSecondary)
                        Text("المصروف: %,d دج".format(line.spentAmount.toLong()), fontSize = 10.sp, color = DangerRed)
                        Text("المتبقي: %,d دج".format(line.remainingAmount.toLong()), fontSize = 10.sp, color = SchoolGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text("سجل النفقات والعمليات الأخيرة", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SchoolGreenDark)
        }

        items(transactions, key = { it.id }) { t ->
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
                    Column {
                        Text(t.description, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("${t.lineTitle} · ${t.createdAt}", fontSize = 10.sp, color = TextSecondary)
                    }
                    Text("- %,d دج".format(t.amount.toLong()), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = DangerRed)
                }
            }
        }
    }
}

@Composable
private fun BudgetStatCard(modifier: Modifier, title: String, value: String, color: Color, bg: Color) {
    Surface(color = bg, shape = RoundedCornerShape(10.dp), modifier = modifier) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Text(title, fontSize = 9.sp, color = color.copy(alpha = 0.85f))
        }
    }
}

// 3. Attendance Management Tab
@Composable
private fun AdminAttendanceTab(
    absences: List<Absence>,
    students: List<Student>,
    classrooms: List<Classroom>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("سجل الغيابات اليومية للمؤسسة", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SchoolGreenDark)
        }

        if (absences.isEmpty()) {
            item {
                Surface(
                    color = SuccessBg,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("حضور ممتاز اليوم! لا توجد غيابات مسجلة.", modifier = Modifier.padding(14.dp), color = SuccessGreen, fontSize = 12.sp)
                }
            }
        } else {
            items(absences, key = { it.id }) { a ->
                Surface(
                    color = CardSurface,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Surface(color = DangerBg, shape = CircleShape, modifier = Modifier.size(32.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("✕", color = DangerRed, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                            Column {
                                Text(a.studentName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("${a.classroomName} · ${a.period.titleAr}", fontSize = 10.sp, color = TextSecondary)
                            }
                        }
                        Text(a.date, fontSize = 11.sp, color = TextMuted)
                    }
                }
            }
        }
    }
}

// 4. Students & Classes Tab
@Composable
private fun AdminStudentsTab(
    students: List<Student>,
    classrooms: List<Classroom>,
    onAddClassClick: () -> Unit,
    onAddStudentClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedClassroomId by remember { mutableStateOf<Int?>(null) }

    val filteredStudents = remember(students, searchQuery, selectedClassroomId) {
        val query = searchQuery.trim()
        students.filter { s ->
            val matchesClass = selectedClassroomId == null || s.classroomId == selectedClassroomId
            val matchesQuery = query.isEmpty() ||
                s.fullName.contains(query, ignoreCase = true) ||
                s.barcode.contains(query, ignoreCase = true) ||
                s.id.toString() == query ||
                s.id.toString().contains(query) ||
                (s.parentPhone?.contains(query) == true)
            matchesClass && matchesQuery
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("هيكل الأقسام والتلاميذ", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SchoolGreenDark)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = onAddClassClick,
                        colors = ButtonDefaults.buttonColors(containerColor = SchoolGreen),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("+ إضافة قسم", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = onAddStudentClick,
                        colors = ButtonDefaults.buttonColors(containerColor = SchoolGold),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("+ تسجيل تلميذ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SchoolGreenDark)
                    }
                }
            }
        }

        // Search Bar for filtering students by Name or ID / Barcode
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, BorderLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("student_search_bar"),
                        placeholder = {
                            Text(
                                "ابحث عن تلميذ بالاسم أو رقم التعريف أو الباركود...",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "بحث",
                                tint = SchoolGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { searchQuery = "" },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "مسح البحث",
                                        tint = TextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SchoolGreen,
                            unfocusedBorderColor = BorderLight,
                            focusedContainerColor = SurfaceTint.copy(alpha = 0.5f),
                            unfocusedContainerColor = SurfaceTint.copy(alpha = 0.25f)
                        )
                    )

                    // Active Search & Filter Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                color = if (searchQuery.isNotBlank() || selectedClassroomId != null) SchoolGoldLight else SurfaceTint,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (searchQuery.isNotBlank() || selectedClassroomId != null)
                                        "النتائج: ${filteredStudents.size} من أصل ${students.size}"
                                    else
                                        "إجمالي التلاميذ: ${students.size}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (searchQuery.isNotBlank() || selectedClassroomId != null) SchoolGoldDark else SchoolGreenDark,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            if (selectedClassroomId != null) {
                                val selectedName = classrooms.find { it.id == selectedClassroomId }?.name ?: ""
                                Surface(
                                    color = SchoolGreen.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "القسم: $selectedName",
                                        fontSize = 11.sp,
                                        color = SchoolGreenDark,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        if (searchQuery.isNotBlank() || selectedClassroomId != null) {
                            TextButton(
                                onClick = {
                                    searchQuery = ""
                                    selectedClassroomId = null
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp), tint = DangerRed)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("إلغاء التصفية", fontSize = 11.sp, color = DangerRed, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Classrooms chips/cards (Interactive filter)
        item {
            Text("تصفية حسب القسم:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // All Classrooms Chip
                val isAllSelected = selectedClassroomId == null
                Surface(
                    color = if (isAllSelected) SchoolGreen else CardSurface,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, if (isAllSelected) SchoolGreen else BorderLight),
                    modifier = Modifier
                        .clickable { selectedClassroomId = null }
                        .bounceClick()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("👥", fontSize = 14.sp)
                        Text(
                            "جميع الأقسام",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isAllSelected) Color.White else TextPrimary
                        )
                        Surface(
                            color = if (isAllSelected) Color.White.copy(alpha = 0.25f) else SurfaceTint,
                            shape = CircleShape
                        ) {
                            Text(
                                "${students.size}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isAllSelected) Color.White else SchoolGreen,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                classrooms.forEach { cls ->
                    val isSelected = selectedClassroomId == cls.id
                    val count = students.count { it.classroomId == cls.id }
                    Surface(
                        color = if (isSelected) SchoolGreen else CardSurface,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, if (isSelected) SchoolGreen else BorderLight),
                        modifier = Modifier
                            .clickable {
                                selectedClassroomId = if (isSelected) null else cls.id
                            }
                            .bounceClick()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("🏫", fontSize = 14.sp)
                            Text(
                                cls.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (isSelected) Color.White else TextPrimary
                            )
                            Surface(
                                color = if (isSelected) Color.White.copy(alpha = 0.25f) else SurfaceTint,
                                shape = CircleShape
                            ) {
                                Text(
                                    "$count",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else SchoolGreen,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = if (searchQuery.isNotBlank() || selectedClassroomId != null)
                    "نتائج البحث والتصفية (${filteredStudents.size} تلميذ):"
                else
                    "قائمة التلاميذ المسجلين (${students.size} تلميذ):",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = SchoolGreenDark
            )
        }

        if (filteredStudents.isEmpty()) {
            item {
                Surface(
                    color = CardSurface,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = SurfaceTint,
                            shape = CircleShape,
                            modifier = Modifier.size(54.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🔍", fontSize = 26.sp)
                            }
                        }
                        Text(
                            text = "لم يتم العثور على أي تلميذ",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (searchQuery.isNotBlank())
                                "لا توجد نتائج تطابق \"$searchQuery\" بالاسم أو رقم التعريف أو الباركود"
                            else
                                "لا يوجد تلاميذ مسجلين في هذا القسم المختار",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = {
                                searchQuery = ""
                                selectedClassroomId = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SchoolGreen),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("إعادة ضبط البحث", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            items(filteredStudents, key = { it.id }) { s ->
                val className = classrooms.find { it.id == s.classroomId }?.name ?: ""
                Surface(
                    color = CardSurface,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StudentAvatar(
                                fullName = s.fullName,
                                studentId = s.id,
                                size = 42.dp
                            )
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(s.fullName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                    Surface(
                                        color = SchoolGoldLight,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            "رقم #${s.id}",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SchoolGoldDark,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text("$className · هاتف الولي: ${s.parentPhone ?: "غير مسجل"}", fontSize = 10.sp, color = TextSecondary)
                            }
                        }
                        Surface(color = SurfaceTint, shape = RoundedCornerShape(6.dp)) {
                            Text(s.barcode, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SchoolGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                        }
                    }
                }
            }
        }
    }
}


// 5. Grades Approval Tab
@Composable
private fun AdminGradesTab(
    grades: List<GradeEntry>,
    onApproveAll: () -> Unit
) {
    val pendingCount = grades.count { it.status == GradeStatus.SUBMITTED }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("اعتماد دفاتر النقاط", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SchoolGreenDark)
                    Text("$pendingCount دفاتر بانتظار الاعتماد", fontSize = 11.sp, color = TextSecondary)
                }
                Button(
                    onClick = onApproveAll,
                    colors = ButtonDefaults.buttonColors(containerColor = SchoolGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("اعتماد الجميع", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(grades, key = { it.id }) { g ->
            Surface(
                color = CardSurface,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StudentAvatar(
                            fullName = g.studentName,
                            studentId = g.studentId,
                            size = 36.dp
                        )
                        Column {
                            Text(g.studentName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("${g.classroomName} · ${g.subject} (${g.term})", fontSize = 10.sp, color = TextSecondary)
                            Text("تقويم: ${g.continuous ?: "-"} · اختبار: ${g.exam ?: "-"}", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    Surface(
                        color = when (g.status) {
                            GradeStatus.APPROVED -> SuccessBg
                            GradeStatus.SUBMITTED -> WarningBg
                            else -> SurfaceTint
                        },
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            g.status.titleAr,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (g.status) {
                                GradeStatus.APPROVED -> SuccessGreen
                                GradeStatus.SUBMITTED -> WarningOrange
                                else -> TextSecondary
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

// 6. Timetable Schedules Tab
@Composable
private fun AdminSchedulesTab(
    schedules: List<Schedule>,
    onGenerate: () -> Unit,
    onApproveAll: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("التوزيع الأسبوعي للحصص", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SchoolGreenDark)
                    Text("${schedules.size} حصة موزعة", fontSize = 11.sp, color = TextSecondary)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onGenerate,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("توليد آلي", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SchoolGreen)
                    }
                    Button(
                        onClick = onApproveAll,
                        colors = ButtonDefaults.buttonColors(containerColor = SchoolGreen),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("اعتماد رسمي", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        items(schedules, key = { it.id }) { s ->
            val dayName = when (s.dayOfWeek) {
                0 -> "الأحد"
                1 -> "الإثنين"
                2 -> "الثلاثاء"
                3 -> "الأربعاء"
                else -> "الخميس"
            }
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
                    Column {
                        Text("${s.subject} (${s.classroomName})", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("${s.teacherName} · $dayName · ${s.period.titleAr}", fontSize = 10.sp, color = TextSecondary)
                    }
                    Surface(color = SuccessBg, shape = RoundedCornerShape(6.dp)) {
                        Text("معتمد", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SuccessGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }
        }
    }
}

// 7. Facilities & Quotas Tab
@Composable
private fun AdminFacilitiesTab(
    facilities: List<Facility>,
    quotas: List<SubjectWeeklyQuota>,
    onAddFacilityClick: () -> Unit,
    onAddQuotaClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("المرافق المدرسية والحجرات", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SchoolGreenDark)
                Button(
                    onClick = onAddFacilityClick,
                    colors = ButtonDefaults.buttonColors(containerColor = SchoolGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("+ إضافة مرفق", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(facilities, key = { it.id }) { f ->
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
                    Column {
                        Text(f.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("النوع: ${f.kind} · الطاقة الاستيعابية: ${f.capacity ?: "-"} تلميذ", fontSize = 10.sp, color = TextSecondary)
                    }
                    Surface(color = SuccessBg, shape = RoundedCornerShape(6.dp)) {
                        Text("متاح", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SuccessGreen, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("الحجوم الساعية الأسبوعية للمواد", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SchoolGreenDark)
                Button(
                    onClick = onAddQuotaClick,
                    colors = ButtonDefaults.buttonColors(containerColor = SchoolGold),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("+ إضافة حصة", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SchoolGreenDark)
                }
            }
        }

        items(quotas, key = { it.id }) { q ->
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
                    Column {
                        Text(q.subject, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("${q.level} · مدة الحصة: ${q.minutesPerLesson} دقيقة", fontSize = 10.sp, color = TextSecondary)
                    }
                    Text("${q.lessonsPerWeek} حصة/أسبوع", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SchoolGreen)
                }
            }
        }
    }
}

// 8. Inventory Tab
@Composable
private fun AdminInventoryTab(
    inventory: List<InventoryItem>,
    onAddClick: () -> Unit,
    onAdjustStock: (Int, Double) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("مخزون المواد الغذائية والتموين", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SchoolGreenDark)
                Button(
                    onClick = onAddClick,
                    colors = ButtonDefaults.buttonColors(containerColor = SchoolGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("+ إضافة مادة", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(inventory, key = { it.id }) { item ->
            Surface(
                color = CardSurface,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, if (item.isLow) DangerRed.copy(alpha = 0.5f) else BorderLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            if (item.isLow) {
                                Surface(color = DangerBg, shape = RoundedCornerShape(4.dp)) {
                                    Text("منخفض!", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = DangerRed, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                }
                            }
                        }
                        Text("الحد الأدنى: ${item.lowStockThreshold} ${item.unit}", fontSize = 10.sp, color = TextSecondary)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = { onAdjustStock(item.id, -1.0) },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Text("-", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DangerRed)
                        }
                        Text("${item.quantity} ${item.unit}", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                        IconButton(
                            onClick = { onAdjustStock(item.id, 1.0) },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Text("+", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SchoolGreen)
                        }
                    }
                }
            }
        }
    }
}

// 9. Staff Accounts Tab
@Composable
private fun AdminUsersTab(
    users: List<User>,
    onAddUserClick: () -> Unit,
    onToggleActive: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("حسابات الطاقم الإداري والتربوي", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SchoolGreenDark)
                Button(
                    onClick = onAddUserClick,
                    colors = ButtonDefaults.buttonColors(containerColor = SchoolGreen),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("+ حساب جديد", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(users, key = { it.id }) { u ->
            Surface(
                color = CardSurface,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(u.fullName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("${u.role.titleAr} (${u.username})", fontSize = 10.sp, color = TextSecondary)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            color = if (u.isActive) SuccessBg else DangerBg,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                if (u.isActive) "نشط" else "معطل",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (u.isActive) SuccessGreen else DangerRed,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Switch(
                            checked = u.isActive,
                            onCheckedChange = { onToggleActive(u.id) }
                        )
                    }
                }
            }
        }
    }
}

// 10. Audit Log Tab
@Composable
private fun AdminAuditTab(logs: List<AuditLog>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text("سجل التدقيق والعمليات الإدارية الموثقة", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SchoolGreenDark)
        }

        items(logs, key = { it.id }) { log ->
            Surface(
                color = CardSurface,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, BorderLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(log.details, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(log.createdAt, fontSize = 10.sp, color = TextMuted)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("المسؤول: ${log.userName} · نوع العملية: ${log.actionAr}", fontSize = 10.sp, color = TextSecondary)
                }
            }
        }
    }
}

// Dialog Implementations
@Composable
private fun AddBudgetLineDialog(onDismiss: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(BudgetCategory.OPERATING) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة اعتماد مالي جديد", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان البند") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("المبلغ المخصص (دج)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountStr.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && amt > 0) {
                        SchoolRepository.addBudgetLine(category, title, amt, null)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SchoolGreen)
            ) {
                Text("إضافة")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}

@Composable
private fun AddExpenseDialog(lines: List<BudgetLine>, onDismiss: () -> Unit) {
    var selectedLineId by remember { mutableStateOf(lines.firstOrNull()?.id ?: 1) }
    var amountStr by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تسجيل نفقة جديدة", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("بيان النفقة / الفاتورة") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("المبلغ المنفق (دج)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountStr.toDoubleOrNull() ?: 0.0
                    if (description.isNotBlank() && amt > 0) {
                        SchoolRepository.addBudgetExpense(selectedLineId, amt, description)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
            ) {
                Text("صرف")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}

@Composable
private fun AddClassDialog(onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة قسم دراسي جديد", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("اسم القسم (مثال: السنة الأولى أ)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        SchoolRepository.addClassroom(name.trim())
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SchoolGreen)
            ) {
                Text("إضافة")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}

@Composable
private fun AddStudentDialog(classrooms: List<Classroom>, onDismiss: () -> Unit) {
    var fullName by remember { mutableStateOf("") }
    var barcode by remember { mutableStateOf("100000" + (classrooms.size + 6)) }
    var selectedClassId by remember { mutableStateOf(classrooms.firstOrNull()?.id ?: 1) }
    var phone by remember { mutableStateOf("0550123456") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تسجيل تلميذ جديد", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("الاسم واللقب") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = barcode,
                    onValueChange = { barcode = it },
                    label = { Text("رقم الباركود") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("هاتف الولي") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fullName.isNotBlank()) {
                        SchoolRepository.addStudent(fullName.trim(), barcode.trim(), selectedClassId, phone.trim())
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SchoolGreen)
            ) {
                Text("تسجيل")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}

@Composable
private fun AddInventoryDialog(onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var quantityStr by remember { mutableStateOf("10") }
    var unit by remember { mutableStateOf("كغ") }
    var thresholdStr by remember { mutableStateOf("5") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة مادة للمخزون", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم المادة (مثال: سكر)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = quantityStr,
                    onValueChange = { quantityStr = it },
                    label = { Text("الكمية الأولية") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text("الوحدة (كغ، لتر، علبة...)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qty = quantityStr.toDoubleOrNull() ?: 0.0
                    val th = thresholdStr.toDoubleOrNull() ?: 5.0
                    if (name.isNotBlank()) {
                        SchoolRepository.addInventoryItem(name.trim(), qty, unit.trim(), th)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SchoolGreen)
            ) {
                Text("إضافة")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}

@Composable
private fun AddUserDialog(onDismiss: () -> Unit) {
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(Role.TEACHER) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إنشاء حساب مستخدم جديد", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("الاسم الكامل") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("اسم المستخدم") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("كلمة المرور") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fullName.isNotBlank() && username.isNotBlank() && password.isNotBlank()) {
                        SchoolRepository.createUser(username.trim(), password.trim(), fullName.trim(), role)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SchoolGreen)
            ) {
                Text("إنشاء الحساب")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}

@Composable
private fun AddFacilityDialog(onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var capacityStr by remember { mutableStateOf("30") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة مرفق مدرسي", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم المرفق (مثال: قاعة الموسيقى)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = capacityStr,
                    onValueChange = { capacityStr = it },
                    label = { Text("الطاقة الاستيعابية") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        SchoolRepository.addFacility(name.trim(), "CLASSROOM", capacityStr.toIntOrNull())
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SchoolGreen)
            ) {
                Text("إضافة")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}

@Composable
private fun AddQuotaDialog(onDismiss: () -> Unit) {
    var subject by remember { mutableStateOf("") }
    var lessonsStr by remember { mutableStateOf("4") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تحديد الحجم الساعي الأسبوعي", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("المادة التعليمية") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = lessonsStr,
                    onValueChange = { lessonsStr = it },
                    label = { Text("عدد الحصص أسبوعياً") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (subject.isNotBlank()) {
                        SchoolRepository.addSubjectQuota("الطور الابتدائي", subject.trim(), lessonsStr.toDoubleOrNull() ?: 4.0, 45)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SchoolGreen)
            ) {
                Text("تحديد")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}

@Composable
private fun AdminSettingsTab() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            PlatformSettingsContent()
        }
    }
}
