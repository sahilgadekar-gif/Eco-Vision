package com.vasundhara.app.ui.components

/**
 * EcoComponents.kt — legacy aliases kept for backward compatibility.
 * All canonical components live in SharedComponents.kt.
 * This file re-exports them so any old import still compiles.
 */

// GradientBackground is superseded by VasundharaBackground in SharedComponents.
// Kept as a typealias-style wrapper so old screens compile without changes.
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.vasundhara.app.ui.theme.DarkBg
import com.vasundhara.app.ui.theme.LocalDarkTheme
import com.vasundhara.app.ui.theme.LightBg
import com.vasundhara.app.ui.theme.LightSurfaceVar
import com.vasundhara.app.ui.theme.NavyBase

@Composable
fun GradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = LocalDarkTheme.current
    Box(
        modifier = modifier.fillMaxSize().background(
            if (isDark) Brush.verticalGradient(listOf(DarkBg, NavyBase, DarkBg))
            else Brush.verticalGradient(listOf(LightBg, LightSurfaceVar, LightBg))
        ),
        content = content
    )
}
