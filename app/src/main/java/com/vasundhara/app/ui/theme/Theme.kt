package com.vasundhara.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

// ── Dark Scheme ───────────────────────────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary = EcoGreen,
    onPrimary = PureBlack,
    primaryContainer = EcoGreenDark,
    onPrimaryContainer = PureWhite,
    secondary = NavyAccent,
    onSecondary = PureWhite,
    secondaryContainer = NavyLight,
    onSecondaryContainer = OffWhite,
    tertiary = InfoBlue,
    onTertiary = PureWhite,
    background = DarkBg,
    onBackground = PureWhite,
    surface = DarkSurface,
    onSurface = PureWhite,
    surfaceVariant = DarkSurfaceHigh,
    onSurfaceVariant = SurfaceWhite,
    outline = DarkOutline,
    error = ErrorRed,
    onError = PureWhite,
    scrim = Color(0x99000000)
)

// ── Light Scheme ──────────────────────────────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary = EcoGreenDark,
    onPrimary = PureWhite,
    primaryContainer = Color(0xFFB9F6CA),
    onPrimaryContainer = Color(0xFF002111),
    secondary = NavyAccent,
    onSecondary = PureWhite,
    secondaryContainer = Color(0xFFD6E4FF),
    onSecondaryContainer = NavyBase,
    tertiary = InfoBlue,
    onTertiary = PureWhite,
    background = LightBg,
    onBackground = NavyBase,
    surface = LightSurface,
    onSurface = NavyBase,
    surfaceVariant = LightSurfaceVar,
    onSurfaceVariant = NavyMid,
    outline = LightOutline,
    error = ErrorRed,
    onError = PureWhite,
)

// ── Theme State ───────────────────────────────────────────────────────────────
val LocalDarkTheme = compositionLocalOf { true }

@Composable
fun VasundharaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    CompositionLocalProvider(LocalDarkTheme provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = VasundharaTypography,
            content = content
        )
    }
}
