package com.mitsudrive.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Устанавливаем Splash Screen
        val splashScreen = installSplashScreen()
        
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF060912)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "MitsuDrive",
                fontSize = 48.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF00D2FF)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Сообщество автомобилистов",
                fontSize = 16.sp,
                color = Color(0xFF6B7394)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Модульная архитектура готова",
                fontSize = 14.sp,
                color = Color(0xFFE0E6F0)
            )
        }
    }
}
