package com.example.schoolplatform.data.repository

import com.example.schoolplatform.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

object SchoolRepository {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val todayStr: String get() = dateFormat.format(Date())

    // Initial Users
    private val initialUsers = listOf(
        User(1, "admin", "admin123", Role.ADMIN, "مدير المؤسسة", isActive = true, isOnline = true),
        User(2, "teacher1", "teacher123", Role.TEACHER, "الأستاذ أحمد", isActive = true, isOnline = true),
        User(3, "restaurant1", "resto123", Role.RESTAURANT_MANAGER, "مشرف المطعم", isActive = true, isOnline = true)
    )

    // Initial Classrooms
    private val initialClassrooms = listOf(
        Classroom(1, "السنة الخامسة أ", "السنة الخامسة"),
        Classroom(2, "السنة الرابعة أ", "السنة الرابعة"),
        Classroom(3, "السنة الثالثة أ", "السنة الثالثة")
    )

    // Initial Students
    private val initialStudents = listOf(
        Student(1, "1000001", "ياسين بلقاسم", 1, "0550112233"),
        Student(2, "1000002", "سارة عمراني", 1, "0550223344"),
        Student(3, "1000003", "محمد شريف", 1, "0550334455"),
        Student(4, "1000004", "أمينة بوزيد", 1, "0550445566"),
        Student(5, "1000005", "كريم حداد", 1, "0550556677"),
        Student(6, "1000006", "نور الهدى بن سالم", 2, "0661123456"),
        Student(7, "1000007", "أيوب بوعلام", 2, "0661789012"),
        Student(8, "1000008", "فاطمة الزهراء طاهري", 3, "0770345678")
    )

    // Initial Inventory
    private val initialInventory = listOf(
        InventoryItem(1, "عدس", 40.0, "كغ", 10.0),
        InventoryItem(2, "أرز", 8.0, "كغ", 10.0),
        InventoryItem(3, "زيت المائدة", 15.0, "لتر", 5.0),
        InventoryItem(4, "معكرونة", 30.0, "كغ", 10.0),
        InventoryItem(5, "طماطم مصبرة", 12.0, "علبة", 6.0)
    )

    // Initial Budget
    private val initialBudgetLines = listOf(
        BudgetLine(1, "2026/2027", BudgetCategory.OPERATING, "لوازم النظافة والتسيير", 120000.0, 35000.0, "ACTIVE", "اعتماد تشغيلي دوري"),
        BudgetLine(2, "2026/2027", BudgetCategory.CANTEEN, "تموين المطعم المدرسي", 450000.0, 185000.0, "ACTIVE", "المواد الغذائية الأساسية والخضر"),
        BudgetLine(3, "2026/2027", BudgetCategory.MAINTENANCE, "صيانة المرافق والتدفئة", 180000.0, 60000.0, "ACTIVE", "إصلاحات دورية ومتابعة شبكة المياه"),
        BudgetLine(4, "2026/2027", BudgetCategory.EQUIPMENT, "وسائل وأجهزة تعليمية", 250000.0, 0.0, "ACTIVE", "مقترح اقتناء أجهزة عرض وإعلام آلي"),
        BudgetLine(5, "2026/2027", BudgetCategory.ACTIVITIES, "الأنشطة التربوية والرياضية", 80000.0, 15000.0, "ACTIVE", "جوائز وتحضير البطولات المدرسية")
    )

    private val initialTransactions = listOf(
        BudgetTransaction(1, 1, "لوازم النظافة والتسيير", 35000.0, "فاتورة مواد التنظيف ومطهرات", "EXPENSE", "مدير المؤسسة", "2026-09-10 10:30"),
        BudgetTransaction(2, 2, "تموين المطعم المدرسي", 185000.0, "دفعة التموين الأولى (بقوليات وزيوت)", "EXPENSE", "مدير المؤسسة", "2026-09-12 14:15"),
        BudgetTransaction(3, 3, "صيانة المرافق والتدفئة", 60000.0, "إصلاحات السباكة وصيانة دورات المياه", "EXPENSE", "مدير المؤسسة", "2026-09-14 09:00"),
        BudgetTransaction(4, 5, "الأنشطة التربوية والرياضية", 15000.0, "كرات رياضية وعتاد جمباز", "EXPENSE", "مدير المؤسسة", "2026-09-15 11:20")
    )

