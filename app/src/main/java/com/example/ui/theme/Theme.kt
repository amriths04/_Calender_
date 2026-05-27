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

private val LightColorScheme =
  lightColorScheme(
    primary = Color(0xFF2563EB), // Selected Day / Selected Tab Button (bg-blue-600)
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE2E8F0),
    onPrimaryContainer = Color(0xFF0F172A),
    secondary = Color(0xFF334155),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFCCCCCC), // Chinese grey toggle background in light mode
    onSecondaryContainer = Color(0xFF1E293B),
    background = Color(0xFFF8FAFC), // Desktop Background (soft, very light gray/slate-50)
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF), // Component Cards (pure white)
    onSurface = Color(0xFF0F172A), // Headings (text-slate-900)
    surfaceVariant = Color(0xFFDBEAFE), // App Background (the phone itself, elegant light blue bg-blue-100)
    onSurfaceVariant = Color(0xFF334155), // Calendar Days (text-slate-700)
    outline = Color(0xFFE2E8F0), // Component Card border (border-slate-200)
    outlineVariant = Color(0xFFE2E8F0).copy(alpha = 0.5f) // Navigation tabs container
  )

private val DarkColorScheme =
  darkColorScheme(
    primary = Color(0xFF2563EB), // Selected Day / Selected Tab Button (bg-blue-600)
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF1E293B),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0xFFE2E8F0),
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFF020617), // Navigation Tabs Container (bg-slate-950)
    onSecondaryContainer = Color(0xFFFFFFFF),
    background = Color(0xFF000000), // Desktop Background (pure pitch black)
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF000000), // Component Cards (pure pitch black)
    onSurface = Color(0xFFFFFFFF), // Headings (text-white)
    surfaceVariant = Color(0xFF0F172A), // App Background (the phone itself, bg-slate-900)
    onSurfaceVariant = Color(0xFFE2E8F0), // Calendar Days (text-slate-200)
    outline = Color(0xFF1E293B), // Component Card border (border-slate-800)
    outlineVariant = Color(0xFF020617) // Navigation tabs container
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disable dynamic color so we enforce our beautiful custom Vibrant Palette
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

