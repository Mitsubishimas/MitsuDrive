package com.mitsudrive.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mitsudrive.app.navigation.DriveNavigation
import com.mitsudrive.app.navigation.Screen
import com.mitsudrive.app.ui.theme.MitsuDriveTheme
import com.mitsudrive.core.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        
        setContent {
            MitsuDriveTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    
    Scaffold(
        containerColor = DarkBackground,
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            DriveNavigation(navController = navController)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    NavigationBar(
        containerColor = CardBackground,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = currentRoute == Screen.Feed.route,
            onClick = {
                navController.navigate(Screen.Feed.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Text("📰", fontSize = 20.sp) },
            label = { Text("Лента", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NeonBlue,
                selectedTextColor = NeonBlue,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = NeonBlue.copy(alpha = 0.1f)
            )
        )
        
        NavigationBarItem(
            selected = currentRoute == Screen.Map.route,
            onClick = {
                navController.navigate(Screen.Map.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Text("🗺️", fontSize = 20.sp) },
            label = { Text("Карта", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NeonBlue,
                selectedTextColor = NeonBlue,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = NeonBlue.copy(alpha = 0.1f)
            )
        )
        
        NavigationBarItem(
            selected = currentRoute == Screen.Chats.route,
            onClick = {
                navController.navigate(Screen.Chats.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Text("💬", fontSize = 20.sp) },
            label = { Text("Чаты", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NeonBlue,
                selectedTextColor = NeonBlue,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = NeonBlue.copy(alpha = 0.1f)
            )
        )
        
        NavigationBarItem(
            selected = currentRoute == Screen.Garage.route,
            onClick = {
                navController.navigate(Screen.Garage.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Text("🚗", fontSize = 20.sp) },
            label = { Text("Гараж", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = NeonBlue,
                selectedTextColor = NeonBlue,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = NeonBlue.copy(alpha = 0.1f)
            )
        )
        
        NavigationBarItem(
            selected = currentRoute == Screen.Sos.route,
            onClick = {
                navController.navigate(Screen.Sos.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            icon = { Text("🆘", fontSize = 20.sp) },
            label = { Text("SOS", fontSize = 10.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AccentRed,
                selectedTextColor = AccentRed,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary,
                indicatorColor = AccentRed.copy(alpha = 0.1f)
            )
        )
    }
}