    // Initial Schedules
    private val initialSchedules = listOf(
        Schedule(1, 2, "الأستاذ أحمد", 1, "السنة الخامسة أ", 0, AbsencePeriod.MORNING, "اللغة العربية"),
        Schedule(2, 2, "الأستاذ أحمد", 1, "السنة الخامسة أ", 0, AbsencePeriod.AFTERNOON, "الرياضيات"),
        Schedule(3, 2, "الأستاذ أحمد", 1, "السنة الخامسة أ", 1, AbsencePeriod.MORNING, "التربية الإسلامية"),
        Schedule(4, 2, "الأستاذ أحمد", 1, "السنة الخامسة أ", 2, AbsencePeriod.MORNING, "التربية العلمية والتكنولوجية"),
        Schedule(5, 2, "الأستاذ أحمد", 1, "السنة الخامسة أ", 3, AbsencePeriod.MORNING, "التربية البدنية والرياضية"),
        Schedule(6, 2, "الأستاذ أحمد", 1, "السنة الخامسة أ", 4, AbsencePeriod.MORNING, "تاريخ وجغرافيا")
    )

    // Initial Facilities
    private val initialFacilities = listOf(
        Facility(1, "حجرة 01 (السنة الخامسة)", "CLASSROOM", 36),
        Facility(2, "حجرة 02 (السنة الرابعة)", "CLASSROOM", 34),
        Facility(3, "مطعم المدرسة المركزي", "CANTEEN", 150),
        Facility(4, "الفناء والملعب الرياضي", "SPORT", 120),
        Facility(5, "قاعة المطالعة والإعلام الآلي", "ACTIVITY", 40)
    )

    // Initial Quotas
    private val initialQuotas = listOf(
        SubjectWeeklyQuota(1, "2026/2027", "السنة الخامسة", "اللغة العربية", 8.0, 45),
        SubjectWeeklyQuota(2, "2026/2027", "السنة الخامسة", "الرياضيات", 5.0, 45),
        SubjectWeeklyQuota(3, "2026/2027", "السنة الخامسة", "التربية البدنية", 2.0, 45),
        SubjectWeeklyQuota(4, "2026/2027", "السنة الخامسة", "التربية الإسلامية", 2.0, 45)
    )

