package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.WidgetTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.YearMonth

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Calendar", appName)
  }

  @Test
  fun `verify MainActivity launch`() {
    val controller = Robolectric.buildActivity(MainActivity::class.java)
    controller.setup()
    assertNotNull(controller.get())
  }

  @Test
  fun `verify CalendarViewModel actions`() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = CalendarViewModel(application)

    // Verify initial values
    assertNotNull(viewModel.currentYearMonth.value)
    assertNotNull(viewModel.selectedDate.value)

    // Test month switching
    val initialMonth = viewModel.currentYearMonth.value
    viewModel.nextMonth()
    assertEquals(initialMonth.plusMonths(1), viewModel.currentYearMonth.value)

    viewModel.prevMonth()
    assertEquals(initialMonth, viewModel.currentYearMonth.value)

    // Test DarkMode toggling
    val initialDark = viewModel.isDarkMode.value
    viewModel.toggleDarkMode()
    assertEquals(!initialDark, viewModel.isDarkMode.value)

    // Test FirstDayOfWeek updates
    viewModel.setFirstDayOfWeek(1)
    assertEquals(1, viewModel.firstDayOfWeek.value)
    viewModel.setFirstDayOfWeek(7)
    assertEquals(7, viewModel.firstDayOfWeek.value)

    // Test Theme updates
    viewModel.setWidgetTheme(WidgetTheme.EMERALD_TEAL)
    assertEquals(WidgetTheme.EMERALD_TEAL, viewModel.widgetTheme.value)
  }
}


