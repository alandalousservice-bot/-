package com.example.schoolplatform.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.schoolplatform.data.model.SupportStaff
import com.example.schoolplatform.data.model.WorkDaySchedule
import com.example.schoolplatform.data.repository.SchoolRepository
import com.example.schoolplatform.ui.components.SchoolBrandSeal
import com.example.schoolplatform.ui.theme.*

@Composable
fun AdminSupportStaffTab(
    staffList: List<SupportStaff>,
    onToggleAttendance: (Int) -> Unit,
    onAddStaffClick: () -> Unit,
    onViewCard: (SupportStaff) -> Unit
) {
    var selectedCategoryFilter by remember { mutableStateOf("الكل") }
    var searchQuery by remember { mutableStateOf("") }

    val categories = listOf("الكل", "الحراسة والأمن", "الإطعام والمطبخ", "النظافة والصيانة", "الصيانة والشبكات")

    val filteredList = staffList.filter { staff ->
        val matchesCategory = when (selectedCategoryFilter) {
            "الحراسة والأمن" -> staff.jobTitle.contains("حارس") || staff.rank.contains("حراسة")
            "الإطعام والمطبخ" -> staff.jobTitle.contains("طبخ") || staff.jobTitle.contains("طباخ") || staff.assignedArea.contains("المطعم")
            "النظافة والصيانة" -> staff.jobTitle.contains("نظافة")
            "الصيانة والشبكات" -> staff.jobTitle.contains("صيانة") || staff.jobTitle.contains("شبكات")
            else -> true
        }
        val matchesSearch = staff.fullName.contains(searchQuery.trim(), ignoreCase = true) ||
                staff.jobTitle.contains(searchQuery.trim(), ignoreCase = true) ||
                staff.assignedArea.contains(searchQuery.trim(), ignoreCase = true)

        matchesCategory && (searchQuery.isBlank() || matchesSearch)
    }

    val presentCount = staffList.count { it.isPresentToday }
    val absentCount = staffList.size - presentCount

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header & KPI Overview Banner
        item {
            Surface(
                color = CardSurface,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, BorderLight),
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("👷", fontSize = 20.sp)
                                Text("سجل العمال المهنيين وأعوان الخدمات", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SchoolGreenDark)
                            }
                            Text("المرسوم التنفيذي رقم 08-315 الخاص بالعمال المهنيين وأعوان الوقاية", fontSize = 10.sp, color = TextSecondary)
                        }

                        Button(
                            onClick = onAddStaffClick,
                            colors = ButtonDefaults.buttonColors(containerColor = SchoolGreen),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("+ عامل جديد", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Live Attendance Summary Cards
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
                                Text("${staffList.size}", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = SchoolGreen)
                                Text("إجمالي العمال", fontSize = 10.sp, color = TextSecondary)
                            }
                        }

                        Surface(
                            color = SuccessBg,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$presentCount", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = SuccessGreen)
                                Text("حاضرون اليوم", fontSize = 10.sp, color = TextSecondary)
                            }
                        }

                        Surface(
                            color = if (absentCount > 0) DangerBg else Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("$absentCount", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = if (absentCount > 0) DangerRed else TextMuted)
                                Text("غياب مسجل", fontSize = 10.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            }
        }

        // Search and Filter Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("بحث بالاسم، الوظيفة، أو مكان التدخل...", fontSize = 11.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SchoolGreen) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Category Filter Chips
        item {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategoryFilter == cat,
                        onClick = { selectedCategoryFilter = cat },
                        label = { Text(cat, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SchoolGreen,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Workers List
        if (filteredList.isEmpty()) {
            item {
                Surface(
                    color = CardSurface,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("لا يوجد عمال يطابقون معايير البحث.", fontSize = 12.sp, color = TextMuted)
                    }
                }
            }
        } else {
            items(filteredList, key = { it.id }) { staff ->
                SupportStaffCard(
                    staff = staff,
                    onToggleAttendance = { onToggleAttendance(staff.id) },
                    onViewCard = { onViewCard(staff) }
                )
            }
        }
    }
}

