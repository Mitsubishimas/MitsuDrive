package com.mitsudrive.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ==================== ЦВЕТА ====================

// Основные цвета MitsuDrive
val NeonBlue = Color(0xFF00D2FF)
val NeonBlueDark = Color(0xFF00A8CC)
val NeonBlueLight = Color(0xFF80E5FF)
val AccentRed = Color(0xFFE94560)
val DarkBackground = Color(0xFF060912)
val CardBackground = Color(0xFF0D111F)
val BorderColor = Color(0xFF1A2240)
val TextPrimary = Color(0xFFE0E6F0)
val TextSecondary = Color(0xFF6B7394)
val TextTertiary = Color(0xFF8892b0)

// Дополнительные цвета
val SuccessGreen = Color(0xFF2ECC71)
val WarningOrange = Color(0xFFF39C12)
val PurpleAccent = Color(0xFF9b59b6)
val ErrorRed = Color(0xFFFF4757)
val OnlineGreen = Color(0xFF2ED573)
val OfflineGray = Color(0xFF6B7394)

// ==================== ЦВЕТОВЫЕ СХЕМЫ ====================

private val DarkColorScheme = darkColorScheme(
    primary = NeonBlue,
    onPrimary = DarkBackground,
    primaryContainer = NeonBlue.copy(alpha = 0.15f),
    onPrimaryContainer = NeonBlue,
    
    secondary = AccentRed,
    onSecondary = TextPrimary,
    secondaryContainer = AccentRed.copy(alpha = 0.1f),
    onSecondaryContainer = AccentRed,
    
    background = DarkBackground,
    onBackground = TextPrimary,
    
    surface = CardBackground,
    onSurface = TextPrimary,
    
    surfaceVariant = CardBackground,
    onSurfaceVariant = TextSecondary,
    
    outline = BorderColor,
    outlineVariant = BorderColor.copy(alpha = 0.5f),
    
    error = ErrorRed,
    onError = TextPrimary,
    
    errorContainer = ErrorRed.copy(alpha = 0.15f),
    onErrorContainer = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = NeonBlueDark,
    onPrimary = Color.White,
    primaryContainer = NeonBlue.copy(alpha = 0.15f),
    onPrimaryContainer = NeonBlueDark,
    
    secondary = AccentRed,
    onSecondary = Color.White,
    secondaryContainer = AccentRed.copy(alpha = 0.1f),
    onSecondaryContainer = AccentRed,
    
    background = Color(0xFFF5F7FA),
    onBackground = Color(0xFF1A1A2E),
    
    surface = Color.White,
    onSurface = Color(0xFF1A1A2E),
    
    surfaceVariant = Color(0xFFF0F2F5),
    onSurfaceVariant = Color(0xFF4A5568),
    
    outline = Color(0xFFCBD5E0),
    outlineVariant = Color(0xFFE2E8F0),
    
    error = ErrorRed,
    onError = Color.White,
    
    errorContainer = ErrorRed.copy(alpha = 0.1f),
    onErrorContainer = ErrorRed
)

// ==================== ТЕМА ====================

@Composable
fun MitsuDriveTheme(
    darkTheme: Boolean = true, // По умолчанию тёмная тема
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = MitsuDriveTypography,
        shapes = MitsuDriveShapes,
        content = content
    )
}
