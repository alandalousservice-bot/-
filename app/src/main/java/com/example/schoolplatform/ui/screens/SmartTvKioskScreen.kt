package com.example.schoolplatform.ui.screens

import android.content.pm.ActivityInfo
import androidx.activity.ComponentActivity
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.schoolplatform.data.model.*
import com.example.schoolplatform.data.network.LocalNetworkManager
import com.example.schoolplatform.data.repository.SchoolRepository
import com.example.schoolplatform.ui.animation.bounceClick
import com.example.schoolplatform.ui.components.SchoolBrandSeal
import com.example.schoolplatform.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SmartTvKioskScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    // Attempt landscape lock for Smart TV presentation
    DisposableEffect(Unit) {
        val activity = context as? ComponentActivity
        val originalOrientation = activity?.requestedOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        onDispose {
            activity?.requestedOrientation = originalOrientation
        }
    }

    val announcements by SchoolRepository.announcements.collectAsState()
    val tvConfig by SchoolRepository.tvConfig.collectAsState()
    val classrooms by SchoolRepository.classrooms.collectAsState()
    val students by SchoolRepository.students.collectAsState()
    val dailyMeal by SchoolRepository.dailyMeal.collectAsState()
    val mealConsumptions by SchoolRepository.mealConsumptions.collectAsState()
    val supportStaff by SchoolRepository.supportStaff.collectAsState()
    val localIp by LocalNetworkManager.localIp.collectAsState()
    val networkType by LocalNetworkManager.currentNetworkType.collectAsState()

    val slides = remember {
        TvSlideCategory.values().toList()
    }
    var currentSlideIndex by remember { mutableIntStateOf(0) }
    var isAutoPlay by remember { mutableStateOf(tvConfig.isPlaying) }
    var showTvSettingsDialog by remember { mutableStateOf(false) }

    // Live Clock State (HH:mm:ss & Arabic Date)
    var currentTimeStr by remember { mutableStateOf("") }
    var currentDateStr by remember { mutableStateOf("") }
    var dayNameStr by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val timeFmt = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val dateFmt = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val dayFmt = SimpleDateFormat("EEEE", Locale("ar"))
        while (true) {
            val now = Date()
            currentTimeStr = timeFmt.format(now)
            currentDateStr = dateFmt.format(now)
            dayNameStr = dayFmt.format(now)
            delay(1000L)
        }
    }

    // Auto-carousel timer
    LaunchedEffect(isAutoPlay, currentSlideIndex, tvConfig.autoScrollSeconds) {
        if (isAutoPlay && tvConfig.autoScrollSeconds > 0) {
            delay(tvConfig.autoScrollSeconds * 1000L)
            currentSlideIndex = (currentSlideIndex + 1) % slides.size
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF072118),
                        Color(0xFF0F3A2C),
                        Color(0xFF081C15)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            // Top Bar: School Seal, Ministry Name, Slide Navigation Pills, Clock, Quick Controls
            TvTopHeader(
                schoolName = tvConfig.schoolName,
                provinceMinistry = tvConfig.provinceMinistry,
                currentTime = currentTimeStr,
                currentDate = "$dayNameStr $currentDateStr",
                networkIp = localIp,
                networkType = networkType,
                isAutoPlay = isAutoPlay,
                onTogglePlay = { isAutoPlay = !isAutoPlay },
                onOpenSettings = { showTvSettingsDialog = true },
                onExit = onNavigateBack
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Slide Navigation Indicators (High visibility for TV viewers & remote control)
            TvSlidePillBar(
                slides = slides,
                currentIndex = currentSlideIndex,
                onSelectSlide = { currentSlideIndex = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Central Dynamic Slide Stage with Transition
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AnimatedContent(
                    targetState = slides[currentSlideIndex],
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(400)) + slideInHorizontally(
                            animationSpec = tween(400),
                            initialOffsetX = { it / 2 }
                        )).togetherWith(
                            fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                                animationSpec = tween(300),
                                targetOffsetX = { -it / 2 }
                            )
                        )
                    },
                    label = "tv_slide_transition"
                ) { slide ->
                    when (slide) {
                        TvSlideCategory.WELCOME -> TvWelcomeSlide(
                            schoolName = tvConfig.schoolName,
                            totalStudents = students.size,
                            totalClassrooms = classrooms.size,
                            totalStaff = supportStaff.size,
                            todayMeal = dailyMeal.description
                        )
                        TvSlideCategory.DAILY_SCHEDULE -> TvScheduleSlide(
                            classrooms = classrooms
                        )
                        TvSlideCategory.CANTEEN_MENU -> TvCanteenSlide(
                            dailyMeal = dailyMeal,
                            servedCount = mealConsumptions.size,
                            totalExpected = students.size
                        )
                        TvSlideCategory.STUDENTS_HONOR -> TvHonorRollSlide(
                            students = students
                        )
                        TvSlideCategory.IMPORTANT_ANNOUNCEMENT -> TvAnnouncementsSlide(
                            announcements = announcements.filter { it.active }
                        )
                        TvSlideCategory.SUPPORT_STAFF_TODAY -> TvStaffOnDutySlide(
                            supportStaff = supportStaff
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Ticker Bar (Classic TV News Ticker with school identity)
            TvBottomNewsTicker(
                tickerText = tvConfig.tickerText,
                schoolName = tvConfig.schoolName
            )
        }

        // TV Settings & Remote Control Modal
        if (showTvSettingsDialog) {
            TvSettingsDialog(
                config = tvConfig,
                onDismiss = { showTvSettingsDialog = false },
                onSave = { autoSec, ticker, clock ->
                    SchoolRepository.updateTvConfig(
                        autoScrollSeconds = autoSec,
                        tickerText = ticker,
                        showClock = clock
                    )
                    showTvSettingsDialog = false
                }
            )
        }
    }
}

