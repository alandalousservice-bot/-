package com.example.schoolplatform.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import com.example.schoolplatform.data.model.SchoolAnnouncement
import com.example.schoolplatform.data.model.TvDisplayConfig
import com.example.schoolplatform.data.repository.SchoolRepository
import com.example.schoolplatform.ui.animation.bounceClick
import com.example.schoolplatform.ui.theme.*

@Composable
fun AdminTvControlTab(
    onLaunchTvScreen: () -> Unit
) {
    val tvConfig by SchoolRepository.tvConfig.collectAsState()
    val announcements by SchoolRepository.announcements.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // TV Launch & Status Hero Card
        item {
            Surface(
                color = Color(0xFF0C3829),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, SchoolGold),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
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
                            Surface(
                                shape = CircleShape,
                                color = SchoolGold,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("📺", fontSize = 22.sp)
                                }
                            }
                            Column {
                                Text(
                                    "شاشة العرض الذكية للتلفاز (Smart TV)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                                Text(
                                    "واجهة مخصصة لشاشات البهو، قاعة الأساتذة، والاستقبال",
                                    fontSize = 12.sp,
                                    color = SchoolGoldAccent
                                )
                            }
                        }

                        Button(
                            onClick = onLaunchTvScreen,
                            colors = ButtonDefaults.buttonColors(containerColor = SchoolGold),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.bounceClick()
                        ) {
                            Icon(Icons.Default.Tv, contentDescription = null, tint = Color(0xFF332003))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "تشغيل وضع التلفاز",
                                color = Color(0xFF332003),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TvParamPill(
                            label = "التمرير الآلي",
                            value = if (tvConfig.isPlaying) "${tvConfig.autoScrollSeconds} ثوانٍ" else "متوقف مؤقتاً",
                            icon = "⏱️"
                        )
                        TvParamPill(
                            label = "الشرائح المضمنة",
                            value = "6 شرائح دورية",
                            icon = "📑"
                        )
                        TvParamPill(
                            label = "الإعلانات الفعالة",
                            value = "${announcements.count { it.active }} إعلانات",
                            icon = "📢"
                        )
                    }
                }
            }
        }

        // Ticker & Carousel Tuning Section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, BorderLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "التحكم في شريط الأخبار وسرعة العرض",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = SchoolGreenDark
                    )

                    var tickerText by remember(tvConfig.tickerText) { mutableStateOf(tvConfig.tickerText) }
                    OutlinedTextField(
                        value = tickerText,
                        onValueChange = {
                            tickerText = it
                            SchoolRepository.updateTvConfig(tickerText = it)
                        },
                        label = { Text("نص شريط الأخبار السفلي للشاشة") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "مدة كل شريحة:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(8, 12, 18, 25).forEach { sec ->
                                FilterChip(
                                    selected = tvConfig.autoScrollSeconds == sec,
                                    onClick = { SchoolRepository.updateTvConfig(autoScrollSeconds = sec) },
                                    label = { Text("$sec ثوانٍ") }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Announcements Management Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "إعلانات وتنبيهات شاشة المؤسسة",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = SchoolGreenDark
                    )
                    Text(
                        "تظهر تلقائياً ضمن شريحة الإعلانات الرسمية على شاشة التلفاز",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = SchoolGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.bounceClick()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إضافة إعلان", fontSize = 12.sp, color = Color.White)
                }
            }
        }

        // List of Announcements
        items(announcements, key = { it.id }) { ann ->
            val isUrgent = ann.priority == "URGENT" || ann.priority == "HIGH"
            Card(
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    1.dp,
                    if (isUrgent) DangerRed.copy(alpha = 0.5f) else BorderLight
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                color = if (isUrgent) DangerRed else SchoolGold,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    if (isUrgent) "عاجل" else "عادي",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                ann.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            ann.content,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "الفئة المستهدفة: ${ann.targetAudience} · التاريخ: ${ann.date}",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Switch(
                            checked = ann.active,
                            onCheckedChange = { SchoolRepository.toggleAnnouncementActive(ann.id) }
                        )
                        IconButton(
                            onClick = { SchoolRepository.deleteAnnouncement(ann.id) }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "حذف الإعلان", tint = DangerRed)
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddAnnouncementDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, content, audience, priority ->
                SchoolRepository.addAnnouncement(title, content, audience, priority)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun TvParamPill(
    label: String,
    value: String,
    icon: String
) {
    Surface(
        color = Color.White.copy(alpha = 0.08f),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(icon, fontSize = 14.sp)
            Column {
                Text(label, fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
                Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SchoolGoldAccent)
            }
        }
    }
}

@Composable
fun AddAnnouncementDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, content: String, audience: String, priority: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var audience by remember { mutableStateOf("الجميع") }
    var priority by remember { mutableStateOf("NORMAL") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("إضافة إعلان جديد لشاشة التلفاز", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("عنوان الإعلان") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("نص الإعلان والتفاصيل") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                OutlinedTextField(
                    value = audience,
                    onValueChange = { audience = it },
                    label = { Text("الفئة المستهدفة (الأولياء، التلاميذ، الجميع)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("NORMAL" to "عادي", "HIGH" to "هام", "URGENT" to "عاجل").forEach { (key, label) ->
                        FilterChip(
                            selected = priority == key,
                            onClick = { priority = key },
                            label = { Text(label) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onConfirm(title.trim(), content.trim(), audience.trim(), priority)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SchoolGreen)
            ) {
                Text("نشر على الشاشة")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}
