package com.example.planetlife.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color

private val PlanetLifeLightColorScheme = lightColorScheme(
    primary = ForestGreen,
    onPrimary = Color.White,
    secondary = CrystalBlue,
    onSecondary = Color.White,
    tertiary = DreamPurple,
    background = SkyBottom,
    onBackground = TextBrown,
    surface = Cream,
    onSurface = TextBrown,
    surfaceVariant = CreamLight,
    outline = CreamBorder,
)

private val PlanetLifeDarkColorScheme = darkColorScheme(
    primary = CrystalBlue,
    onPrimary = Color.White,
    secondary = ForestGreen,
    onSecondary = Color.White,
    tertiary = DreamPurple,
    background = Color(0xFF172034),
    onBackground = Color(0xFFEDE7D6),
    surface = Color(0xFF26304A),
    onSurface = Color(0xFFEDE7D6),
    surfaceVariant = Color(0xFF303A55),
    outline = Color(0xFF6E7690),
)

@Composable
fun PlanetLifeTheme(
    themeMode: String = "跟随系统",
    content: @Composable () -> Unit,
) {
    val systemDark = isSystemInDarkTheme()
    val useDarkTheme = when (themeMode) {
        "夜间", "night", "dark" -> true
        "白天", "day", "light" -> false
        else -> systemDark
    }

    MaterialTheme(
        colorScheme = if (useDarkTheme) PlanetLifeDarkColorScheme else PlanetLifeLightColorScheme,
        typography = PlanetLifeTypography,
        content = content,
    )
}
