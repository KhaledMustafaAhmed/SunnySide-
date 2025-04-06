package com.example.sunny.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF2C2C2C),
    secondary = Color(0xFF333333),
    tertiary = Color(0xFF272727),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color(0xFFF9F9F7),
    onSecondary = Color(0xFFF4F3EF),
    onTertiary = Color(0xFFEFEEE8),
    onBackground = Color(0xFFF9F9F7),
    onSurface = Color(0xFFF9F9F7),
    surfaceVariant = Color(0xFF252525)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFB8D3D4),
    secondary = Color(0xFF9FAAC3),
    tertiary = Color(0xFF817FA0),
    background = Color(0xFFE3E8E4),
    surface = Color(0xFFF5F7F5),
    onPrimary = Color(0xFF000000),
    onSecondary = Color(0xFF000000),
    onTertiary = Color(0xFF000000),
    onBackground = Color(0xFF333333),
    onSurface = Color(0xFF333333),
    surfaceVariant = Color(0xFFD1D9D6),
    outline = Color(0xFF667865)
)

@Composable
fun SunnyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}