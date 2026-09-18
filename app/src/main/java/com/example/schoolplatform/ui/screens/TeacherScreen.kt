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
import com.example.schoolplatform.ui.animation.AnimatedSchoolTabRow
import com.example.schoolplatform.ui.animation.bounceClick
import com.example.schoolplatform.ui.components.SchoolTopBar
import com.example.schoolplatform.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherScreen(
    onNavigateLanding: () -> Unit,
    onNavigateLogin: () -> Unit,
    onNavigateNetworkSync: () -> Unit = {}
) {
    val currentUser by SchoolRepository.currentUser.collectAsState()
    val classrooms by SchoolRepository.classrooms.collectAsState()
    val students by SchoolRepository.students.collectAsState()
    val absences by SchoolRepository.absences.collectAsState()
    val grades by SchoolRepository.grades.collectAsState()
    val schedules by SchoolRepository.schedules.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("تسجيل الغياب", "دفتر النقاط", "الجدول الأسبوعي", "سجل الغيابات")

    // Attendance state
    var selectedClassId by remember { mutableStateOf(1) }
    var selectedPeriod by remember { mutableStateOf(AbsencePeriod.MORNING) }
    var searchQuery by remember { mutableStateOf("") }
    val absentStudentIds = remember { mutableStateMapOf<Int, Boolean>() }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    // Synchronize initial absent students
    LaunchedEffect(selectedClassId, selectedPeriod, absences) {
        val currentClass = classrooms.find { it.id == selectedClassId }
        val currentAbsents = absences.filter {
            it.classroomName == currentClass?.name && it.period == selectedPeriod
        }.map { it.studentId }
        absentStudentIds.clear()
        currentAbsents.forEach { absentStudentIds[it] = true }
    }

    val classStudents = students.filter { it.classroomId == selectedClassId }
    val filteredStudents = classStudents.filter {
        it.fullName.contains(searchQuery, ignoreCase = true) || it.barcode.contains(searchQuery)
    }

    Scaffold(
        containerColor = PaperBackground,
        topBar = {
            SchoolTopBar(
                title = "فضاء الأستاذ",
                currentUser = currentUser,
                onNavigateLanding = onNavigateLanding,
                onNavigateLogin = onNavigateLogin,
                onNavigateNetworkSync = onNavigateNetworkSync
            )
        },
        snackbarHost = {
            if (snackbarMessage != null) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { snackbarMessage = null }) {
                            Text("حسناً", color = Color.White)
                        }
                    },
                    containerColor = SchoolGreenDark
                ) {
                    Text(snackbarMessage ?: "", color = Color.White)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Teacher Profile summary card
            Surface(
                color = SurfaceTint,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = SchoolGreen,
                            shape = CircleShape,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("👨‍🏫", fontSize = 18.sp)
                            }
                        }
                        Column {
                            Text(
                                currentUser?.fullName ?: "الأستاذ أحمد",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = SchoolGreenDark
                            )
                            Text(
                                "أستاذ التعليم الابتدائي · خدمة كاملة (24 س أسبوعياً)",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    // Online/Offline status badge with pulsing dot
                    Surface(
                        color = SuccessBg,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            com.example.schoolplatform.ui.animation.PulsingStatusDot(color = SuccessGreen, size = 7.dp)
                            Text("محلي ومتزامن", fontSize = 10.sp, color = SuccessGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Animated Teacher Tabs
            AnimatedSchoolTabRow(
                tabs = tabTitles,
                icons = listOf("📋", "📊", "🗓️", "📜"),
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
                label = "teacherTabContentTransition",
                modifier = Modifier.fillMaxSize()
            ) { targetTab ->
                when (targetTab) {
                    0 -> AttendanceTab(
                        classrooms = classrooms,
                        selectedClassId = selectedClassId,
                        onSelectClass = { selectedClassId = it },
                        selectedPeriod = selectedPeriod,
                        onSelectPeriod = { selectedPeriod = it },
                        students = filteredStudents,
                        totalCount = classStudents.size,
                        absentMap = absentStudentIds,
                        onToggleAbsent = { id, isAbsent -> absentStudentIds[id] = isAbsent },
                        onMarkAll = { isAbsent ->
                            classStudents.forEach { absentStudentIds[it.id] = isAbsent }
                        },
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it },
                        onSubmit = {
                            val absentSet = absentStudentIds.filter { it.value }.keys
                            SchoolRepository.submitAbsences(selectedClassId, selectedPeriod, absentSet)
                            snackbarMessage = "تم حفظ وإرسال تقرير الغياب بنجاح"
                        }
                    )
                    1 -> GradebookTab(
                        students = classStudents,
                        grades = grades,
                        onSubmitForApproval = { term, subject ->
                            SchoolRepository.submitGradesForApproval(term, subject)
                            snackbarMessage = "تم إرسال دفتر النقاط للإدارة للاعتماد"
                        }
                    )
                    2 -> TimetableTab(
                        schedules = schedules.filter { it.teacherId == (currentUser?.id ?: 2) }
                    )
                    3 -> AbsenceHistoryTab(
                        absences = absences
                    )
                }
            }
        }
    }
}

