package com.example.schoolplatform.data.model

enum class Role(val titleAr: String) {
    SUPER_ADMIN("مدير عام"),
    ADMIN("مدير المؤسسة"),
    TEACHER("أستاذ"),
    RESTAURANT_MANAGER("مشرف المطعم")
}

enum class MealPeriod(val titleAr: String) {
    BREAKFAST("الفطور"),
    LUNCH("الغداء")
}

enum class AbsencePeriod(val titleAr: String) {
    MORNING("الحصة الصباحية"),
    AFTERNOON("الحصة المسائية")
}

enum class BudgetCategory(val titleAr: String) {
    OPERATING("التسيير اليومي والنظافة"),
    CANTEEN("المطعم المدرسي والتموين"),
    EQUIPMENT("الوسائل والتجهيزات"),
    MAINTENANCE("الصيانة والإصلاح"),
    ACTIVITIES("الأنشطة التربوية"),
    EMERGENCY("الطوارئ والاحتياط")
}

enum class GradeStatus(val titleAr: String) {
    DRAFT("مسودة"),
    SUBMITTED("بانتظار الاعتماد"),
    APPROVED("معتمد")
}

data class User(
    val id: Int,
    val username: String,
    val passwordHash: String,
    val role: Role,
    val fullName: String,
    val isActive: Boolean = true,
    val isOnline: Boolean = true,
    val lastLoginAt: String? = null
)

data class Classroom(
    val id: Int,
    val name: String,
    val level: String = "الطور الابتدائي"
)

data class Student(
    val id: Int,
    val barcode: String,
    val fullName: String,
    val classroomId: Int,
    val parentPhone: String? = "0550123456"
)

data class StudentProfile(
    val studentId: Int,
    val birthDate: String = "2015-04-12",
    val nationalId: String = "1098234120938",
    val parentName: String = "ولي التلميذ",
    val parentPhone: String = "0550123456",
    val address: String = "سطيف - وسط المدينة",
    val healthNotes: String = "حالة صحية جيدة، لا توجد حساسية غذائية",
    val photoUrl: String? = null
)

data class TeacherProfile(
    val userId: Int,
    val specialty: String = "أستاذ التعليم الابتدائي (لغة عربية)",
    val phone: String = "0661987654",
    val email: String = "teacher1@alandalous.dz",
    val hireDate: String = "2020-09-01",
    val address: String = "سطيف",
    val serviceType: String = "FULL_TIME",
    val weeklyTargetMinutes: Int = 1440 // 24 hours
)

data class Absence(
    val id: Int,
    val studentId: Int,
    val studentName: String,
    val classroomName: String,
    val date: String,
    val period: AbsencePeriod
)

data class GradeEntry(
    val id: Int,
    val studentId: Int,
    val studentName: String,
    val classroomName: String,
    val teacherId: Int,
    val term: String,
    val subject: String,
    val continuous: Double? = null,
    val exam: Double? = null,
    val observation: String? = null,
    val status: GradeStatus = GradeStatus.DRAFT
)

data class DailyMeal(
    val id: Int,
    val date: String,
    val period: MealPeriod,
    val description: String,
    val price: Double? = 0.0
)

data class MealConsumption(
    val id: Int,
    val studentId: Int,
    val studentName: String,
    val mealId: Int,
    val scannedAt: String,
    val scannedBy: String
)

data class InventoryItem(
    val id: Int,
    val name: String,
    val quantity: Double,
    val unit: String,
    val lowStockThreshold: Double = 10.0,
    val imageUrl: String? = null
) {
    val isLow: Boolean get() = quantity <= lowStockThreshold
}

data class BudgetLine(
    val id: Int,
    val schoolYear: String = "2026/2027",
    val category: BudgetCategory,
    val title: String,
    val allocatedAmount: Double,
    val spentAmount: Double = 0.0,
    val status: String = "ACTIVE",
    val notes: String? = null
) {
    val remainingAmount: Double get() = allocatedAmount - spentAmount
    val utilizationPercent: Int
        get() = if (allocatedAmount > 0) ((spentAmount / allocatedAmount) * 100).toInt().coerceIn(0, 100) else 0
}

data class BudgetTransaction(
    val id: Int,
    val lineId: Int,
    val lineTitle: String,
    val amount: Double,
    val description: String,
    val kind: String = "EXPENSE",
    val createdByName: String,
    val createdAt: String
)

data class Schedule(
    val id: Int,
    val teacherId: Int,
    val teacherName: String,
    val classroomId: Int,
    val classroomName: String,
    val dayOfWeek: Int, // 0 = الأحد, 1 = الإثنين, 2 = الثلاثاء, 3 = الأربعاء, 4 = الخميس
    val period: AbsencePeriod,
    val subject: String = "اللغة العربية",
    val status: String = "APPROVED"
)

data class SchoolScheduleConfig(
    val schoolYear: String = "2026/2027",
    val mode: String = "SINGLE", // SINGLE or DOUBLE
    val morningStart: String = "08:00",
    val morningEnd: String = "12:00",
    val afternoonStart: String = "13:30",
    val afternoonEnd: String = "16:30",
    val lessonMinutes: Int = 45,
    val breakMinutes: Int = 15,
    val workingDays: Int = 5
)

data class Facility(
    val id: Int,
    val name: String,
    val kind: String = "CLASSROOM",
    val capacity: Int? = 35,
    val isAvailable: Boolean = true
)

data class SubjectWeeklyQuota(
    val id: Int,
    val schoolYear: String = "2026/2027",
    val level: String,
    val subject: String,
    val lessonsPerWeek: Double,
    val minutesPerLesson: Int = 45,
    val required: Boolean = true
)

data class AuditLog(
    val id: Int,
    val userId: Int,
    val userName: String,
    val action: String,
    val actionAr: String,
    val entity: String,
    val entityId: Int? = null,
    val details: String = "",
    val createdAt: String
)

sealed class MealScanResult(val message: String, val studentName: String?) {
    data class Success(val name: String) : MealScanResult("تم تأكيد تقديم الوجبة بنجاح", name)
    data class AlreadyTaken(val name: String) : MealScanResult("أُخذت الوجبة مسبقاً لهذه الفترة", name)
    data class AbsentWarning(val name: String) : MealScanResult("تنبيه: التلميذ مسجل غائباً في الحصة الصباحية", name)
    object NotFound : MealScanResult("رمز البطاقة غير مسجل في قاعدة التلاميذ", null)
}
