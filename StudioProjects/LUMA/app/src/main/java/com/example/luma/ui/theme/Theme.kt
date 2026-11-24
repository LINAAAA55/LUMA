package com.example.luma.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LumaColorScheme = darkColorScheme(
    primary = LumaSoftLavender,
    onPrimary = LumaMidnightNavy,
    primaryContainer = Color(0xFF382F5E),
    onPrimaryContainer = Color(0xFFEADDFF),
    secondary = LumaLightBlue,
    onSecondary = LumaMidnightNavy,
    secondaryContainer = Color(0xFF1E3A4C),
    onSecondaryContainer = Color(0xFFD3E4FD),
    background = LumaMidnightNavy,
    onBackground = LumaWhite,
    surface = LumaDeepBlue,
    onSurface = LumaWhite,
    surfaceVariant = Color(0xFF252D4A),
    onSurfaceVariant = LumaSecondaryText,
    outline = LumaCardBorder,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

@Composable
fun LUMATheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LumaColorScheme,
        typography = Typography,
        content = content
    )
}
