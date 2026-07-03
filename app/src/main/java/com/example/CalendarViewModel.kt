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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalCoroutinesApi::class)
class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private val repository: CalendarRepository
    
    // Current app display parameters
    val activeTab = MutableStateFlow("calendar") // "calendar" or "customise"
    val isDarkMode = MutableStateFlow(false) // Safe default to prevent blocking app startup

    // Widget specific configs
    val widgetTheme = MutableStateFlow(WidgetTheme.VIBRANT_PURPLE)
    val widgetTransparency = MutableStateFlow(0)
    val firstDayOfWeek = MutableStateFlow(1)

    // Active viewing Month
    val currentYearMonth = MutableStateFlow(YearMonth.now())

    // Current real-world date
    val today = MutableStateFlow(LocalDate.now())

    // Selected day in Calendar View
    val selectedDate = MutableStateFlow(LocalDate.now())

    fun refreshCurrentDate() {
        val currentDate = LocalDate.now()
        if (today.value != currentDate) {
            today.value = currentDate
        }
    }

    init {
        val database = CalendarDatabase.getDatabase(context)
        repository = CalendarRepository(database.calendarEventDao())

        // Offload SharedPreferences disk reads to background I/O thread
        viewModelScope.launch(Dispatchers.IO) {
            isDarkMode.value = WidgetSettings.isDarkMode(context)
            widgetTheme.value = WidgetSettings.getTheme(context)
            widgetTransparency.value = WidgetSettings.getTransparency(context)
            firstDayOfWeek.value = WidgetSettings.getFirstDayOfWeek(context)
        }
    }

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
            CalendarWidgetProvider.triggerAllWidgetsUpdate(context)
        }
    }

    fun deleteEvent(event: CalendarEvent) {
        viewModelScope.launch {
            repository.delete(event)
            CalendarWidgetProvider.triggerAllWidgetsUpdate(context)
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
                    val previewBundle = android.os.Bundle().apply {
                        val remoteViews = CalendarWidgetProvider.buildWidgetViews(context, AppWidgetManager.INVALID_APPWIDGET_ID)
                        // If we had a mechanism to bind data, we would do it here. 
                        // But an unbound RemoteViews works as a structural preview.
                        putParcelable(AppWidgetManager.EXTRA_APPWIDGET_PREVIEW, remoteViews)
                    }
                    appWidgetManager.requestPinAppWidget(myProvider, previewBundle, null)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
