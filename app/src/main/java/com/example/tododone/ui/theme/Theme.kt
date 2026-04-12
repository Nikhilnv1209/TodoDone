package com.example.tododone.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ============================================
// PREMIUM DARK THEME - Hybrid (Dark + Colorful)
// ============================================

// Background Colors
val BackgroundPrimary = Color(0xFF0D0D0D)       // Deep black
val BackgroundSecondary = Color(0xFF1A1A1A)     // Dark gray
val BackgroundElevated = Color(0xFF242424)      // Elevated surface

// Accent Colors - Pastel Palette
val AccentLavender = Color(0xFFE0D5F5)          // Soft purple
val AccentMint = Color(0xFFD4F5E0)              // Soft green
val AccentCream = Color(0xFFF5F0D4)             // Soft yellow/cream
val AccentPeach = Color(0xFFF5DDD4)             // Soft peach
val AccentSky = Color(0xFFD4E5F5)               // Soft blue

// Primary Brand Colors
val PrimaryCream = Color(0xFFF5E6D3)            // Main accent (like FAB)
val PrimaryCreamDark = Color(0xFFD4C4B0)        // Pressed state
val OnPrimaryDark = Color(0xFF1A1A1A)           // Text on cream

// Semantic Colors
val SuccessGreen = Color(0xFF4ADE80)
val WarningOrange = Color(0xFFFB923C)
val ErrorRed = Color(0xFFF87171)
val InfoBlue = Color(0xFF60A5FA)

// Text Colors
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFB3B3B3)
val TextTertiary = Color(0xFF808080)

// Priority Colors
val PriorityHigh = Color(0xFFFCA5A5)            // Soft red
val PriorityMedium = Color(0xFFFCD34D)          // Soft yellow
val PriorityLow = Color(0xFF86EFAC)             // Soft green

// Glassmorphism
val GlassmorphismLight = Color(0x1AFFFFFF)      // 10% white
val GlassmorphismDark = Color(0x0D000000)       // 5% black

// Navigation Colors
val DarkSurface = Color(0xFF1C1C1E)             // Dark surface for navbar
val ActiveNavIndicator = Color(0xFFF5E6D3)      // Cream color for active nav item

// Premium Dark Color Scheme
private val PremiumDarkColorScheme = darkColorScheme(
    primary = PrimaryCream,
    onPrimary = OnPrimaryDark,
    primaryContainer = BackgroundElevated,
    onPrimaryContainer = TextPrimary,
    secondary = AccentLavender,
    onSecondary = BackgroundPrimary,
    secondaryContainer = BackgroundSecondary,
    onSecondaryContainer = TextPrimary,
    tertiary = AccentMint,
    onTertiary = BackgroundPrimary,
    tertiaryContainer = BackgroundElevated,
    onTertiaryContainer = TextPrimary,
    background = BackgroundPrimary,
    onBackground = TextPrimary,
    surface = BackgroundSecondary,
    onSurface = TextPrimary,
    surfaceVariant = BackgroundElevated,
    onSurfaceVariant = TextSecondary,
    outline = TextTertiary,
    outlineVariant = Color(0xFF333333),
    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFF451515),
    onErrorContainer = ErrorRed,
)

@Composable
fun TodoDoneTheme(
    darkTheme: Boolean = true, // Always use dark theme for premium look
    content: @Composable () -> Unit
) {
    val colorScheme = PremiumDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = BackgroundPrimary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
