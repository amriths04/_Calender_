package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// iOS San Francisco (Helvetica-like) Style Guidelines Typography mapping
// Uses default SansSerif (Helvetica/Inter basis on Android) for pixel-perfect iOS look
val IosLargeTitle = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Bold,
    fontSize = 34.sp,
    lineHeight = 41.sp,
    letterSpacing = 0.4.sp
)

val IosTitle = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.SemiBold,
    fontSize = 17.sp,
    lineHeight = 22.sp,
    letterSpacing = (-0.43).sp
)

val IosBody = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Normal,
    fontSize = 17.sp,
    lineHeight = 22.sp,
    letterSpacing = (-0.43).sp
)

val IosSecondary = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Normal,
    fontSize = 15.sp,
    lineHeight = 20.sp,
    letterSpacing = (-0.24).sp
)

val IosTertiary = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Normal,
    fontSize = 13.sp,
    lineHeight = 18.sp,
    letterSpacing = (-0.08).sp
)

val IosTabBarLabel = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.Medium,
    fontSize = 10.sp,
    lineHeight = 12.sp,
    letterSpacing = 0.12.sp
)

val Typography = Typography(
    displayLarge = IosLargeTitle,
    headlineLarge = IosLargeTitle.copy(fontSize = 30.sp, lineHeight = 36.sp),
    headlineMedium = IosLargeTitle.copy(fontSize = 24.sp, lineHeight = 30.sp),
    titleLarge = IosTitle,
    titleMedium = IosTitle.copy(fontWeight = FontWeight.Medium),
    titleSmall = IosTitle.copy(fontSize = 16.sp, fontWeight = FontWeight.Medium),
    bodyLarge = IosBody,
    bodyMedium = IosSecondary,
    bodySmall = IosTertiary,
    labelLarge = IosBody.copy(fontWeight = FontWeight.Medium),
    labelMedium = IosTertiary.copy(fontWeight = FontWeight.Medium),
    labelSmall = IosTabBarLabel
)

