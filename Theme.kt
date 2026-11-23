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
    primary = LibraryDarkPrimary,
    onPrimary = LibraryEmeraldDark,
    primaryContainer = LibraryEmeraldDark,
    onPrimaryContainer = LibraryDarkPrimary,
    secondary = LibraryDarkSecondary,
    onSecondary = LibraryOnGoldContainer,
    secondaryContainer = Color(0xFF4A3408),
    onSecondaryContainer = LibraryGoldContainer,
    tertiary = LibraryDarkTertiary,
    background = LibraryDarkBackground,
    surface = LibraryDarkSurface,
    surfaceVariant = LibraryDarkSurfaceVariant,
    onBackground = LibraryTextPrimaryDark,
    onSurface = LibraryTextPrimaryDark,
    onSurfaceVariant = LibraryTextSecondaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = LibraryEmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = LibraryEmeraldContainer,
    onPrimaryContainer = LibraryOnEmeraldContainer,
    secondary = LibraryGoldSecondary,
    onSecondary = Color.White,
    secondaryContainer = LibraryGoldContainer,
    onSecondaryContainer = LibraryOnGoldContainer,
    tertiary = LibraryNavyTertiary,
    background = LibraryBackgroundLight,
    surface = LibrarySurfaceLight,
    surfaceVariant = LibrarySurfaceVariantLight,
    onBackground = LibraryTextPrimaryLight,
    onSurface = LibraryTextPrimaryLight,
    onSurfaceVariant = LibraryTextSecondaryLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Use our bespoke library palette by default for coherent brand identity
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