@Composable
private fun AttendanceTab(
    classrooms: List<Classroom>,
    selectedClassId: Int,
    onSelectClass: (Int) -> Unit,
    selectedPeriod: AbsencePeriod,
    onSelectPeriod: (AbsencePeriod) -> Unit,
    students: List<Student>,
    totalCount: Int,
    absentMap: Map<Int, Boolean>,
    onToggleAbsent: (Int, Boolean) -> Unit,
    onMarkAll: (Boolean) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    val absentCount = absentMap.count { it.value }
    val presentCount = (totalCount - absentCount).coerceAtLeast(0)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Class and Period selector
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, BorderLight)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("اختر القسم والفترة:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    // Classroom chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        classrooms.forEach { cls ->
                            FilterChip(
                                selected = cls.id == selectedClassId,
                                onClick = { onSelectClass(cls.id) },
                                label = { Text(cls.name, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SchoolGreen,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Period chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedPeriod == AbsencePeriod.MORNING,
                            onClick = { onSelectPeriod(AbsencePeriod.MORNING) },
                            label = { Text("صباحية (08:00 - 12:00)", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SchoolGreen,
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = selectedPeriod == AbsencePeriod.AFTERNOON,
                            onClick = { onSelectPeriod(AbsencePeriod.AFTERNOON) },
                            label = { Text("مسائية (13:30 - 16:30)", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SchoolGreen,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        // Live Counters Banner
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CounterCard(
                    modifier = Modifier.weight(1f),
                    title = "إجمالي القسم",
                    value = "$totalCount",
                    color = SchoolGreenDark,
                    bgColor = SurfaceTint
                )
                CounterCard(
                    modifier = Modifier.weight(1f),
                    title = "الحاضرون",
                    value = "$presentCount",
                    color = SuccessGreen,
                    bgColor = SuccessBg
                )
                CounterCard(
                    modifier = Modifier.weight(1f),
                    title = "الغائبون",
                    value = "$absentCount",
                    color = DangerRed,
                    bgColor = DangerBg
                )
            }
        }

        // Quick Bulk Actions & Search
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = { onMarkAll(false) },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("الكل حاضر", fontSize = 11.sp, color = SuccessGreen, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { onMarkAll(true) },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("الكل غائب", fontSize = 11.sp, color = DangerRed, fontWeight = FontWeight.Bold)
                    }
                }

                // Submit Button
                Button(
                    onClick = onSubmit,
                    colors = ButtonDefaults.buttonColors(containerColor = SchoolGreen),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("حفظ التقرير", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Search Box
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("بحث عن تلميذ بالاسم أو رقم الباركود...", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Students Attendance List
        items(students, key = { it.id }) { student ->
            val isAbsent = absentMap[student.id] ?: false
            Surface(
                color = CardSurface,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, if (isAbsent) DangerRed.copy(alpha = 0.5f) else BorderLight),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleAbsent(student.id, !isAbsent) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = if (isAbsent) DangerBg else SuccessBg,
                            shape = CircleShape,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    if (isAbsent) "✕" else "✓",
                                    color = if (isAbsent) DangerRed else SuccessGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                        Column {
                            Text(student.fullName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("باركود: ${student.barcode}", fontSize = 10.sp, color = TextMuted)
                        }
                    }

                    // Attendance Toggle Pill
                    Surface(
                        color = if (isAbsent) DangerRed else SuccessGreen,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (isAbsent) "غائب" else "حاضر",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CounterCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    color: Color,
    bgColor: Color
) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = color)
            Text(title, fontSize = 10.sp, color = color.copy(alpha = 0.8f))
        }
    }
}

@Composable
private fun GradebookTab(
    students: List<Student>,
    grades: List<GradeEntry>,
    onSubmitForApproval: (String, String) -> Unit
) {
    var selectedTerm by remember { mutableStateOf("الفصل الثالث") }
    var selectedSubject by remember { mutableStateOf("التربية البدنية والرياضية") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, BorderLight)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("دفتر النقاط والتقييم المستمر", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("المادة: $selectedSubject · $selectedTerm", fontSize = 11.sp, color = TextSecondary)
                        }
                        Button(
                            onClick = { onSubmitForApproval(selectedTerm, selectedSubject) },
                            colors = ButtonDefaults.buttonColors(containerColor = SchoolGold),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("إرسال للاعتماد", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SchoolGreenDark)
                        }
                    }
                }
            }
        }

        items(students, key = { it.id }) { student ->
            val grade = grades.find { it.studentId == student.id && it.term == selectedTerm && it.subject == selectedSubject }
            var continuous by remember(grade) { mutableStateOf(grade?.continuous?.toString() ?: "8.5") }
            var exam by remember(grade) { mutableStateOf(grade?.exam?.toString() ?: "9.0") }
            var obs by remember(grade) { mutableStateOf(grade?.observation ?: "مستوى متميز ومواظب") }

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
                        Text(student.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Surface(
                            color = when (grade?.status) {
                                GradeStatus.APPROVED -> SuccessBg
                                GradeStatus.SUBMITTED -> WarningBg
                                else -> SurfaceTint
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                grade?.status?.titleAr ?: GradeStatus.DRAFT.titleAr,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (grade?.status) {
                                    GradeStatus.APPROVED -> SuccessGreen
                                    GradeStatus.SUBMITTED -> WarningOrange
                                    else -> TextSecondary
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = continuous,
                            onValueChange = { continuous = it },
                            label = { Text("تقويم /10", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                        OutlinedTextField(
                            value = exam,
                            onValueChange = { exam = it },
                            label = { Text("اختبار /10", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = obs,
                        onValueChange = { obs = it },
                        label = { Text("ملاحظة تربوية", fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AlignEnd {
                        TextButton(
                            onClick = {
                                SchoolRepository.saveGrade(
                                    studentId = student.id,
                                    term = selectedTerm,
                                    subject = selectedSubject,
                                    continuous = continuous.toDoubleOrNull(),
                                    exam = exam.toDoubleOrNull(),
                                    observation = obs
                                )
                            }
                        ) {
                            Text("حفظ النقطة", fontSize = 11.sp, color = SchoolGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AlignEnd(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
        content()
    }
}

@Composable
private fun TimetableTab(schedules: List<Schedule>) {
    val days = listOf("الأحد", "الإثنين", "الثلاثاء", "الأربعاء", "الخميس")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Surface(
                color = CardSurface,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("التوزيع الزمني الأسبوعي المعتمد", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("السنة الدراسية: 2026/2027 · نظام الدوام الواحد", fontSize = 11.sp, color = TextSecondary)
                    }
                    Text("24 ساعة/أسبوع", fontWeight = FontWeight.ExtraBold, color = SchoolGreen, fontSize = 13.sp)
                }
            }
        }

        items(days.indices.toList()) { dayIndex ->
            val daySchedules = schedules.filter { it.dayOfWeek == dayIndex }
            Surface(
                color = CardSurface,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        days[dayIndex],
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = SchoolGreenDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (daySchedules.isEmpty()) {
                        Text("لا توجد حصص مجدولة لهذا اليوم", fontSize = 11.sp, color = TextMuted)
                    } else {
                        daySchedules.forEach { s ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("• ${s.subject} (${s.classroomName})", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Text(s.period.titleAr, fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AbsenceHistoryTab(absences: List<Absence>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text("سجل الغيابات المسجلة", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = SchoolGreenDark)
        }
        if (absences.isEmpty()) {
            item {
                Text("لا توجد غيابات مسجلة حالياً، جميع التلاميذ حاضرون.", fontSize = 12.sp, color = TextSecondary)
            }
        } else {
            items(absences, key = { it.id }) { a ->
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
                            Text(a.studentName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("${a.classroomName} · ${a.period.titleAr}", fontSize = 10.sp, color = TextSecondary)
                        }
                        Text(a.date, fontSize = 11.sp, color = TextMuted)
                    }
                }
            }
        }
    }
}