    // Initial Support Staff (العمال المهنيين)
    private val initialSupportStaff = listOf(
        SupportStaff(
            id = 1,
            fullName = "عمران لخضر",
            jobTitle = "حارس المؤسسة (أمن واستقبال)",
            rank = "عامل مهني صنف 01 (حراسة)",
            phone = "0661112233",
            assignedArea = "المدخل الرئيسي وساحة المؤسسة",
            shiftType = "دوام كامل (مناوبة نهارية)",
            isPresentToday = true,
            notes = "مكلف بفتح الأبواب، تأمين دخول التلاميذ وسجل الزوار",
            weeklySchedule = listOf(
                WorkDaySchedule("الأحد", "07:00 - 12:00", "13:00 - 17:00", "تأمين دخول التلاميذ، تنظيم حركة الأولياء، مراقبة الساحة"),
                WorkDaySchedule("الإثنين", "07:00 - 12:00", "13:00 - 17:00", "حراسة المدخل، تسجيل الزيارات الإدارية، غلق الباب وقت الحصص"),
                WorkDaySchedule("الثلاثاء", "07:00 - 12:30", "عطلة نصف يوم", "تأمين نصف اليوم الدراسي، تفقد منافذ المؤسسة والأسوار"),
                WorkDaySchedule("الأربعاء", "07:00 - 12:00", "13:00 - 17:00", "مراقبة دخول وخروج الأساتذة والتلاميذ، متابعة الواجهة الخارجية"),
                WorkDaySchedule("الخميس", "07:00 - 12:00", "13:00 - 17:30", "تأمين خروج نهاية الأسبوع وغلق المؤسسة والتأكد من إحكام المنافذ")
            )
        ),
        SupportStaff(
            id = 2,
            fullName = "فاطمة بن يحيى",
            jobTitle = "طباخة رئيسية للمطعم المدرسي",
            rank = "عاملة مهنية صنف 02 (إطعام)",
            phone = "0551223344",
            assignedArea = "المطعم المدرسي المركزي وقاعة الطهي",
            shiftType = "دوام كامل (08:00 - 15:00)",
            isPresentToday = true,
            notes = "مسؤولة عن إعداد الوجبات الساخنة ومراقبة السلامة الغذائية",
            weeklySchedule = listOf(
                WorkDaySchedule("الأحد", "08:00 - 12:00", "12:00 - 14:30", "استلام الخضر والحبوب، تحضير وجبة الغداء الساخنة (عدس) وتوزيعها"),
                WorkDaySchedule("الإثنين", "08:00 - 12:00", "12:00 - 14:30", "طهي وجبة الأرز بالصلصة والبيض، تعقيم أواني الطهي"),
                WorkDaySchedule("الثلاثاء", "08:00 - 12:30", "12:30 - 13:30", "تحضير وجبة خفيفة ومطهرة، تنظيف عمومي لقاعة الطهي والمداخن"),
                WorkDaySchedule("الأربعاء", "08:00 - 12:00", "12:00 - 14:30", "إعداد وجبة الفاصوليا والخضار الطازجة مع الفاكهة الموسمية"),
                WorkDaySchedule("الخميس", "08:00 - 12:00", "12:00 - 15:00", "طهي الكسكس بالخضر، جرد المواد المستهلكة، وغسيل معدات المطبخ")
            )
        ),
        SupportStaff(
            id = 3,
            fullName = "حليمة بوجمعة",
            jobTitle = "عاملة نظافة وصيانة دورية",
            rank = "عاملة مهنية صنف 01 (نظافة)",
            phone = "0772334455",
            assignedArea = "الجناح التربوي (أ) والمكاتب الإدارية",
            shiftType = "دوام كامل (07:30 - 16:00)",
            isPresentToday = true,
            notes = "تنظيف وتعقيم الحجرات الدراسية وقاعة الأساتذة والممرات",
            weeklySchedule = listOf(
                WorkDaySchedule("الأحد", "07:30 - 12:00", "13:00 - 16:00", "كنس وغسيل الحجرات 1 و 2 و 3، مسح السبورة، وتفريغ السلات"),
                WorkDaySchedule("الإثنين", "07:30 - 12:00", "13:00 - 16:00", "تعقيم دورات المياه، غسيل ممرات الجناح التربوي بالمنظفات"),
                WorkDaySchedule("الثلاثاء", "07:30 - 12:30", "أشغال النظافة الدورية", "تنظيف مكتب المدير والمطالعة وقاعة الأساتذة والمكاتب"),
                WorkDaySchedule("الأربعاء", "07:30 - 12:00", "13:00 - 16:00", "تنظيف الحجرات 4 و 5، مسح النوافذ، وتعقيم مقابض الأبواب"),
                WorkDaySchedule("الخميس", "07:30 - 12:00", "13:00 - 16:30", "التنظيف الأسبوعي الشامل للممرات المشتركة وتهوية القاعات")
            )
        ),
        SupportStaff(
            id = 4,
            fullName = "عبد القادر مسعودي",
            jobTitle = "عون صيانة وترميم وشبكات وتدفئة",
            rank = "عامل مهني صنف 03 (صيانة عامة)",
            phone = "0663445566",
            assignedArea = "مرافق المؤسسة، شبكات المياه، والتدفئة المركزية",
            shiftType = "دوام كامل (08:00 - 16:30)",
            isPresentToday = true,
            notes = "متابعة أجهزة التدفئة شتاءً، السباكة، وإصلاح الكراسي والطاولات",
            weeklySchedule = listOf(
                WorkDaySchedule("الأحد", "08:00 - 12:00", "13:00 - 16:30", "فحص خزانات ومضخات المياه، صمامات التدفئة، وصنابير الساحات"),
                WorkDaySchedule("الإثنين", "08:00 - 12:00", "13:00 - 16:30", "إصلاح الطاولات والمقاعد المتضررة في ورشة الصيانة المدرسية"),
                WorkDaySchedule("الثلاثاء", "08:00 - 12:30", "مراجعة الكهرباء", "تفقد لوحات الكهرباء ومصابيح الإنارة في الحجرات والممرات"),
                WorkDaySchedule("الأربعاء", "08:00 - 12:00", "13:00 - 16:30", "صيانة دورات مياه التلاميذ وإصلاح أقفال الأبواب والنوافذ"),
                WorkDaySchedule("الخميس", "08:00 - 12:00", "13:00 - 16:00", "تفقد التجهيزات العامة وتأمين شبكة الغاز والماء قبل العطلة")
            )
        ),
        SupportStaff(
            id = 5,
            fullName = "خديجة مرابط",
            jobTitle = "عاملة مساعدة بالمطعم والخدمات العامة",
            rank = "عاملة مهنية صنف 01",
            phone = "0554556677",
            assignedArea = "قاعة الإطعام ومحيط المطعم المدرسي",
            shiftType = "دوام كامل (08:00 - 15:30)",
            isPresentToday = true,
            notes = "تنظيم طاولات التلاميذ، غسيل الأواني، وتعقيم قاعة الإطعام",
            weeklySchedule = listOf(
                WorkDaySchedule("الأحد", "08:00 - 12:00", "12:00 - 15:30", "فرز الأواني، مساعدة الطباخة في تقطيع الخضر، وتقديم الوجبات"),
                WorkDaySchedule("الإثنين", "08:00 - 12:00", "12:00 - 15:30", "تنظيف وغسيل صحون التلاميذ، تعقيم الطاولات بعد انتهاء الغداء"),
                WorkDaySchedule("الثلاثاء", "08:00 - 13:00", "نظافة المطعم", "غسيل أرضيات قاعة الإطعام بالماء والمطهر والتهوية الكاملة"),
                WorkDaySchedule("الأربعاء", "08:00 - 12:00", "12:00 - 15:30", "تنظيم طابور دخول التلاميذ وتوزيع الفواكه والخبز ومسح المقاعد"),
                WorkDaySchedule("الخميس", "08:00 - 12:00", "12:00 - 15:30", "غسيل كلي للمفروشات والأواني وترتيب المخزن لليوم الموالي")
            )
        )
    )

