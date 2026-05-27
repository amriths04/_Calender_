package com.example.data

import android.content.Context

object WidgetSettings {
    private const val PREFS_NAME = "CalendarPlusPrefs"
    private const val KEY_DARK_MODE = "is_dark_mode"
    private const val KEY_WIDGET_THEME = "widget_theme"
    private const val KEY_WIDGET_TRANSPARENCY = "widget_transparency" // 0 to 100
    private const val KEY_FIRST_DAY_WEEK = "first_day_of_week" // 1 = Monday, 7 = Sunday
    private const val KEY_WIDGET_MONTH_OFFSET = "widget_month_offset_" // Suffix with appWidgetId

    fun isDarkMode(context: Context): Boolean {
        // Default to false (Light mode), but can read system dark mode or stored preference
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_DARK_MODE, false)
    }

    fun setDarkMode(context: Context, isDark: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_DARK_MODE, isDark)
            .apply()
    }

    fun getTheme(context: Context): WidgetTheme {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val themeName = prefs.getString(KEY_WIDGET_THEME, WidgetTheme.VIBRANT_PURPLE.name) ?: WidgetTheme.VIBRANT_PURPLE.name
        return try {
            WidgetTheme.valueOf(themeName)
        } catch (e: Exception) {
            WidgetTheme.VIBRANT_PURPLE
        }
    }

    fun setTheme(context: Context, theme: WidgetTheme) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_WIDGET_THEME, theme.name)
            .apply()
    }

    fun getTransparency(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_WIDGET_TRANSPARENCY, 0) // default 0% transparency (fully opaque)
    }

    fun setTransparency(context: Context, transparencyPercent: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_WIDGET_TRANSPARENCY, transparencyPercent.coerceIn(0, 100))
            .apply()
    }

    fun getFirstDayOfWeek(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_FIRST_DAY_WEEK, 1) // default Monday (1)
    }

    fun setFirstDayOfWeek(context: Context, firstDay: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_FIRST_DAY_WEEK, firstDay.coerceIn(1, 7))
            .apply()
    }

    // Offset used for navigating months in the widget itself
    fun getWidgetMonthOffset(context: Context, appWidgetId: Int): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt("$KEY_WIDGET_MONTH_OFFSET$appWidgetId", 0)
    }

    fun setWidgetMonthOffset(context: Context, appWidgetId: Int, offset: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt("$KEY_WIDGET_MONTH_OFFSET$appWidgetId", offset)
            .apply()
    }

    fun clearWidgetMonthOffset(context: Context, appWidgetId: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove("$KEY_WIDGET_MONTH_OFFSET$appWidgetId")
            .apply()
    }
}

enum class WidgetTheme(
    val displayName: String,
    val primaryColor: Long, // Hex values as Long
    val secondaryColor: Long,
    val description: String
) {
    VIBRANT_PURPLE(
        "Cobalt Ocean",
        0xFF0056C6L,
        0xFFE0F2FEL,
        "Premium crisp royal blue accents."
    ),
    EMERALD_TEAL(
        "Emerald Teal",
        0xFF006A6AL,
        0xFFCCEBEBL,
        "Calm organic deep green accents."
    ),
    SUNSET_CRIMSON(
        "Sunset Crimson",
        0xFFB3261EL,
        0xFFF9DEDCL,
        "Striking high contrast warm crimson red."
    ),
    AMBER_OCHRE(
        "Amber Ochre",
        0xFF8A5100L,
        0xFFFFDDB3L,
        "Playful autumn orange dynamic highlights."
    ),
    MIDNIGHT_WALNUT(
        "Midnight Walnut",
        0xFF211814L,
        0xFFEAE2DFL,
        "Sophisticated luxury deep chocolate charcoal."
    );

    fun getAccentHex(isDark: Boolean): Int {
        return primaryColor.toInt()
    }

    fun getAccentColorHex(): Int {
        return primaryColor.toInt()
    }
}
