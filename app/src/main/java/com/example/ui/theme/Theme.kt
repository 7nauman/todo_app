package com.example.ui.theme

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
    primary = NeonWarmGold,
    secondary = OrchidOrchid,
    tertiary = AccentTeal,
    background = DeepSlateIndigo,
    surface = DeepSurface,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = ElevatedSurface,
    onSurfaceVariant = TextSecondaryDark,
    primaryContainer = ElevatedSurface,
    onPrimaryContainer = NeonWarmGold,
    error = PriorityHigh
)

private val LightColorScheme = lightColorScheme(
    primary = StandardPrimaryLight,
    secondary = StandardSecondaryLight,
    tertiary = Color(0xFF43A047),
    background = StandardBackgroundLight,
    surface = StandardSurfaceLight,
    onBackground = StandardOnSurfaceLight,
    onSurface = StandardOnSurfaceLight,
    surfaceVariant = Color(0xFFEEEEF6),
    onSurfaceVariant = Color(0xFF555566),
    primaryContainer = Color(0xFFEDE7F6),
    onPrimaryContainer = StandardPrimaryLight,
    error = Color(0xFFC62828)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Set dynamic color default to false to force our curated Premium Slate Indigo / Gold look,
    // which gives better consistency and visual polish!
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> DarkColorScheme // Let's use Dark Theme by default for that eye-safe cyberpunk workspace aesthetic!
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