    // Initial Audit
    private val initialAuditLogs = listOf(
        AuditLog(1, 1, "مدير المؤسسة", "CREATE_USER", "إنشاء مستخدم", "المستخدمين", 2, "إنشاء حساب الأستاذ أحمد", "2026-09-01 08:30"),
        AuditLog(2, 1, "مدير المؤسسة", "EDIT_BUDGET", "اعتماد ميزانية", "الميزانية", 2, "تخصيص ميزانية المطعم 450,000 دج", "2026-09-05 09:10"),
        AuditLog(3, 2, "الأستاذ أحمد", "SUBMIT_ABSENCE", "تسجيل غياب", "الحضور", 1, "تسجيل غياب الحصة الصباحية (السنة الخامسة أ)", "2026-09-17 08:45"),
        AuditLog(4, 3, "مشرف المطعم", "SCAN_MEAL", "صرف وجبة", "المطعم", 1, "صرف وجبة الغداء للتلميذ ياسين بلقاسم", "2026-09-17 12:15")
    )

    // State Flows
    private val _currentUser = MutableStateFlow<User?>(initialUsers[0]) // default to Admin
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _users = MutableStateFlow(initialUsers)
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _classrooms = MutableStateFlow(initialClassrooms)
    val classrooms: StateFlow<List<Classroom>> = _classrooms.asStateFlow()

    private val _students = MutableStateFlow(initialStudents)
    val students: StateFlow<List<Student>> = _students.asStateFlow()

    private val _absences = MutableStateFlow<List<Absence>>(listOf(
        Absence(1, 4, "أمينة بوزيد", "السنة الخامسة أ", todayStr, AbsencePeriod.MORNING)
    ))
    val absences: StateFlow<List<Absence>> = _absences.asStateFlow()

    private val _grades = MutableStateFlow<List<GradeEntry>>(listOf(
        GradeEntry(1, 1, "ياسين بلقاسم", "السنة الخامسة أ", 2, "الفصل الثالث", "التربية البدنية والرياضية", 9.0, 9.5, "ممتاز ونشيط جداً", GradeStatus.SUBMITTED),
        GradeEntry(2, 2, "سارة عمراني", "السنة الخامسة أ", 2, "الفصل الثالث", "التربية البدنية والرياضية", 8.5, 9.0, "مشاركة متميزة وسلوك قدوة", GradeStatus.SUBMITTED),
        GradeEntry(3, 3, "محمد شريف", "السنة الخامسة أ", 2, "الفصل الثالث", "التربية البدنية والرياضية", 7.5, 8.0, "مستوى جيد مع إمكانية التحسن", GradeStatus.DRAFT)
    ))
    val grades: StateFlow<List<GradeEntry>> = _grades.asStateFlow()

    private val _dailyMeal = MutableStateFlow(
        DailyMeal(1, todayStr, MealPeriod.LUNCH, "عدس بالخضر وأرز طازج مع فاكهة موسمية", 0.0)
    )
    val dailyMeal: StateFlow<DailyMeal> = _dailyMeal.asStateFlow()

