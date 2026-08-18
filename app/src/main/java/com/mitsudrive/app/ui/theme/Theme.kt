package com.mitsudrive.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val NeonBlue = Color(0xFF00D2FF)
val AccentRed = Color(0xFFE94560)
val DarkBackground = Color(0xFF060912)
val CardBackground = Color(0xFF0D111F)
val BorderColor = Color(0xFF1A2240)
val TextPrimary = Color(0xFFE0E6F0)
val TextSecondary = Color(0xFF6B7394)

private val MitsuDriveColorScheme = darkColorScheme(
    primary = NeonBlue,
    onPrimary = DarkBackground,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = CardBackground,
    onSurface = TextPrimary,
    outline = BorderColor,
    error = AccentRed
)

@Composable
fun MitsuDriveTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MitsuDriveColorScheme,
        content = content
    )
}
