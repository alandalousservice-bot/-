package com.example.schoolplatform

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.schoolplatform.data.model.Role
import com.example.schoolplatform.data.repository.SchoolRepository
import com.example.schoolplatform.ui.screens.*
import com.example.schoolplatform.ui.theme.PaperBackground
import com.example.schoolplatform.ui.theme.SchoolPlatformTheme
import com.example.schoolplatform.ui.theme.ThemeManager
import com.example.schoolplatform.ui.theme.ThemeMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by ThemeManager.themeMode.collectAsState()
            val isSystemDark = isSystemInDarkTheme()
            val isDark = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemDark
            }

            SchoolPlatformTheme(darkTheme = isDark) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = PaperBackground
                    ) {
                        SchoolPlatformApp()
                    }
                }
            }
        }
    }
}

@Composable
fun SchoolPlatformApp() {
    val navController = rememberNavController()
    val currentUser by SchoolRepository.currentUser.collectAsState()

    // Determine initial destination: landing page
    val startDestination = "landing"

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { fadeIn(animationSpec = tween(250)) },
        exitTransition = { fadeOut(animationSpec = tween(200)) }
    ) {
        composable("landing") {
            LandingScreen(
                onNavigateLogin = { navController.navigate("login") },
                onEnterAsRole = { role ->
                    when (role) {
                        Role.ADMIN, Role.SUPER_ADMIN -> navController.navigate("admin")
                        Role.TEACHER -> navController.navigate("teacher")
                        Role.RESTAURANT_MANAGER -> navController.navigate("restaurant")
                    }
                }
            )
        }

        composable("login") {
            LoginScreen(
                onNavigateBack = { navController.popBackStack() },
                onLoginSuccess = { role ->
                    when (role) {
                        Role.ADMIN, Role.SUPER_ADMIN -> navController.navigate("admin") {
                            popUpTo("landing")
                        }
                        Role.TEACHER -> navController.navigate("teacher") {
                            popUpTo("landing")
                        }
                        Role.RESTAURANT_MANAGER -> navController.navigate("restaurant") {
                            popUpTo("landing")
                        }
                    }
                }
            )
        }

        composable("admin") {
            AdminDashboardScreen(
                onNavigateLanding = {
                    navController.navigate("landing") {
                        popUpTo("landing") { inclusive = true }
                    }
                },
                onNavigateLogin = {
                    navController.navigate("login")
                },
                onNavigateNetworkSync = {
                    navController.navigate("network_sync")
                }
            )
        }

        composable("teacher") {
            TeacherScreen(
                onNavigateLanding = {
                    navController.navigate("landing") {
                        popUpTo("landing") { inclusive = true }
                    }
                },
                onNavigateLogin = {
                    navController.navigate("login")
                },
                onNavigateNetworkSync = {
                    navController.navigate("network_sync")
                }
            )
        }

        composable("restaurant") {
            RestaurantScreen(
                onNavigateLanding = {
                    navController.navigate("landing") {
                        popUpTo("landing") { inclusive = true }
                    }
                },
                onNavigateLogin = {
                    navController.navigate("login")
                },
                onNavigateNetworkSync = {
                    navController.navigate("network_sync")
                }
            )
        }

        composable("network_sync") {
            LocalNetworkSyncScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