    private val _mealConsumptions = MutableStateFlow<List<MealConsumption>>(listOf(
        MealConsumption(1, 1, "ياسين بلقاسم", 1, "12:10", "restaurant1")
    ))
    val mealConsumptions: StateFlow<List<MealConsumption>> = _mealConsumptions.asStateFlow()

    private val _inventory = MutableStateFlow(initialInventory)
    val inventory: StateFlow<List<InventoryItem>> = _inventory.asStateFlow()

    private val _budgetLines = MutableStateFlow(initialBudgetLines)
    val budgetLines: StateFlow<List<BudgetLine>> = _budgetLines.asStateFlow()

    private val _budgetTransactions = MutableStateFlow(initialTransactions)
    val budgetTransactions: StateFlow<List<BudgetTransaction>> = _budgetTransactions.asStateFlow()

    private val _schedules = MutableStateFlow(initialSchedules)
    val schedules: StateFlow<List<Schedule>> = _schedules.asStateFlow()

    private val _facilities = MutableStateFlow(initialFacilities)
    val facilities: StateFlow<List<Facility>> = _facilities.asStateFlow()

    private val _quotas = MutableStateFlow(initialQuotas)
    val quotas: StateFlow<List<SubjectWeeklyQuota>> = _quotas.asStateFlow()

    private val _scheduleConfig = MutableStateFlow(SchoolScheduleConfig())
    val scheduleConfig: StateFlow<SchoolScheduleConfig> = _scheduleConfig.asStateFlow()

    private val _supportStaff = MutableStateFlow(initialSupportStaff)
    val supportStaff: StateFlow<List<SupportStaff>> = _supportStaff.asStateFlow()

    private val _auditLogs = MutableStateFlow(initialAuditLogs)
    val auditLogs: StateFlow<List<AuditLog>> = _auditLogs.asStateFlow()

    // Auth methods
    fun login(username: String, password: String):User? {
        val user = _users.value.find { it.username == username && it.passwordHash == password && it.isActive }
        if (user != null) {
            _currentUser.value = user
            addAuditLog("LOGIN", "تسجيل دخول", user.id, "تسجيل دخول المستخدم ${user.fullName}")
        }
        return user
    }

    fun switchUser(user: User) {
        _currentUser.value = user
        addAuditLog("SWITCH_USER", "تبديل مستخدم", user.id, "الانتقال إلى حساب ${user.fullName}")
    }

    fun logout() {
        val current = _currentUser.value
        if (current != null) {
            addAuditLog("LOGOUT", "تسجيل خروج", current.id, "تسجيل خروج ${current.fullName}")
        }
        _currentUser.value = null
    }

    // Attendance
    fun submitAbsences(classroomId: Int, period: AbsencePeriod, absentStudentIds: Set<Int>) {
        val classroom = _classrooms.value.find { it.id == classroomId } ?: return
        val currentList = _absences.value.filterNot { it.classroomName == classroom.name && it.date == todayStr && it.period == period }.toMutableList()
        val studentsList = _students.value.filter { it.classroomId == classroomId }
        
        absentStudentIds.forEach { studentId ->
            val student = studentsList.find { it.id == studentId }
            if (student != null) {
                currentList.add(
                    Absence(
                        id = (currentList.maxOfOrNull { it.id } ?: 0) + 1,
                        studentId = student.id,
                        studentName = student.fullName,
                        classroomName = classroom.name,
                        date = todayStr,
                        period = period
                    )
                )
            }
        }
        _absences.value = currentList
        addAuditLog("SUBMIT_ABSENCE", "تسجيل غياب", classroomId, "تسجيل ${absentStudentIds.size} غائب في ${classroom.name} (${period.titleAr})")
    }

    // Grades
    fun saveGrade(studentId: Int, term: String, subject: String, continuous: Double?, exam: Double?, observation: String?) {
        val student = _students.value.find { it.id == studentId } ?: return
        val classroom = _classrooms.value.find { it.id == student.classroomId }
        val currentList = _grades.value.toMutableList()
        val index = currentList.indexOfFirst { it.studentId == studentId && it.term == term && it.subject == subject }
        
        val teacherId = _currentUser.value?.id ?: 2
        val entry = GradeEntry(
            id = if (index >= 0) currentList[index].id else (currentList.maxOfOrNull { it.id } ?: 0) + 1,
            studentId = studentId,
            studentName = student.fullName,
            classroomName = classroom?.name ?: "",
            teacherId = teacherId,
            term = term,
            subject = subject,
            continuous = continuous,
            exam = exam,
            observation = observation,
            status = GradeStatus.DRAFT
        )
        if (index >= 0) {
            currentList[index] = entry
        } else {
            currentList.add(entry)
        }
        _grades.value = currentList
    }

