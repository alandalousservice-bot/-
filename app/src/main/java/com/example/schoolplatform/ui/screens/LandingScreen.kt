package com.example.schoolplatform.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.schoolplatform.R
import com.example.schoolplatform.data.model.Role
import com.example.schoolplatform.data.repository.SchoolRepository
import com.example.schoolplatform.ui.animation.bounceClick
import com.example.schoolplatform.ui.components.SchoolBrandSeal
import com.example.schoolplatform.ui.theme.*

@Composable
fun LandingScreen(
    onNavigateLogin: () -> Unit,
    onEnterAsRole: (Role) -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = PaperBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            // Header bar
            Surface(
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SchoolBrandSeal(size = 40.dp)
                        Column {
                            Text(
                                "مدرسة مزيان عمار الابتدائية",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = SchoolGreenDark
                            )
                            Text(
                                "ولاية سطيف · منصة الإدارة المدرسية",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Button(
                        onClick = onNavigateLogin,
                        modifier = Modifier.bounceClick(),
                        colors = ButtonDefaults.buttonColors(containerColor = SchoolGreen),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text("دخول المنصة ←", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            // Hero Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Eyebrow
                Surface(
                    color = SchoolGoldLight,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(SchoolGold)
                        )
                        Text(
                            "منصة رقمية لإدارة المدرسة الابتدائية",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF785408)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "إدارة أوضح.\nمدرسة أقوى.",
                    fontSize = 32.sp,
                    lineHeight = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SchoolGreenDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "منصة متكاملة تساعد فريق المؤسسة على تنظيم اليوم الدراسي، تتبع حضور التلاميذ، رقمنة مطعم المدرسة، وإدارة الميزانية بكل شفافية وموثوقية.",
                    fontSize = 13.sp,
                    lineHeight = 22.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Hero Image Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .border(2.dp, BorderLight, RoundedCornerShape(18.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.school_hero),
                        contentDescription = "صورة المدرسة",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color(0xAA0B4232))
                                )
                            )
                    )
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.School, contentDescription = null, tint = SchoolGoldAccent)
                        Text(
                            "يوم دراسي منظم في متناول الجميع",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onNavigateLogin,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .bounceClick(),
                        colors = ButtonDefaults.buttonColors(containerColor = SchoolGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("تسجيل الدخول", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                // Trust items
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TrustChip(text = "صلاحيات حسب الدور")
                    Text("•", color = TextMuted)
                    TrustChip(text = "سجل تدقيق شامل")
                    Text("•", color = TextMuted)
                    TrustChip(text = "عمل محلي سريع")
                }
            }

            // Stats Section
            Surface(
                color = SurfaceTint,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatBox(number = "03", label = "فضاءات عمل")
                    StatBox(number = "01", label = "سجل موحد")
                    StatBox(number = "24/7", label = "جاهزية محلية")
                    StatBox(number = "100%", label = "عربية RTL")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Role Access Cards
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    "مساحات العمل حسب الدور",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = SchoolGreenDark,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                RoleQuickCard(
                    role = Role.ADMIN,
                    emoji = "👩‍💼",
                    title = "مدير المؤسسة",
                    desc = "لوحة قيادة شاملة، الحضور، اعتماد النقاط، والميزانية",
                    onSelect = {
                        val user = SchoolRepository.users.value.first { it.role == Role.ADMIN }
                        SchoolRepository.switchUser(user)
                        onEnterAsRole(Role.ADMIN)
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                RoleQuickCard(
                    role = Role.TEACHER,
                    emoji = "👨‍🏫",
                    title = "الأستاذ (أحمد)",
                    desc = "الجدول الأسبوعي، تسجيل الغياب اليومي، ودفتر النقاط",
                    onSelect = {
                        val user = SchoolRepository.users.value.first { it.role == Role.TEACHER }
                        SchoolRepository.switchUser(user)
                        onEnterAsRole(Role.TEACHER)
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                RoleQuickCard(
                    role = Role.RESTAURANT_MANAGER,
                    emoji = "🍽️",
                    title = "مشرف المطعم",
                    desc = "مسح باركود وجبات الغداء ومتابعة مخزون المواد الغذائية",
                    onSelect = {
                        val user = SchoolRepository.users.value.first { it.role == Role.RESTAURANT_MANAGER }
                        SchoolRepository.switchUser(user)
                        onEnterAsRole(Role.RESTAURANT_MANAGER)
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3 Core Features
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    "المزايا الرئيسية للمنصة",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = SchoolGreenDark,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                FeatureCard(
                    icon = Icons.Default.Dashboard,
                    number = "01",
                    title = "لوحة قيادة واحدة",
                    desc = "صورة يومية واضحة ومحدثة عن حضور التلاميذ، وجبات المطعم، والمخزون."
                )
                Spacer(modifier = Modifier.height(10.dp))
                FeatureCard(
                    icon = Icons.Default.Groups,
                    number = "02",
                    title = "سجل مدرسي موحد",
                    desc = "ملفات التلاميذ والأقسام ودفاتر النقاط في مسار منظم وسهل المراجعة والاعتماد."
                )
                Spacer(modifier = Modifier.height(10.dp))
                FeatureCard(
                    icon = Icons.Default.AccountBalance,
                    number = "03",
                    title = "ميزانية تحت السيطرة",
                    desc = "اعتمادات مالية مقسمة حسب المحاور، نفقات موثقة، ورصيد متبقٍ يظهر قبل أي قرار صرف."
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Footer
            Surface(
                color = SchoolGreenDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SchoolBrandSeal(size = 46.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "مدرسة مزيان عمار الابتدائية",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Text(
                        "الجمهورية الجزائرية الديمقراطية الشعبية · وزارة التربية الوطنية",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "السنة الدراسية 2026/2027",
                        color = SchoolGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TrustChip(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("✓", color = SchoolGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Text(text, fontSize = 10.sp, color = TextSecondary)
    }
}

@Composable
private fun StatBox(number: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(number, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = SchoolGreen)
        Text(label, fontSize = 10.sp, color = TextSecondary)
    }
}

@Composable
private fun RoleQuickCard(
    role: Role,
    emoji: String,
    title: String,
    desc: String,
    onSelect: () -> Unit
) {
    Surface(
        color = CardSurface,
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, BorderLight),
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick(scaleDown = 0.96f) { onSelect() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                color = SurfaceTint,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(emoji, fontSize = 22.sp)
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                Spacer(modifier = Modifier.height(2.dp))
                Text(desc, fontSize = 11.sp, color = TextSecondary, lineHeight = 16.sp)
            }

            Icon(
                Icons.Default.ArrowForward,
                contentDescription = null,
                tint = SchoolGreen,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun FeatureCard(
    icon: ImageVector,
    number: String,
    title: String,
    desc: String
) {
    Surface(
        color = CardSurface,
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 1.dp,
        border = BorderStroke(1.dp, BorderLight),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = SurfaceTint,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = SchoolGreen, modifier = Modifier.size(22.dp))
                    }
                }
                Text(number, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextMuted)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = SchoolGreenDark)
            Spacer(modifier = Modifier.height(4.dp))
            Text(desc, fontSize = 12.sp, color = TextSecondary, lineHeight = 18.sp)
        }
    }
}
