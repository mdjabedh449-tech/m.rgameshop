package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GameZoneDarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = OnGold,
    primaryContainer = GoldContainer,
    onPrimaryContainer = GoldSecondary,
    secondary = GoldSecondary,
    onSecondary = OnGold,
    secondaryContainer = DarkSurfaceElevated,
    onSecondaryContainer = GoldSecondary,
    tertiary = GoldAccent,
    onTertiary = OnGold,
    background = DarkBackground,
    onBackground = TextWhite,
    surface = DarkSurface,
    onSurface = TextWhite,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = DarkSurfaceBorder,
    outlineVariant = GoldBronze,
    error = StatusCancelled,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Always force the signature black and gold gaming aesthetic for this admin portal
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GameZoneDarkColorScheme,
        typography = Typography,
        content = content
    )
}
