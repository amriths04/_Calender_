package com.example.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Modular scaling utilities for a highly responsive Android Jetpack Compose UI across all devices.
 * Emulates React Native's dimensions scaling mathematically but adapted to density independent
 * units (Dp and Sp) for maximum Material Design 3 compatibility.
 */
object ResponsiveUtil {
    // Guideline sizes based on standard mobile device (e.g., Nexus 5 or Pixel 4 is approx 360 x 640 in DP)
    private const val GUIDELINE_BASE_WIDTH = 360f
    private const val GUIDELINE_BASE_HEIGHT = 640f

    /**
     * Scale horizontally based on screen width.
     */
    @Composable
    fun scale(size: Float): Dp {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp
        val scaleFactor = screenWidth / GUIDELINE_BASE_WIDTH
        // Coerce factor to avoid layout blowing up on exceptionally large tablet screens (standard max boundaries)
        val boundedFactor = scaleFactor.coerceIn(0.8f, 1.5f)
        return (size * boundedFactor).dp
    }

    @Composable
    fun scale(size: Int): Dp = scale(size.toFloat())

    /**
     * Scale vertically based on screen height.
     */
    @Composable
    fun verticalScale(size: Float): Dp {
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp
        val scaleFactor = screenHeight / GUIDELINE_BASE_HEIGHT
        val boundedFactor = scaleFactor.coerceIn(0.8f, 1.5f)
        return (size * boundedFactor).dp
    }

    @Composable
    fun verticalScale(size: Int): Dp = verticalScale(size.toFloat())

    /**
     * Moderate scale which dampens scaling for sizes like padding to keep visual stability on larger devices.
     */
    @Composable
    fun moderateScale(size: Float, factor: Float = 0.5f): Dp {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp
        val scaleFactor = screenWidth / GUIDELINE_BASE_WIDTH
        val boundedFactor = scaleFactor.coerceIn(0.8f, 1.5f)
        val scaledVal = size * boundedFactor
        return (size + (scaledVal - size) * factor).dp
    }

    @Composable
    fun moderateScale(size: Int, factor: Float = 0.5f): Dp = moderateScale(size.toFloat(), factor)

    /**
     * Font-size normalization utility matching the React Native implementation.
     * Prevents oversized text on large screens while retaining dynamic scale.
     */
    @Composable
    fun normalize(size: Float): TextUnit {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp
        val scaleFactor = screenWidth / GUIDELINE_BASE_WIDTH
        // Provide gentle scaling for font sizes so text is highly legible but doesn't overflow container widgets
        val boundedFactor = scaleFactor.coerceIn(0.85f, 1.35f)
        return (size * boundedFactor).sp
    }

    @Composable
    fun normalize(size: Int): TextUnit = normalize(size.toFloat())
}
