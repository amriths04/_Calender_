package com.example

import android.app.Application
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.CalendarDatabase
import com.example.data.CalendarEvent
import com.example.data.CalendarRepository
import com.example.data.WidgetSettings
import com.example.data.WidgetTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private val repository: CalendarRepository

    init {
        val database = CalendarDatabase.getDatabase(context)
        repository = CalendarRepository(database.calendarEventDao())
    }

    // Current app display parameters
    val activeTab = MutableStateFlow("calendar") // "calendar" or "customise"
    val isDarkMode = MutableStateFlow(WidgetSettings.isDarkMode(context))

    // Widget specific configs
    val widgetTheme = MutableStateFlow(WidgetSettings.getTheme(context))
    val widgetTransparency = MutableStateFlow(WidgetSettings.getTransparency(context))
    val firstDayOfWeek = MutableStateFlow(WidgetSettings.getFirstDayOfWeek(context))

    // Active viewing Month
    val currentYearMonth = MutableStateFlow(YearMonth.now())

    // Selected day in Calendar View
    val selectedDate = MutableStateFlow(LocalDate.now())

    // List of events saved for the selected day
    val eventsForSelectedDate: StateFlow<List<CalendarEvent>> = selectedDate
        .flatMapLatest { date ->
            repository.getEventsForDate(date.toString())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun selectDate(date: LocalDate) {
        selectedDate.value = date
    }

    fun nextMonth() {
        currentYearMonth.value = currentYearMonth.value.plusMonths(1)
    }

    fun prevMonth() {
        currentYearMonth.value = currentYearMonth.value.minusMonths(1)
    }

    fun toggleDarkMode() {
        val nextMode = !isDarkMode.value
        isDarkMode.value = nextMode
        WidgetSettings.setDarkMode(context, nextMode)
        CalendarWidgetProvider.triggerAllWidgetsUpdate(context)
    }

    fun setWidgetTheme(theme: WidgetTheme) {
        widgetTheme.value = theme
        WidgetSettings.setTheme(context, theme)
        CalendarWidgetProvider.triggerAllWidgetsUpdate(context)
    }

    fun setWidgetTransparency(transparencyPercent: Int) {
        widgetTransparency.value = transparencyPercent
        WidgetSettings.setTransparency(context, transparencyPercent)
        CalendarWidgetProvider.triggerAllWidgetsUpdate(context)
    }

    fun setFirstDayOfWeek(firstDay: Int) {
        firstDayOfWeek.value = firstDay
        WidgetSettings.setFirstDayOfWeek(context, firstDay)
        CalendarWidgetProvider.triggerAllWidgetsUpdate(context)
    }

    fun addEvent(title: String, description: String = "", category: String = "Work") {
        viewModelScope.launch {
            repository.insert(
                CalendarEvent(
                    date = selectedDate.value.toString(),
                    title = title,
                    description = description,
                    category = category
                )
            )
        }
    }

    fun deleteEvent(event: CalendarEvent) {
        viewModelScope.launch {
            repository.delete(event)
        }
    }

    fun isPinWidgetSupported(): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                appWidgetManager?.isRequestPinAppWidgetSupported ?: false
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    fun pinWidget() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val myProvider = ComponentName(context, CalendarWidgetProvider::class.java)
                if (appWidgetManager != null && appWidgetManager.isRequestPinAppWidgetSupported) {
                    appWidgetManager.requestPinAppWidget(myProvider, null, null)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
