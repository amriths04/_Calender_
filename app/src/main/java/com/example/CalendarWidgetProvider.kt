package com.example

import android.app.PendingIntent
import android.app.WallpaperManager
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import com.example.data.WidgetSettings
import com.example.data.WidgetTheme
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

class CalendarWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            WidgetSettings.clearWidgetMonthOffset(context, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val action = intent.action
        val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)

        if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
            val currentOffset = WidgetSettings.getWidgetMonthOffset(context, appWidgetId)
            when (action) {
                ACTION_PREV_MONTH -> {
                    WidgetSettings.setWidgetMonthOffset(context, appWidgetId, currentOffset - 1)
                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    updateAppWidget(context, appWidgetManager, appWidgetId)
                }
                ACTION_NEXT_MONTH -> {
                    WidgetSettings.setWidgetMonthOffset(context, appWidgetId, currentOffset + 1)
                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    updateAppWidget(context, appWidgetManager, appWidgetId)
                }
            }
        } else if (action == ACTION_UPDATE_ALL_WIDGETS || action == Intent.ACTION_TIME_CHANGED || action == Intent.ACTION_DATE_CHANGED) {
            // Update all widgets
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, CalendarWidgetProvider::class.java)
            val allIds = appWidgetManager.getAppWidgetIds(thisWidget)
            for (id in allIds) {
                updateAppWidget(context, appWidgetManager, id)
            }
        }
    }

    companion object {
        const val ACTION_PREV_MONTH = "com.example.calendarplus.ACTION_PREV_MONTH"
        const val ACTION_NEXT_MONTH = "com.example.calendarplus.ACTION_NEXT_MONTH"
        const val ACTION_UPDATE_ALL_WIDGETS = "com.example.calendarplus.ACTION_UPDATE_ALL_WIDGETS"

        fun triggerAllWidgetsUpdate(context: Context) {
            try {
                val intent = Intent(context, CalendarWidgetProvider::class.java).apply {
                    action = ACTION_UPDATE_ALL_WIDGETS
                }
                context.sendBroadcast(intent)
            } catch (e: Exception) {
                // Ignore safe exception if broadcasting is restricted
            }
        }

        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.calendar_widget_layout)

            // 1. Read preference configurations
            val isDark = WidgetSettings.isDarkMode(context)
            val theme = WidgetSettings.getTheme(context)
            val transparency = WidgetSettings.getTransparency(context)
            val firstDayOfWeek = WidgetSettings.getFirstDayOfWeek(context) // 1 = Monday, 7 = Sunday
            val offset = WidgetSettings.getWidgetMonthOffset(context, appWidgetId)

            // 2. Setup Background Theme and Transparency
            val bgResource = if (isDark) R.drawable.widget_bg_dark else R.drawable.widget_bg_light
            views.setInt(R.id.widget_container, "setBackgroundResource", bgResource)

            // Calculate colors based on theme mode
            val textColorPrimary = if (isDark) Color.parseColor("#FFFFFF") else Color.parseColor("#111111")
            val textColorSecondary = if (isDark) Color.parseColor("#8E8E93") else Color.parseColor("#666666")
            val textColorSunday = Color.parseColor("#FF3B30")
            val textColorMuted = if (isDark) Color.parseColor("#444446") else Color.parseColor("#C7C7CC")

            views.setTextColor(R.id.widget_month_title, textColorPrimary)

            // Update weekday headers based on first day starting choice
            val headers = if (firstDayOfWeek == 1) {
                listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            } else {
                listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            }

            val weekHeaderIds = listOf(
                R.id.week_header_0, R.id.week_header_1, R.id.week_header_2,
                R.id.week_header_3, R.id.week_header_4, R.id.week_header_5, R.id.week_header_6
            )

            for (i in 0..6) {
                views.setTextViewText(weekHeaderIds[i], headers[i])
                if (headers[i] == "Sun") {
                    views.setTextColor(weekHeaderIds[i], textColorSunday)
                } else {
                    views.setTextColor(weekHeaderIds[i], textColorSecondary)
                }
            }

            // 3. Compute Dates Grid (6x7 = 42 cells)
            val today = LocalDate.now()
            val targetDate = today.plusMonths(offset.toLong())
            val targetYearMonth = YearMonth.of(targetDate.year, targetDate.month)

            // Formatted Month & Year
            val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
            val monthTitle = targetYearMonth.format(formatter)
            views.setTextViewText(R.id.widget_month_title, monthTitle)

            val lengthOfMonth = targetYearMonth.lengthOfMonth()
            val firstOfMonth = targetYearMonth.atDay(1)
            val dayOfWeekVal = firstOfMonth.dayOfWeek.value // 1 (Mon) .. 7 (Sun)

            // Offset based on firstDayOfWeek config
            val startOffset = if (firstDayOfWeek == 1) {
                dayOfWeekVal - 1
            } else {
                if (dayOfWeekVal == 7) 0 else dayOfWeekVal
            }

            val cellsList = ArrayList<GridDay>()

            // Fill trailing days from previous month
            val prevMonth = targetYearMonth.minusMonths(1)
            val prevMonthLength = prevMonth.lengthOfMonth()
            for (i in (prevMonthLength - startOffset + 1)..prevMonthLength) {
                cellsList.add(GridDay(i, false, prevMonth.atDay(i)))
            }

            // Fill current month days
            for (i in 1..lengthOfMonth) {
                cellsList.add(GridDay(i, true, targetYearMonth.atDay(i)))
            }

            // Fill remaining leading days for next month
            val remaining = 42 - cellsList.size
            val nextMonth = targetYearMonth.plusMonths(1)
            for (i in 1..remaining) {
                cellsList.add(GridDay(i, false, nextMonth.atDay(i)))
            }

            // Map grid list items to TextView child IDs: cell_0 to cell_41
            val cellIds = listOf(
                R.id.cell_0, R.id.cell_1, R.id.cell_2, R.id.cell_3, R.id.cell_4, R.id.cell_5, R.id.cell_6,
                R.id.cell_7, R.id.cell_8, R.id.cell_9, R.id.cell_10, R.id.cell_11, R.id.cell_12, R.id.cell_13,
                R.id.cell_14, R.id.cell_15, R.id.cell_16, R.id.cell_17, R.id.cell_18, R.id.cell_19, R.id.cell_20,
                R.id.cell_21, R.id.cell_22, R.id.cell_23, R.id.cell_24, R.id.cell_25, R.id.cell_26, R.id.cell_27,
                R.id.cell_28, R.id.cell_29, R.id.cell_30, R.id.cell_31, R.id.cell_32, R.id.cell_33, R.id.cell_34,
                R.id.cell_35, R.id.cell_36, R.id.cell_37, R.id.cell_38, R.id.cell_39, R.id.cell_40, R.id.cell_41
            )

            // Reset background and values for all cell views
            for (idx in cellIds.indices) {
                val cellId = cellIds[idx]
                val gridDay = cellsList[idx]
                views.setTextViewText(cellId, gridDay.dayNumber.toString())

                // Check Sunday mapping for color styling
                val isSundayColumn = if (firstDayOfWeek == 1) {
                    idx % 7 == 6
                } else {
                    idx % 7 == 0
                }

                val isToday = gridDay.localDate.isEqual(today) && gridDay.isCurrentMonth

                if (isToday) {
                    // Highlight active current day matching user's theme selection!
                    val activeBg = when (theme) {
                        WidgetTheme.VIBRANT_PURPLE -> R.drawable.cell_active_cobalt
                        WidgetTheme.EMERALD_TEAL -> R.drawable.cell_active_emerald
                        WidgetTheme.SUNSET_CRIMSON -> R.drawable.cell_active_crimson
                        WidgetTheme.AMBER_OCHRE -> R.drawable.cell_active_cobalt
                        WidgetTheme.MIDNIGHT_WALNUT -> R.drawable.cell_active_slate
                    }
                    views.setInt(cellId, "setBackgroundResource", activeBg)
                    views.setTextColor(cellId, Color.WHITE)
                } else {
                    views.setInt(cellId, "setBackgroundResource", 0) // No background wrapper
                    if (gridDay.isCurrentMonth) {
                        if (isSundayColumn) {
                            views.setTextColor(cellId, textColorSunday)
                        } else {
                            views.setTextColor(cellId, textColorPrimary)
                        }
                    } else {
                        views.setTextColor(cellId, textColorMuted)
                    }
                }
            }

            // 4. Click Pending Intents
            // Previous Button Intent
            val intentPrev = Intent(context, CalendarWidgetProvider::class.java).apply {
                action = ACTION_PREV_MONTH
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            val pendingPrev = PendingIntent.getBroadcast(
                context,
                appWidgetId * 10 + 1,
                intentPrev,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
            views.setOnClickPendingIntent(R.id.btn_prev_month, pendingPrev)

            // Next Button Intent
            val intentNext = Intent(context, CalendarWidgetProvider::class.java).apply {
                action = ACTION_NEXT_MONTH
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }
            val pendingNext = PendingIntent.getBroadcast(
                context,
                appWidgetId * 10 + 2,
                intentNext,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
            views.setOnClickPendingIntent(R.id.btn_next_month, pendingNext)

            // Click background or Month Title to open App
            val openAppIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingOpen = PendingIntent.getActivity(
                context,
                appWidgetId * 10 + 3,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_month_title, pendingOpen)

            // Submit update
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

data class GridDay(
    val dayNumber: Int,
    val isCurrentMonth: Boolean,
    val localDate: java.time.LocalDate
)