    fun submitGradesForApproval(term: String, subject: String) {
        val currentList = _grades.value.map {
            if (it.term == term && it.subject == subject && it.status == GradeStatus.DRAFT) {
                it.copy(status = GradeStatus.SUBMITTED)
            } else it
        }
        _grades.value = currentList
        addAuditLog("SUBMIT_GRADES", "إرسال نقاط للاعتماد", null, "إرسال دفتر نقاط $subject ($term) للاعتماد")
    }

    fun approveGrades(term: String, subject: String) {
        val currentList = _grades.value.map {
            if (it.term == term && it.subject == subject) {
                it.copy(status = GradeStatus.APPROVED)
            } else it
        }
        _grades.value = currentList
        addAuditLog("APPROVE_GRADES", "اعتماد دفتر النقاط", null, "اعتماد رسمي لدفتر نقاط $subject ($term)")
    }

    // Restaurant Canteen Scanner
    fun scanMeal(barcode: String, period: MealPeriod): MealScanResult {
        val trimmed = barcode.trim()
        val student = _students.value.find { it.barcode == trimmed }
            ?: return MealScanResult.NotFound

        // Check if already consumed today
        val alreadyConsumed = _mealConsumptions.value.any { it.studentId == student.id && it.mealId == _dailyMeal.value.id }
        if (alreadyConsumed) {
            return MealScanResult.AlreadyTaken(student.fullName)
        }

        // Check morning absence warning
        val isAbsentMorning = _absences.value.any { it.studentId == student.id && it.date == todayStr && it.period == AbsencePeriod.MORNING }

        // Record consumption
        val newRecord = MealConsumption(
            id = (_mealConsumptions.value.maxOfOrNull { it.id } ?: 0) + 1,
            studentId = student.id,
            studentName = student.fullName,
            mealId = _dailyMeal.value.id,
            scannedAt = timeFormat.format(Date()),
            scannedBy = _currentUser.value?.username ?: "restaurant1"
        )
        _mealConsumptions.value = _mealConsumptions.value + newRecord
        addAuditLog("SCAN_MEAL", "صرف وجبة", student.id, "تقديم وجبة الغداء للتلميذ ${student.fullName}")

        return if (isAbsentMorning) {
            MealScanResult.AbsentWarning(student.fullName)
        } else {
            MealScanResult.Success(student.fullName)
        }
    }

    // Budget Operations
    fun addBudgetLine(category: BudgetCategory, title: String, allocatedAmount: Double, notes: String?) {
        val newLine = BudgetLine(
            id = (_budgetLines.value.maxOfOrNull { it.id } ?: 0) + 1,
            schoolYear = "2026/2027",
            category = category,
            title = title,
            allocatedAmount = allocatedAmount,
            spentAmount = 0.0,
            status = "ACTIVE",
            notes = notes
        )
        _budgetLines.value = _budgetLines.value + newLine
        addAuditLog("ADD_BUDGET_LINE", "إضافة بند مالي", newLine.id, "إضافة اعتماد: $title بمبلغ $allocatedAmount دج")
    }

    fun addBudgetExpense(lineId: Int, amount: Double, description: String) {
        val line = _budgetLines.value.find { it.id == lineId } ?: return
        val updatedLines = _budgetLines.value.map {
            if (it.id == lineId) it.copy(spentAmount = it.spentAmount + amount) else it
        }
        _budgetLines.value = updatedLines

        val newTransaction = BudgetTransaction(
            id = (_budgetTransactions.value.maxOfOrNull { it.id } ?: 0) + 1,
            lineId = lineId,
            lineTitle = line.title,
            amount = amount,
            description = description,
            kind = "EXPENSE",
            createdByName = _currentUser.value?.fullName ?: "مدير المؤسسة",
            createdAt = "$todayStr ${timeFormat.format(Date())}"
        )
        _budgetTransactions.value = listOf(newTransaction) + _budgetTransactions.value
        addAuditLog("ADD_EXPENSE", "صرف نفقة مالية", lineId, "تسجيل نفقة $amount دج على بند ${line.title}: $description")
    }