@Composable
fun SupportStaffCard(
    staff: SupportStaff,
    onToggleAttendance: () -> Unit,
    onViewCard: () -> Unit
) {
    var isScheduleExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val avatarEmoji = when {
        staff.jobTitle.contains("حارس") -> "🛡️"
        staff.jobTitle.contains("طباخ") || staff.jobTitle.contains("طبخ") -> "🍲"
        staff.jobTitle.contains("نظافة") -> "🧹"
        staff.jobTitle.contains("صيانة") || staff.jobTitle.contains("ترميم") -> "🔧"
        else -> "🏢"
    }

    Surface(
        color = CardSurface,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, BorderLight),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Avatar, Name, Job Title, Rank & Presence
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = SurfaceTint,
                        shape = CircleShape,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(avatarEmoji, fontSize = 22.sp)
                        }
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(staff.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                            Surface(
                                color = SchoolGreen.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    staff.rank,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SchoolGreenDark,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(staff.jobTitle, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SchoolGreen)
                    }
                }

                // Presence Switch Chip
                Surface(
                    color = if (staff.isPresentToday) SuccessBg else DangerBg,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, if (staff.isPresentToday) SuccessGreen.copy(alpha = 0.5f) else DangerRed.copy(alpha = 0.5f)),
                    modifier = Modifier.clickable { onToggleAttendance() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            if (staff.isPresentToday) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = null,
                            tint = if (staff.isPresentToday) SuccessGreen else DangerRed,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            if (staff.isPresentToday) "حاضر اليوم" else "غائب اليوم",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (staff.isPresentToday) SuccessGreen else DangerRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Info Details Grid
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PaperBackground, RoundedCornerShape(8.dp))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.Place, contentDescription = null, tint = SchoolGreen, modifier = Modifier.size(14.dp))
                    Text("مجال التدخل: ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(staff.assignedArea, fontSize = 11.sp, color = TextSecondary)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = SchoolGoldDark, modifier = Modifier.size(14.dp))
                        Text("نظام العمل: ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(staff.shiftType, fontSize = 11.sp, color = TextSecondary)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.clickable {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${staff.phone}"))
                            context.startActivity(intent)
                        }
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = SchoolGreen, modifier = Modifier.size(13.dp))
                        Text(staff.phone, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SchoolGreen)
                    }
                }

                staff.notes?.let { note ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                        Text("ملاحظات: ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(note, fontSize = 10.sp, color = TextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Weekly Schedule Accordion
            Surface(
                color = CardSurface,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, BorderLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isScheduleExpanded = !isScheduleExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = SchoolGreen, modifier = Modifier.size(16.dp))
                            Text("برنامج العمل الأسبوعي (الأحد إلى الخميس)", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = SchoolGreenDark)
                        }
                        Icon(
                            if (isScheduleExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    if (isScheduleExpanded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        WeeklyScheduleListView(staff.weeklySchedule)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onViewCard,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, SchoolGreen),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Badge, contentDescription = null, tint = SchoolGreen, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("عرض البطاقة الرسمية", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SchoolGreen)
                }

                Button(
                    onClick = onToggleAttendance,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (staff.isPresentToday) DangerRed.copy(alpha = 0.85f) else SuccessGreen
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        if (staff.isPresentToday) "تسجيل كغائب" else "تأكيد الحضور",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun WeeklyScheduleListView(schedules: List<WorkDaySchedule>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        schedules.forEach { s ->
            Surface(
                color = PaperBackground,
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, BorderLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        color = SchoolGreen,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            s.dayName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("الصباح: ${s.morningShift}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text("·", fontSize = 10.sp, color = TextMuted)
                            Text("المساء: ${s.afternoonShift}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(s.specificTasks, fontSize = 10.sp, color = TextSecondary, lineHeight = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SupportStaffCardDialog(
    staff: SupportStaff,
    onDismiss: () -> Unit
) {
    var showPrintedToast by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = { showPrintedToast = true },
                colors = ButtonDefaults.buttonColors(containerColor = SchoolGreen),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("طباعة البطاقة الرسمية", fontSize = 11.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إغلاق", fontSize = 11.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Official Algerian Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardSurface, RoundedCornerShape(10.dp))
                        .border(BorderStroke(1.dp, BorderLight), RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SchoolBrandSeal(size = 40.dp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("الجمهورية الجزائرية الديمقراطية الشعبية", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("وزارة التربية الوطنية · مديرية التربية لولاية سطيف", fontSize = 9.sp, color = TextSecondary)
                    Text("مدرسة مزيان عمار الابتدائية · 2026/2027", fontSize = 9.sp, color = SchoolGreen, fontWeight = FontWeight.Bold)
                    Divider(modifier = Modifier.padding(vertical = 6.dp), color = BorderLight)
                    Text(
                        "بطاقة الهوية المهنية وجدول العمل الأسبوعي",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        color = SchoolGreenDark,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Worker Bio Summary
                Surface(
                    color = CardSurface,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("الاسم واللقب: ${staff.fullName}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("الهاتف: ${staff.phone}", fontSize = 11.sp, color = SchoolGreen, fontWeight = FontWeight.Bold)
                        }
                        Text("الوظيفة: ${staff.jobTitle}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text("الرتبة: ${staff.rank}", fontSize = 10.sp, color = TextSecondary)
                        Text("مكان التدخل: ${staff.assignedArea}", fontSize = 10.sp, color = TextSecondary)
                        Text("نظام الدوام: ${staff.shiftType}", fontSize = 10.sp, color = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Weekly Work Schedule Table
                Text(
                    "برنامج العمل والمهام الميدانية الأسبوعية:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = SchoolGreenDark,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(6.dp))

                WeeklyScheduleListView(staff.weeklySchedule)

                Spacer(modifier = Modifier.height(12.dp))

                // Official Seal and Signature Footnote
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("توقيع العامل المعني", fontSize = 9.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(".......................", fontSize = 9.sp, color = TextMuted)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("توقيع وخاتم مدير المؤسسة", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = SchoolGreenDark)
                        Spacer(modifier = Modifier.height(20.dp))
                        Text("[ ختم المؤسسة ]", fontSize = 9.sp, color = SchoolGoldDark)
                    }
                }

                if (showPrintedToast) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = SuccessBg,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "تم إرسال بطاقة العمل الأسبوعية إلى طابعة المؤسسة بنجاح.",
                            color = SuccessGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun AddSupportStaffDialog(
    onDismiss: () -> Unit,
    onConfirm: (fullName: String, jobTitle: String, rank: String, phone: String, assignedArea: String, shiftType: String, notes: String?) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var jobTitle by remember { mutableStateOf("حارس المؤسسة") }
    var rank by remember { mutableStateOf("عامل مهني صنف 01 (حراسة)") }
    var phone by remember { mutableStateOf("0661000000") }
    var assignedArea by remember { mutableStateOf("المدخل والساحة المدرسية") }
    var shiftType by remember { mutableStateOf("دوام كامل (07:30 - 16:30)") }
    var notes by remember { mutableStateOf("") }

    val jobOptions = listOf(
        "حارس المؤسسة (أمن واستقبال)",
        "طباخ المطعم المدرسي",
        "مساعد طباخ ونظافة المطعم",
        "عامل نظافة وصيانة دورية",
        "عون صيانة وترميم وشبكات",
        "عون خدمة متعدد المهام"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تسجيل عامل مهني جديد", fontWeight = FontWeight.Bold, fontSize = 15.sp) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("الاسم واللقب") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("الوظيفة الموكلة:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    jobOptions.forEach { opt ->
                        FilterChip(
                            selected = jobTitle == opt,
                            onClick = {
                                jobTitle = opt
                                rank = when {
                                    opt.contains("طباخ") -> "عامل مهني صنف 02 (إطعام)"
                                    opt.contains("صيانة") -> "عامل مهني صنف 03 (صيانة)"
                                    else -> "عامل مهني صنف 01"
                                }
                                assignedArea = when {
                                    opt.contains("طباخ") || opt.contains("المطعم") -> "المطعم المدرسي المركزي"
                                    opt.contains("حارس") -> "المدخل الرئيسي والساحة"
                                    opt.contains("نظافة") -> "الجناح التربوي والحجرات"
                                    else -> "مرافق المؤسسة العامة"
                                }
                            },
                            label = { Text(opt.split(" ").firstOrNull() ?: opt, fontSize = 10.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = rank,
                    onValueChange = { rank = it },
                    label = { Text("الرتبة الإدارية (وفق المرسوم 08-315)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("رقم الهاتف للتواصل") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = assignedArea,
                    onValueChange = { assignedArea = it },
                    label = { Text("مكان ومجال التدخل") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = shiftType,
                    onValueChange = { shiftType = it },
                    label = { Text("نظام وساعات العمل") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات إضافية أو توصيات") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fullName.isNotBlank()) {
                        onConfirm(fullName.trim(), jobTitle.trim(), rank.trim(), phone.trim(), assignedArea.trim(), shiftType.trim(), notes.ifBlank { null })
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SchoolGreen)
            ) {
                Text("تسجيل العامل وبرنامج العمل")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
