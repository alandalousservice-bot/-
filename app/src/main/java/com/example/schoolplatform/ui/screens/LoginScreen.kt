package com.example.schoolplatform.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.schoolplatform.data.model.Role
import com.example.schoolplatform.data.repository.SchoolRepository
import com.example.schoolplatform.ui.components.SchoolBrandSeal
import com.example.schoolplatform.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateBack: () -> Unit,
    onLoginSuccess: (Role) -> Unit
) {
    var username by remember { mutableStateOf("admin") }
    var password by remember { mutableStateOf("admin123") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = PaperBackground,
        topBar = {
            TopAppBar(
                title = { Text("تسجيل الدخول للمنصة", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "رجوع")
                    }
                },
                actions = {
                    val isDark = LocalDarkTheme.current
                    IconButton(
                        onClick = { ThemeManager.toggleDarkMode() }
                    ) {
                        Icon(
                            imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (isDark) "التبديل إلى الوضع النهاري" else "التبديل إلى الوضع الليلي",
                            tint = if (isDark) SchoolGold else SchoolGreenDark
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PaperBackground
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            SchoolBrandSeal(size = 72.dp)

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                "مدرسة مزيان عمار الابتدائية",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = SchoolGreenDark
            )

            Text(
                "المنصة الإدارية والتربوية الموحدة",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Form Card
            Surface(
                color = CardSurface,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (errorMessage != null) {
                        Surface(
                            color = DangerBg,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 14.dp)
                        ) {
                            Text(
                                text = errorMessage ?: "",
                                color = DangerRed,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(10.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    OutlinedTextField(
                        value = username,
                        onValueChange = {
                            username = it
                            errorMessage = null
                        },
                        label = { Text("اسم المستخدم") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = SchoolGreen) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null
                        },
                        label = { Text("كلمة المرور") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = SchoolGreen) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            val user = SchoolRepository.login(username.trim(), password.trim())
                            if (user != null) {
                                onLoginSuccess(user.role)
                            } else {
                                errorMessage = "بيانات الدخول غير صحيحة، يرجى التأكد من اسم المستخدم وكلمة المرور"
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SchoolGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("دخول الحساب", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Quick demo buttons
            Text(
                "أو تسجيل الدخول السريع كـ:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickLoginChip(
                    modifier = Modifier.weight(1f),
                    title = "المدير",
                    onSelect = {
                        username = "admin"
                        password = "admin123"
                        val user = SchoolRepository.login("admin", "admin123")
                        if (user != null) onLoginSuccess(user.role)
                    }
                )
                QuickLoginChip(
                    modifier = Modifier.weight(1f),
                    title = "الأستاذ",
                    onSelect = {
                        username = "teacher1"
                        password = "teacher123"
                        val user = SchoolRepository.login("teacher1", "teacher123")
                        if (user != null) onLoginSuccess(user.role)
                    }
                )
                QuickLoginChip(
                    modifier = Modifier.weight(1f),
                    title = "المطعم",
                    onSelect = {
                        username = "restaurant1"
                        password = "resto123"
                        val user = SchoolRepository.login("restaurant1", "resto123")
                        if (user != null) onLoginSuccess(user.role)
                    }
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun QuickLoginChip(
    modifier: Modifier = Modifier,
    title: String,
    onSelect: () -> Unit
) {
    OutlinedButton(
        onClick = onSelect,
        modifier = modifier.height(38.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = SchoolGreenDark
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
    ) {
        Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}