// ---------------- Top Header Component for TV ----------------

@Composable
private fun TvTopHeader(
    schoolName: String,
    provinceMinistry: String,
    currentTime: String,
    currentDate: String,
    networkIp: String,
    networkType: LanNetworkType,
    isAutoPlay: Boolean,
    onTogglePlay: () -> Unit,
    onOpenSettings: () -> Unit,
    onExit: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(18.dp))
            .padding(horizontal = 18.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Right Section: Brand & Ministry
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            SchoolBrandSeal(size = 52.dp)
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = schoolName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Surface(
                        color = SchoolGold.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, SchoolGold.copy(alpha = 0.6f))
                    ) {
                        Text(
                            text = "شاشة العرض الذكية Smart TV",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SchoolGoldAccent,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "$provinceMinistry · بث مباشر للمؤسسة",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.75f)
                )
            }
        }

        // Center: Local Network Tag
        Surface(
            color = Color.Black.copy(alpha = 0.3f),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(networkType.iconEmoji, fontSize = 14.sp)
                Text(
                    "شبكة داخلية: $networkIp",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Left Section: Clock, Play/Pause, Settings, Exit
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Live Digital Clock
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = currentTime.ifEmpty { "08:00:00" },
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SchoolGoldAccent,
                    letterSpacing = 1.sp
                )
                Text(
                    text = currentDate,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Play / Pause Carousel Button
            FilledTonalIconButton(
                onClick = onTogglePlay,
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = if (isAutoPlay) SchoolGreenLight else Color(0xFF6B7280)
                )
            ) {
                Icon(
                    imageVector = if (isAutoPlay) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isAutoPlay) "إيقاف التمرير التلقائي" else "تشغيل التمرير التلقائي",
                    tint = Color.White
                )
            }

            // Settings Button
            FilledTonalIconButton(
                onClick = onOpenSettings,
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = Color.White.copy(alpha = 0.15f)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "إعدادات شاشة العرض",
                    tint = Color.White
                )
            }

            // Exit Kiosk Mode
            FilledTonalIconButton(
                onClick = onExit,
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = DangerRed.copy(alpha = 0.35f)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "خروج من وضع التلفاز",
                    tint = Color.White
                )
            }
        }
    }
}