    // Inventory
    fun addInventoryItem(name: String, quantity: Double, unit: String, threshold: Double) {
        val newItem = InventoryItem(
            id = (_inventory.value.maxOfOrNull { it.id } ?: 0) + 1,
            name = name,
            quantity = quantity,
            unit = unit,
            lowStockThreshold = threshold
        )
        _inventory.value = _inventory.value + newItem
        addAuditLog("ADD_INVENTORY", "إضافة مادة للمخزون", newItem.id, "إضافة مادة $name بكمية $quantity $unit")
    }

    fun updateInventoryStock(itemId: Int, delta: Double) {
        val updated = _inventory.value.map {
            if (it.id == itemId) it.copy(quantity = (it.quantity + delta).coerceAtLeast(0.0)) else it
        }
        _inventory.value = updated
    }

    // Classrooms and Students
    fun addClassroom(name: String) {
        val newClass = Classroom(
            id = (_classrooms.value.maxOfOrNull { it.id } ?: 0) + 1,
            name = name,
            level = "الطور الابتدائي"
        )
        _classrooms.value = _classrooms.value + newClass
        addAuditLog("ADD_CLASSROOM", "إضافة قسم", newClass.id, "إضافة القسم الجديد: $name")
    }

    fun addStudent(fullName: String, barcode: String, classroomId: Int, phone: String?) {
        val newStudent = Student(
            id = (_students.value.maxOfOrNull { it.id } ?: 0) + 1,
            barcode = barcode,
            fullName = fullName,
            classroomId = classroomId,
            parentPhone = phone ?: "0550000000"
        )
        _students.value = _students.value + newStudent
        val className = _classrooms.value.find { it.id == classroomId }?.name ?: ""
        addAuditLog("ADD_STUDENT", "تسجيل تلميذ جديد", newStudent.id, "تسجيل التلميذ $fullName بالقسم $className")
    }

    // Timetable Schedules
    fun addSchedule(teacherId: Int, classroomId: Int, dayOfWeek: Int, period: AbsencePeriod, subject: String) {
        val teacher = _users.value.find { it.id == teacherId } ?: return
        val classroom = _classrooms.value.find { it.id == classroomId } ?: return
        val newSchedule = Schedule(
            id = (_schedules.value.maxOfOrNull { it.id } ?: 0) + 1,
            teacherId = teacherId,
            teacherName = teacher.fullName,
            classroomId = classroomId,
            classroomName = classroom.name,
            dayOfWeek = dayOfWeek,
            period = period,
            subject = subject,
            status = "APPROVED"
        )
        _schedules.value = _schedules.value + newSchedule
        addAuditLog("ADD_SCHEDULE", "إضافة حصة دراسية", newSchedule.id, "إسناد حصة $subject لـ ${teacher.fullName} مع ${classroom.name}")
    }

    fun removeSchedule(scheduleId: Int) {
        _schedules.value = _schedules.value.filterNot { it.id == scheduleId }
    }

    fun approveAllSchedules() {
        _schedules.value = _schedules.value.map { it.copy(status = "APPROVED") }
        addAuditLog("APPROVE_SCHEDULES", "اعتماد التوزيع الأسبوعي", null, "اعتماد جدول الحصص الأسبوعي لجميع الأقسام")
    }

    fun autoGenerateSchedules() {
        // Generates structured weekly timetable based on teachers and classes
        val teachers = _users.value.filter { it.role == Role.TEACHER }
        val classes = _classrooms.value
        if (teachers.isEmpty() || classes.isEmpty()) return
        
        val teacher = teachers.first()
        val subjects = listOf("اللغة العربية", "الرياضيات", "التربية الإسلامية", "التربية العلمية", "التربية البدنية")
        val generated = mutableListOf<Schedule>()
        var idCounter = 1
        
        for (day in 0..4) {
            classes.forEachIndexed { classIndex, cls ->
                val subj1 = subjects[(day + classIndex) % subjects.size]
                val subj2 = subjects[(day + classIndex + 1) % subjects.size]
                generated.add(Schedule(idCounter++, teacher.id, teacher.fullName, cls.id, cls.name, day, AbsencePeriod.MORNING, subj1))
                generated.add(Schedule(idCounter++, teacher.id, teacher.fullName, cls.id, cls.name, day, AbsencePeriod.AFTERNOON, subj2))
            }
        }
        _schedules.value = generated
        addAuditLog("GENERATE_SCHEDULES", "توليد التوزيع آلياً", null, "توليد ${generated.size} حصة دراسية أسبوعية بنجاح")
    }

