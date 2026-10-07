package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Indigo400,
    onPrimary = Indigo900,
    primaryContainer = Indigo800,
    onPrimaryContainer = Indigo200,
    secondary = AmberFocus,
    onSecondary = Indigo900,
    secondaryContainer = Indigo700,
    onSecondaryContainer = AmberGlow,
    tertiary = CyanAccent,
    onTertiary = Indigo900,
    tertiaryContainer = DarkSurfaceVariant,
    onTertiaryContainer = CyanGlow,
    background = DarkSurface,
    onBackground = TextPrimaryDark,
    surface = DarkSurfaceVariant,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkSurfaceCard,
    onSurfaceVariant = TextSecondaryDark,
    outline = DarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = Indigo600,
    onPrimary = LightSurface,
    primaryContainer = Indigo50,
    onPrimaryContainer = Indigo900,
    secondary = AmberDark,
    onSecondary = LightSurface,
    secondaryContainer = AmberGlow,
    onSecondaryContainer = AmberDark,
    tertiary = CyanAccent,
    onTertiary = LightSurface,
    tertiaryContainer = CyanGlow,
    onTertiaryContainer = Indigo900,
    background = LightSurface,
    onBackground = TextPrimaryLight,
    surface = LightSurfaceCard,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,
    outline = LightBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek focus dark mode for study intensity
    dynamicColor: Boolean = false,
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