// ---------------- Slide Navigation Pills ----------------

@Composable
private fun TvSlidePillBar(
    slides: List<TvSlideCategory>,
    currentIndex: Int,
    onSelectSlide: (Int) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(slides.size) { index ->
            val slide = slides[index]
            val isSelected = index == currentIndex
            Surface(
                color = if (isSelected) SchoolGold else Color.White.copy(alpha = 0.10f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    1.dp,
                    if (isSelected) SchoolGoldAccent else Color.White.copy(alpha = 0.15f)
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onSelectSlide(index) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(slide.iconEmoji, fontSize = 15.sp)
                    Text(
                        text = slide.titleAr,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                        color = if (isSelected) Color(0xFF332003) else Color.White
                    )
                }
            }
        }
    }
}

// ---------------- SLIDE 1: Welcome & School Overview ----------------

@Composable
private fun TvWelcomeSlide(
    schoolName: String,
    totalStudents: Int,
    totalClassrooms: Int,
    totalStaff: Int,
    todayMeal: String
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
            .padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Big Welcome Banner
        Column(
            modifier = Modifier
                .weight(1.3f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Surface(
                    color = SchoolGold.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, SchoolGold)
                ) {
                    Text(
                        "الجمهورية الجزائرية الديمقراطية الشعبية · وزارة التربية الوطنية",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SchoolGoldAccent,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "مرحباً بكم في\n$schoolName",
                    fontSize = 32.sp,
                    lineHeight = 40.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "فضاء تربوي تعليمي متكامل يهدف إلى تنشئة أجيال واعية ومتميزة بالقيم الوطنية والمعرفة الحديثة.",
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }

            // Quick High-Impact Cards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TvStatTile(
                    modifier = Modifier.weight(1f),
                    icon = "🎒",
                    value = "$totalStudents",
                    title = "تلميذ مسجل"
                )
                TvStatTile(
                    modifier = Modifier.weight(1f),
                    icon = "🏫",
                    value = "$totalClassrooms",
                    title = "أفواج تربوية"
                )
                TvStatTile(
                    modifier = Modifier.weight(1f),
                    icon = "👷",
                    value = "$totalStaff",
                    title = "طاقم الدعم والخدمات"
                )
            }
        }

        // Left Highlight Card: Daily Mission & Canteen Teaser
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Canteen highlight card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = Color(0xFF144D3A).copy(alpha = 0.7f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, SchoolGold.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = SchoolGold,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🍲", fontSize = 18.sp)
                            }
                        }
                        Column {
                            Text(
                                "وجبة الغداء اليومية بالمطعم",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                            Text(
                                "تحت مراقبة وإشراف صحي دوري",
                                fontSize = 11.sp,
                                color = SchoolGoldAccent
                            )
                        }
                    }

                    Text(
                        text = todayMeal,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 22.sp
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.AccessTime, contentDescription = null, tint = SchoolGold, modifier = Modifier.size(16.dp))
                        Text(
                            "فترة الإطعام: 12:00 - 13:15",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Institutional value banner
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.7f),
                color = Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("🌟", fontSize = 28.sp)
                    Column {
                        Text(
                            "ميثاق التميز والانضباط",
                            fontWeight = FontWeight.Bold,
                            color = SchoolGoldAccent,
                            fontSize = 13.sp
                        )
                        Text(
                            "الانضباط في الحضور، احترام المعلم، وحماية ممتلكات المدرسة واجب كل تلميذ.",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TvStatTile(
    modifier: Modifier = Modifier,
    icon: String,
    value: String,
    title: String
) {
    Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = 0.07f),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(icon, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                value,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = SchoolGoldAccent
            )
            Text(
                title,
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

// ---------------- SLIDE 2: Daily Schedule & Classes ----------------

@Composable
private fun TvScheduleSlide(
    classrooms: List<Classroom>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("🕒", fontSize = 24.sp)
                Column {
                    Text(
                        "التوقيت المدرسي والحصص التعليمية الرسمية",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "برنامج الحصص والأنشطة للفترة الصباحية والمسائية",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Surface(
                color = SchoolGold.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, SchoolGold)
            ) {
                Text(
                    "نظام دوام واحد (Single Shift)",
                    color = SchoolGoldAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Morning Periods
            TvSchedulePeriodCard(
                modifier = Modifier.weight(1f),
                periodTitle = "الفترة الصباحية",
                timing = "08:00 - 11:15",
                icon = "☀️",
                items = listOf(
                    "08:00 - 08:45" to "الحصة الأولى: اللغة العربية والقراءة",
                    "08:45 - 09:30" to "الحصة الثانية: الرياضيات وحل المشكلات",
                    "09:30 - 09:45" to "استراحة التلاميذ ومراقبة الساحة",
                    "09:45 - 10:30" to "الحصة الثالثة: التربية العلمية والتكنولوجية",
                    "10:30 - 11:15" to "الحصة الرابعة: التربية الإسلامية والتاريخ"
                )
            )

            // Afternoon Periods
            TvSchedulePeriodCard(
                modifier = Modifier.weight(1f),
                periodTitle = "الفترة المسائية",
                timing = "13:00 - 15:15",
                icon = "🌤️",
                items = listOf(
                    "12:00 - 13:00" to "فترة الإطعام بالمطعم المدرسي المركزي",
                    "13:00 - 13:45" to "الحصة الخامسة: الجغرافيا والتربية المدنية",
                    "13:45 - 14:30" to "الحصة السادسة: أنشطة الدعم والخط العربي",
                    "14:30 - 15:15" to "الحصة السابعة: التربية البدنية والرياضية"
                )
            )
        }
    }
}

@Composable
private fun TvSchedulePeriodCard(
    modifier: Modifier = Modifier,
    periodTitle: String,
    timing: String,
    icon: String,
    items: List<Pair<String, String>>
) {
    Surface(
        modifier = modifier.fillMaxHeight(),
        color = Color.White.copy(alpha = 0.08f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(icon, fontSize = 20.sp)
                Text(periodTitle, fontWeight = FontWeight.Bold, color = SchoolGoldAccent, fontSize = 15.sp)
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    color = Color.Black.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        timing,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            HorizontalDivider(color = Color.White.copy(alpha = 0.12f))

            items.forEach { (time, desc) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.04f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        time,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SchoolGold
                    )
                    Text(
                        desc,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// ---------------- SLIDE 3: Canteen & Nutrition ----------------

@Composable
private fun TvCanteenSlide(
    dailyMeal: DailyMeal,
    servedCount: Int,
    totalExpected: Int
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
            .padding(20.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Big Dish Card
        Surface(
            modifier = Modifier
                .weight(1.3f)
                .fillMaxHeight(),
            color = Color(0xFF134533).copy(alpha = 0.85f),
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, SchoolGold.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("🍲", fontSize = 28.sp)
                        Column {
                            Text(
                                "المطعم المدرسي المركزي",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                "وجبة ساخنة ومطابقة لمعايير السلامة والتغذية المدرسية",
                                fontSize = 11.sp,
                                color = SchoolGoldAccent
                            )
                        }
                    }

                    Surface(
                        color = SchoolGold,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "مجاني لجميع التلاميذ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF332003),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Column {
                    Text(
                        "طبق اليوم الرئيسي:",
                        fontSize = 13.sp,
                        color = SchoolGoldAccent,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = dailyMeal.description,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        lineHeight = 32.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "مكونات طبيعية طازجة، خبز كامل، وفاكهة موسمية تضمن نمواً صحياً ونشاطاً دراسياً متميزاً لأبنائنا.",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 20.sp
                    )
                }

                // Hygiene and quality tags
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TvQualityChip(icon = "✅", label = "فحص مخبري دوري للمياه")
                    TvQualityChip(icon = "🧼", label = "تعقيم يومي للأواني")
                    TvQualityChip(icon = "🍎", label = "فيتامينات وسعرات متوازنة")
                }
            }
        }

        // Right Column: Progress & Service Metrics
        Column(
            modifier = Modifier
                .weight(0.9f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "مؤشر توزيع وجبات اليوم",
                        fontWeight = FontWeight.Bold,
                        color = SchoolGoldAccent,
                        fontSize = 14.sp
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "$servedCount / $totalExpected",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            "وجبة تم تقديمها عبر المسح الرقمي",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    }

                    val progress = if (totalExpected > 0) (servedCount.toFloat() / totalExpected) else 0.5f
                    Column {
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            color = SchoolGold,
                            trackColor = Color.White.copy(alpha = 0.2f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("نسبة التغطية", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f))
                            Text("${(progress * 100).toInt()}%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SchoolGoldAccent)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.7f),
                color = Color.White.copy(alpha = 0.06f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("🧑‍🍳", fontSize = 24.sp)
                    Column {
                        Text(
                            "فريق الطهي والإطعام",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                        Text(
                            "إشراف الأستاذة فاطمة بن يحيى مع أعوان النظافة والخدمات المدرسية.",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.75f),
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TvQualityChip(icon: String, label: String) {
    Surface(
        color = Color.White.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(icon, fontSize = 11.sp)
            Text(label, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }
    }
}

// ---------------- SLIDE 4: Honor Roll / Top Students ----------------

@Composable
private fun TvHonorRollSlide(
    students: List<Student>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("🏆", fontSize = 26.sp)
                Column {
                    Text(
                        "لوحة شرف التلاميذ المتفوقين والقدوة المدرسية",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "تشجيع التميز الأكاديمي والانضباط والسلوك الحسن بمدرسة مزيان عمار",
                        fontSize = 12.sp,
                        color = SchoolGoldAccent
                    )
                }
            }

            Surface(
                color = SchoolGold,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "الفصل الدراسي الحالي",
                    color = Color(0xFF332003),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Grid of Honor Students Cards
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val topStudents = listOf(
                Triple("ياسين بلقاسم", "السنة الخامسة أ", "معدل 9.75 · المرتبة الأولى وامتياز"),
                Triple("سارة عمراني", "السنة الخامسة أ", "معدل 9.60 · سلوك مثالي ومطالعة"),
                Triple("نور الهدى بن سالم", "السنة الرابعة أ", "معدل 9.45 · تفوق في الرياضيات واللغات"),
                Triple("أيوب بوعلام", "السنة الرابعة أ", "معدل 9.30 · نشاط رياضي وانضباط تام")
            )

            topStudents.forEachIndexed { index, (name, grade, note) ->
                val medal = when (index) {
                    0 -> "🥇"
                    1 -> "🥈"
                    2 -> "🥉"
                    else -> "⭐"
                }
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    color = Color.White.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        1.dp,
                        if (index == 0) SchoolGold else Color.White.copy(alpha = 0.15f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(medal, fontSize = 32.sp)

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                name,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                color = SchoolGreenDark.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    grade,
                                    color = SchoolGoldAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            note,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )

                        Text(
                            "تهانينا وتمنياتنا بمزيد من التألق!",
                            fontSize = 9.sp,
                            color = SchoolGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ---------------- SLIDE 5: Announcements & Guidelines ----------------

@Composable
private fun TvAnnouncementsSlide(
    announcements: List<SchoolAnnouncement>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("📢", fontSize = 26.sp)
                Column {
                    Text(
                        "إعلانات الإدارة والتعليمات المدرسية المباشرة",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "تحديثات هامة موجهة للأولياء، الطاقم التربوي، والتلاميذ",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Surface(
                color = SchoolGold.copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, SchoolGold)
            ) {
                Text(
                    "تنبيهات فورية ومباشرة",
                    color = SchoolGoldAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            val displayList = announcements.take(3)
            displayList.forEach { ann ->
                val isUrgent = ann.priority == "URGENT" || ann.priority == "HIGH"
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    color = if (isUrgent) Color(0xFF381414).copy(alpha = 0.6f) else Color.White.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        1.dp,
                        if (isUrgent) DangerRed.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.15f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = if (isUrgent) DangerRed else SchoolGold,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        if (isUrgent) "تنبيه هام" else "إشعار عام",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    ann.targetAudience,
                                    fontSize = 10.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                ann.title,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 15.sp,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                ann.content,
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                lineHeight = 18.sp
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Event, contentDescription = null, tint = SchoolGold, modifier = Modifier.size(14.dp))
                            Text(
                                "تاريخ الإصدار: ${ann.date}",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---------------- SLIDE 6: Support Staff on Duty ----------------

@Composable
private fun TvStaffOnDutySlide(
    supportStaff: List<SupportStaff>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("👷", fontSize = 26.sp)
                Column {
                    Text(
                        "طاقم المداومة والخدمات المدرسية الحاضرين اليوم",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "العمال المهنيون المكلفون بالحراسة، النظافة، الطهي والصيانة العامة",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Surface(
                color = SuccessGreen,
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.White))
                    Text(
                        "حضور تام بنسبة 100%",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            supportStaff.take(4).forEach { staff ->
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    color = Color.White.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = Color(0xFF166534),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        "مداوم اليوم",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Text("🟢 حاضر", fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                staff.fullName,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                staff.jobTitle,
                                fontSize = 11.sp,
                                color = SchoolGoldAccent,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "المجال: ${staff.assignedArea}",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                lineHeight = 16.sp
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                            Text(
                                "الفترة: ${staff.shiftType}",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.65f)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---------------- Bottom News Ticker ----------------

@Composable
private fun TvBottomNewsTicker(
    tickerText: String,
    schoolName: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
        color = Color(0xFF061811),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SchoolGold.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Static Gold Badge
            Surface(
                modifier = Modifier.fillMaxHeight(),
                color = SchoolGold,
                shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("🔔", fontSize = 14.sp)
                    Text(
                        "شريط الأخبار",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        color = Color(0xFF332003)
                    )
                }
            }

            // Marquee or Ticker Content
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tickerText,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ---------------- TV Settings Dialog ----------------

@Composable
private fun TvSettingsDialog(
    config: TvDisplayConfig,
    onDismiss: () -> Unit,
    onSave: (autoSec: Int, ticker: String, showClock: Boolean) -> Unit
) {
    var autoScrollSec by remember { mutableIntStateOf(config.autoScrollSeconds) }
    var ticker by remember { mutableStateOf(config.tickerText) }
    var showClock by remember { mutableStateOf(config.showClock) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("⚙️", fontSize = 22.sp)
                Text("إعدادات شاشة التلفاز الذكية Smart TV", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    "تعديل مدة التمرير الآلي وشريط الأخبار المعروض على شاشة الاستقبال:",
                    fontSize = 12.sp,
                    color = TextSecondary
                )

                // Scroll interval
                Column {
                    Text(
                        "مدة بقاء كل شريحة: $autoScrollSec ثانية",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = SchoolGreenDark
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(8, 12, 18, 25).forEach { sec ->
                            FilterChip(
                                selected = autoScrollSec == sec,
                                onClick = { autoScrollSec = sec },
                                label = { Text("$sec ثوانٍ") }
                            )
                        }
                    }
                }

                // Ticker Input
                OutlinedTextField(
                    value = ticker,
                    onValueChange = { ticker = it },
                    label = { Text("نص شريط الأخبار المتحرك") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(autoScrollSec, ticker, showClock) },
                colors = ButtonDefaults.buttonColors(containerColor = SchoolGreen)
            ) {
                Text("حفظ التعديلات", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