    // Facilities & Quotas
    fun addFacility(name: String, kind: String, capacity: Int?) {
        val newFacility = Facility(
            id = (_facilities.value.maxOfOrNull { it.id } ?: 0) + 1,
            name = name,
            kind = kind,
            capacity = capacity
        )
        _facilities.value = _facilities.value + newFacility
        addAuditLog("ADD_FACILITY", "إضافة مرفق", newFacility.id, "تسجيل مرفق: $name ($kind)")
    }

    fun addSubjectQuota(level: String, subject: String, lessonsPerWeek: Double, minutesPerLesson: Int) {
        val newQuota = SubjectWeeklyQuota(
            id = (_quotas.value.maxOfOrNull { it.id } ?: 0) + 1,
            schoolYear = "2026/2027",
            level = level,
            subject = subject,
            lessonsPerWeek = lessonsPerWeek,
            minutesPerLesson = minutesPerLesson
        )
        _quotas.value = _quotas.value + newQuota
        addAuditLog("ADD_QUOTA", "تحديد حجم ساعي", newQuota.id, "تحديد $lessonsPerWeek حصة لمادة $subject ($level)")
    }

    // Users
    fun createUser(username: String, password: String, fullName: String, role: Role) {
        val newUser = User(
            id = (_users.value.maxOfOrNull { it.id } ?: 0) + 1,
            username = username,
            passwordHash = password,
            role = role,
            fullName = fullName,
            isActive = true,
            isOnline = false
        )
        _users.value = _users.value + newUser
        addAuditLog("CREATE_USER", "إنشاء حساب", newUser.id, "إنشاء حساب ${role.titleAr}: $fullName ($username)")
    }

    fun toggleUserActive(userId: Int) {
        val updated = _users.value.map {
            if (it.id == userId) it.copy(isActive = !it.isActive) else it
        }
        _users.value = updated
    }

    // Support Staff (العمال المهنيين)
    fun toggleStaffAttendance(staffId: Int) {
        val updated = _supportStaff.value.map {
            if (it.id == staffId) {
                val newPresence = !it.isPresentToday
                val statusText = if (newPresence) "تسجيل حضور" else "تسجيل غياب"
                addAuditLog("STAFF_ATTENDANCE", statusText, staffId, "$statusText للعامل المهني ${it.fullName}")
                it.copy(isPresentToday = newPresence)
            } else it
        }
        _supportStaff.value = updated
    }

    fun addSupportStaff(
        fullName: String,
        jobTitle: String,
        rank: String,
        phone: String,
        assignedArea: String,
        shiftType: String,
        notes: String? = null
    ) {
        val newStaff = SupportStaff(
            id = (_supportStaff.value.maxOfOrNull { it.id } ?: 0) + 1,
            fullName = fullName,
            jobTitle = jobTitle,
            rank = rank,
            phone = phone,
            assignedArea = assignedArea,
            shiftType = shiftType,
            isPresentToday = true,
            notes = notes,
            weeklySchedule = listOf(
                WorkDaySchedule("الأحد", "07:30 - 12:00", "13:00 - 16:30", "أداء المهام اليومية في $assignedArea"),
                WorkDaySchedule("الإثنين", "07:30 - 12:00", "13:00 - 16:30", "متابعة النظافة والصيانة في $assignedArea"),
                WorkDaySchedule("الثلاثاء", "07:30 - 12:30", "نصف يوم عمل", "أشغال النصف يوم الدوري وتفقد المرافق"),
                WorkDaySchedule("الأربعاء", "07:30 - 12:00", "13:00 - 16:30", "مواصلة مهام $jobTitle في $assignedArea"),
                WorkDaySchedule("الخميس", "07:30 - 12:00", "13:00 - 16:30", "الترتيب الأسبوعي الشامل وتأمين المرفق")
            )
        )
        _supportStaff.value = _supportStaff.value + newStaff
        addAuditLog("ADD_STAFF", "تسجيل عامل مهني", newStaff.id, "تسجيل العامل المهني $fullName ($jobTitle)")
    }

    // Audit Logging
    fun addAuditLog(action: String, actionAr: String, entityId: Int?, details: String) {
        val user = _currentUser.value
        val log = AuditLog(
            id = (_auditLogs.value.maxOfOrNull { it.id } ?: 0) + 1,
            userId = user?.id ?: 0,
            userName = user?.fullName ?: "النظام",
            action = action,
            actionAr = actionAr,
            entity = "النظام الإداري",
            entityId = entityId,
            details = details,
            createdAt = "$todayStr ${timeFormat.format(Date())}"
        )
        _auditLogs.value = listOf(log) + _auditLogs.value
    }
}
