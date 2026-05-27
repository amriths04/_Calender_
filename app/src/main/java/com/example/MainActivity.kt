package com.example

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.layout.offset
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import android.view.HapticFeedbackConstants
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.CalendarEvent
import com.example.data.WidgetSettings
import com.example.data.WidgetTheme
import com.example.ui.theme.MyApplicationTheme
import com.example.util.ResponsiveUtil
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: CalendarViewModel = viewModel()
            val isDark by viewModel.isDarkMode.collectAsState()

            MyApplicationTheme(darkTheme = isDark, dynamicColor = false) {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun MainAppScreen(viewModel: CalendarViewModel) {
    val context = LocalContext.current
    val activeTab by viewModel.activeTab.collectAsState()
    val isDark by viewModel.isDarkMode.collectAsState()

    val desktopBackgroundColor = MaterialTheme.colorScheme.background
    val appBackgroundBrush = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF1E293B), // Premium light-dark slate blue (slate-800)
                Color(0xFF0F172A), // Core dark mode blue background (slate-900)
                Color(0xFF020617)  // Pitch-black slate blue depth (slate-950)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFEFF6FF), // Soft light-blue mist
                Color(0xFFDBEAFE)  // Soft light-blue surface (blue-100)
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(desktopBackgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxHeight()
                .background(appBackgroundBrush)
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(
                                start = ResponsiveUtil.moderateScale(20f),
                                end = ResponsiveUtil.moderateScale(20f),
                                top = ResponsiveUtil.moderateScale(8f),
                                bottom = ResponsiveUtil.moderateScale(4f)
                            )
                    ) {
                        Text(
                            text = "Calendar",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = ResponsiveUtil.normalize(25f)
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.align(Alignment.Center)
                        )
                        DarkLightModeToggle(
                            isDark = isDark,
                            onToggle = { viewModel.toggleDarkMode() },
                            modifier = Modifier.align(Alignment.CenterEnd)
                        )
                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    CalendarTabScreen(viewModel)
                }
            }
        }
    }
}

@OptIn(androidx.compose.animation.ExperimentalAnimationApi::class)
@Composable
fun CalendarTabScreen(viewModel: CalendarViewModel) {
    val currentMonth by viewModel.currentYearMonth.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val firstDayOfWeek by viewModel.firstDayOfWeek.collectAsState()
    val widgetTheme by viewModel.widgetTheme.collectAsState()
    val isDark by viewModel.isDarkMode.collectAsState()

    // Time ribbon config: Infinite/large scrolling anchored at Jan 2000
    val startMonth = remember { YearMonth.of(2000, 1) }
    val totalMonths = 100_000 // effectively infinite (over 8,000 years of swipe)

    fun getMonthIndex(yearMonth: YearMonth): Int {
        val monthsBetween = java.time.temporal.ChronoUnit.MONTHS.between(startMonth, yearMonth)
        return monthsBetween.toInt().coerceIn(0, totalMonths - 1)
    }

    fun getYearMonthFromIndex(index: Int): YearMonth {
        return startMonth.plusMonths(index.toLong())
    }

    val pagerState = rememberPagerState(
        initialPage = getMonthIndex(currentMonth),
        pageCount = { totalMonths }
    )
    val coroutineScope = rememberCoroutineScope()
    var isProgrammaticScrolling by remember { mutableStateOf(false) }
    var lastSettledMonthFromPager by remember { mutableStateOf<YearMonth?>(null) }


    // Sync pager to ViewModel updates (only when VM changes and pager is not already there or swiping)
    LaunchedEffect(currentMonth) {
        val targetIndex = getMonthIndex(currentMonth)
        if (currentMonth != lastSettledMonthFromPager) {
            if (pagerState.currentPage != targetIndex && !pagerState.isScrollInProgress && !isProgrammaticScrolling) {
                try {
                    pagerState.scrollToPage(targetIndex)
                } catch (e: Exception) {
                    // Safely ignore paging exceptions during standard layout/state transitions
                }
            }
        }
    }

    // Sync ViewModel to pager changes once they settle
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { settledPage ->
            try {
                if (settledPage >= 0 && !isProgrammaticScrolling) {
                    val visibleMonth = getYearMonthFromIndex(settledPage)
                    if (viewModel.currentYearMonth.value != visibleMonth) {
                        lastSettledMonthFromPager = visibleMonth
                        viewModel.currentYearMonth.value = visibleMonth
                    }
                }
            } catch (e: Exception) {
                // Safely ignore mapping exceptions
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = ResponsiveUtil.moderateScale(16f),
                end = ResponsiveUtil.moderateScale(16f),
                top = ResponsiveUtil.moderateScale(18f),
                bottom = ResponsiveUtil.moderateScale(8f)
            )
    ) {
        // Simplified header with only day and date (no month or year)
        val dateFormatted = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, d", Locale.getDefault()))
        Text(
            text = dateFormatted,
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = ResponsiveUtil.normalize(24f)),
            color = if (isDark) Color.White else Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = ResponsiveUtil.verticalScale(8f))
        )

        // Desk Calendar Grid Card (with completely fixed dimensions on a particular device)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            HorizontalPager(
                state = pagerState,
                beyondViewportPageCount = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .testTag("time_ribbon_row")
            ) { index ->
                val monthItem = remember(index) { getYearMonthFromIndex(index) }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(
                            start = ResponsiveUtil.moderateScale(8f),
                            end = ResponsiveUtil.moderateScale(8f),
                            top = ResponsiveUtil.moderateScale(8f),
                            bottom = ResponsiveUtil.moderateScale(4f)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(ResponsiveUtil.verticalScale(40f))
                            .padding(
                                start = ResponsiveUtil.moderateScale(14f),
                                end = ResponsiveUtil.moderateScale(4f)
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = monthItem.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = ResponsiveUtil.normalize(21f)
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                            modifier = Modifier
                                .weight(1f)
                        )

                        val todayMonth = remember { YearMonth.now() }
                        Button(
                            onClick = {
                                if (monthItem != todayMonth && !pagerState.isScrollInProgress && !isProgrammaticScrolling) {
                                    val today = LocalDate.now()
                                    viewModel.selectDate(today)
                                    val targetPage = getMonthIndex(todayMonth)
                                    if (pagerState.currentPage != targetPage) {
                                        isProgrammaticScrolling = true
                                        coroutineScope.launch {
                                            try {
                                                val startPage = pagerState.currentPage
                                                
                                                delay(20) // Yield to let UI state align
                                                
                                                // FEATURE: Cinematic Sequential Page Swiping
                                                // Performs a clean, organic step-by-step sequential page slide
                                                // through all intermediate months back to the target month.
                                                val absDiff = Math.abs(targetPage - startPage)
                                                 val direction = if (targetPage > startPage) 1 else -1
                                                 val adjustedStartPage = if (absDiff > 10) {
                                                     val snapPage = targetPage - (10 * direction)
                                                     pagerState.scrollToPage(snapPage)
                                                     delay(40) // Let layout settle silently
                                                     snapPage
                                                 } else {
                                                     startPage
                                                 }
                                                 val pagesToScroll = mutableListOf<Int>()
                                                if (adjustedStartPage != targetPage) {
                                                    val step = if (targetPage > adjustedStartPage) 1 else -1
                                                    var current = adjustedStartPage + step
                                                    while (current != targetPage) {
                                                        pagesToScroll.add(current)
                                                        current += step
                                                    }
                                                    pagesToScroll.add(targetPage)
                                                }
                                                
                                                for (page in pagesToScroll) {
                                                    pagerState.animateScrollToPage(
                                                        page = page,
                                                        animationSpec = tween(
                                                            durationMillis = 180,
                                                            easing = androidx.compose.animation.core.FastOutSlowInEasing
                                                        )
                                                    )
                                                    delay(45) // Space between swipes so you see each swipe backward or forward step-by-step
                                                }
                                            } catch (e: Exception) {
                                                // Safely ignore paging cancellation
                                            } finally {
                                                lastSettledMonthFromPager = todayMonth
                                                 viewModel.currentYearMonth.value = todayMonth
                                                isProgrammaticScrolling = false
                                            }
                                        }
                                    }
                                }
                            },
                            enabled = (monthItem != todayMonth),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (monthItem != todayMonth) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                                contentColor = if (monthItem != todayMonth) MaterialTheme.colorScheme.onPrimaryContainer else Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                disabledContentColor = Color.Transparent
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .testTag("today_button")
                                .graphicsLayer {
                                    alpha = if (monthItem != todayMonth) 1f else 0f
                                },
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Today,
                                contentDescription = "Go to current month",
                                modifier = Modifier.size(16.dp),
                                tint = if (monthItem != todayMonth) MaterialTheme.colorScheme.onPrimaryContainer else Color.Transparent
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Today",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (monthItem != todayMonth) MaterialTheme.colorScheme.onPrimaryContainer else Color.Transparent
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(ResponsiveUtil.verticalScale(6f)))

                    MonthView(
                        month = monthItem,
                        selectedDate = selectedDate,
                        firstDayOfWeek = firstDayOfWeek,
                        widgetTheme = widgetTheme,
                        onDateSelect = { date -> viewModel.selectDate(date) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun MonthView(
    month: YearMonth,
    selectedDate: LocalDate,
    firstDayOfWeek: Int,
    widgetTheme: com.example.data.WidgetTheme,
    onDateSelect: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val daysInMonth = month.lengthOfMonth()
    val firstOfMonth = month.atDay(1)
    val firstDayOfWeekVal = firstOfMonth.dayOfWeek.value // 1..7 (Mon..Sun)

    val startOffset = if (firstDayOfWeek == 1) {
        firstDayOfWeekVal - 1
    } else {
        if (firstDayOfWeekVal == 7) 0 else firstDayOfWeekVal
    }

    val prevMonth = month.minusMonths(1)
    val prevMonthLength = prevMonth.lengthOfMonth()

    val numRows = 6
    val totalCells = 42
    val dayList = remember(month, firstDayOfWeek) {
        val list = ArrayList<GridDay>()
        // Previous Month trailing days
        for (i in (prevMonthLength - startOffset + 1)..prevMonthLength) {
            list.add(GridDay(i, false, prevMonth.atDay(i)))
        }
        // Current Month days
        for (i in 1..daysInMonth) {
            list.add(GridDay(i, true, month.atDay(i)))
        }
        // Next Month leading days
        val remaining = totalCells - list.size
        val nextMonth = month.plusMonths(1)
        for (i in 1..remaining) {
            list.add(GridDay(i, false, nextMonth.atDay(i)))
        }
        list
    }

    Column(
        modifier = modifier.padding(
            start = ResponsiveUtil.moderateScale(4f),
            end = ResponsiveUtil.moderateScale(4f),
            top = ResponsiveUtil.moderateScale(2f),
            bottom = ResponsiveUtil.moderateScale(0f)
        )
    ) {
        // Week Day Headers: Mon, Tue, Wed, Thu, Fri, Sat, Sun. (Sunday Red)
        val dayHeaders = if (firstDayOfWeek == 1) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        } else {
            listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = ResponsiveUtil.verticalScale(5f))
        ) {
            dayHeaders.forEach { header ->
                val isSunday = header == "Sun"
                Text(
                    text = header,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = ResponsiveUtil.moderateScale(2f)),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = ResponsiveUtil.normalize(13f)
                    ),
                    color = if (isSunday) Color(0xFFFF3B30) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Highly optimized index-based grid layout avoiding heavy loop allocations or repeating system clock queries
        val today = remember { LocalDate.now() }
        for (rowIdx in 0 until numRows) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (colIdx in 0..6) {
                    val flatIdx = rowIdx * 7 + colIdx
                    if (flatIdx < dayList.size) {
                        val cellDay = dayList[flatIdx]
                        val isSelected = cellDay.localDate == selectedDate
                        val isToday = cellDay.localDate == today && cellDay.isCurrentMonth
                        val isSunday = if (firstDayOfWeek == 1) colIdx == 6 else colIdx == 0
                        val themeAccentColor = Color(widgetTheme.primaryColor)

                        DayCell(
                            cellDay = cellDay,
                            isSelected = isSelected,
                            isToday = isToday,
                            isSunday = isSunday,
                            themeAccentColor = themeAccentColor,
                            onDateSelect = onDateSelect,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DayCell(
    cellDay: GridDay,
    isSelected: Boolean,
    isToday: Boolean,
    isSunday: Boolean,
    themeAccentColor: Color,
    onDateSelect: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1.65f) // Increased aspect ratio (reduced height) to shrink the gap between week rows
            .padding(
                horizontal = ResponsiveUtil.moderateScale(1.5f),
                vertical = ResponsiveUtil.moderateScale(0.05f) // Extremely tight vertical padding to reduce gaps
            )
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null, // clean, high-performance click without distracting ripple sizes
                onClick = { onDateSelect(cellDay.localDate) }
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .padding(ResponsiveUtil.moderateScale(0.5f))
                .aspectRatio(1f)
                .clip(CircleShape)
                .background(
                    when {
                        isToday -> Color(0xFFFF3B30)
                        isSelected -> themeAccentColor
                        else -> Color.Transparent
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = cellDay.dayNumber.toString(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = ResponsiveUtil.normalize(17f),
                    fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Medium
                ),
                color = when {
                    isToday -> Color.White
                    isSelected -> Color.White
                    !cellDay.isCurrentMonth -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                    isSunday -> Color(0xFFFF3B30)
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

@Composable
fun YearsView(
    viewModel: CalendarViewModel,
    startMonth: YearMonth,
    onMonthSelect: (YearMonth) -> Unit
) {
    val widgetTheme by viewModel.widgetTheme.collectAsState()
    val themeAccentColor = Color(widgetTheme.primaryColor)
    val currentMonth by viewModel.currentYearMonth.collectAsState()
    val isDark by viewModel.isDarkMode.collectAsState()

    val startYear = startMonth.year
    val yearsRange = remember { (startYear..(startYear + 30)).toList() }

    val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = (currentMonth.year - startYear).coerceIn(0, 30))

    LazyColumn(
        state = lazyListState,
        modifier = Modifier
            .fillMaxSize()
            .testTag("years_list")
    ) {
        items(yearsRange) { year ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = year.toString(),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, fontSize = 18.sp),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                    )

                    // 12 Months in a 4x3 compact layout
                    val chunks = (1..12).chunked(4)
                    chunks.forEach { rowMonths ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowMonths.forEach { monthVal ->
                                val targetMonth = YearMonth.of(year, monthVal)
                                val isCurrent = targetMonth == currentMonth
                                val monthName = targetMonth.format(DateTimeFormatter.ofPattern("MMM", Locale.getDefault()))

                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(34.dp)
                                        .clickable { onMonthSelect(targetMonth) }
                                        .testTag("mini_month_${year}_${monthVal}"),
                                    shape = RoundedCornerShape(6.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isCurrent) themeAccentColor else MaterialTheme.colorScheme.surface
                                    ),
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = if (isCurrent) themeAccentColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                                    )
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = monthName,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                                fontSize = 11.sp
                                            ),
                                            color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomiseTabScreen(viewModel: CalendarViewModel) {
    val context = LocalContext.current
    val widgetTheme by viewModel.widgetTheme.collectAsState()
    val widgetTransparency by viewModel.widgetTransparency.collectAsState()
    val firstDayOfWeek by viewModel.firstDayOfWeek.collectAsState()
    val isDark by viewModel.isDarkMode.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Widget Real-time dynamic preview card
        item {
            var previewMonthOffset by remember { mutableStateOf(0) }
            val previewMonth = remember(previewMonthOffset) {
                YearMonth.now().plusMonths(previewMonthOffset.toLong())
            }
            val previewMonthFormatter = remember { DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault()) }

            Text(
                text = "Live Widget Preview",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Widget Preview Simulator!
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(14.dp)
                ) {
                    // Header row - Centered clean title supporting click-swipe on header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = previewMonth.format(previewMonthFormatter),
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (isDark) Color.White else Color.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Invisible touch targets on the left-half and right-half of the header for swipe-like tap transition
                        Row(modifier = Modifier.matchParentSize()) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { previewMonthOffset-- }
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) { previewMonthOffset++ }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Weekdays headers
                    val headers = if (firstDayOfWeek == 1) {
                        listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    } else {
                        listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                    }

                    Row(modifier = Modifier.fillMaxWidth()) {
                        headers.forEach { header ->
                            val isSun = header == "Sun"
                            Text(
                                text = header,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSun) Color(0xFFFF3B30) else if (isDark) Color(0xFF8E8E93) else Color(0xFF666666)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Dynamic Days Calculation for the Live Widget Simulator
                    val daysInMonth = previewMonth.lengthOfMonth()
                    val firstOfMonth = previewMonth.atDay(1)
                    val firstDayOfWeekVal = firstOfMonth.dayOfWeek.value // 1..7 (Mon..Sun)

                    val startOffset = if (firstDayOfWeek == 1) {
                        firstDayOfWeekVal - 1
                    } else {
                        if (firstDayOfWeekVal == 7) 0 else firstDayOfWeekVal
                    }

                    val prevMonth = previewMonth.minusMonths(1)
                    val prevMonthLength = prevMonth.lengthOfMonth()

                    val totalCells = 42
                    val dayList = remember(previewMonth, firstDayOfWeek) {
                        val list = ArrayList<GridDay>()
                        for (i in (prevMonthLength - startOffset + 1)..prevMonthLength) {
                            list.add(GridDay(i, false, prevMonth.atDay(i)))
                        }
                        for (i in 1..daysInMonth) {
                            list.add(GridDay(i, true, previewMonth.atDay(i)))
                        }
                        val remaining = totalCells - list.size
                        val nextMonth = previewMonth.plusMonths(1)
                        for (i in 1..remaining) {
                            list.add(GridDay(i, false, nextMonth.atDay(i)))
                        }
                        list
                    }

                    val today = remember { LocalDate.now() }
                    for (rowIdx in 0..5) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            for (colIdx in 0..6) {
                                val flatIdx = rowIdx * 7 + colIdx
                                if (flatIdx < dayList.size) {
                                    val cellDay = dayList[flatIdx]
                                    val isToday = cellDay.localDate == today && cellDay.isCurrentMonth
                                    val isSun = if (firstDayOfWeek == 1) colIdx == 6 else colIdx == 0

                                    val themeAccent = Color(widgetTheme.primaryColor)

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1.2f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                if (isToday) themeAccent else Color.Transparent
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = cellDay.dayNumber.toString(),
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontSize = 12.sp,
                                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                            ),
                                            color = when {
                                                isToday -> Color.White
                                                !cellDay.isCurrentMonth -> if (isDark) Color(0xFF444446) else Color(0xFFC7C7CC)
                                                isSun -> Color(0xFFFF3B30)
                                                else -> if (isDark) Color.White else Color(0xFF222222)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Custom high-fidelity Theme Customization presets card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Theme Customization",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "PRO",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Custom horizontal row of picker colors!
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WidgetTheme.values().forEach { themeOption ->
                            val isSelected = widgetTheme == themeOption
                            val primaryCol = Color(themeOption.primaryColor)

                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primaryContainer 
                                        else Color.Transparent
                                    )
                                    .border(
                                        width = if (isSelected) 2.dp else 0.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { viewModel.setWidgetTheme(themeOption) }
                                    .testTag("theme_preset_${themeOption.name.lowercase()}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(primaryCol)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Description label metadata
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = widgetTheme.displayName,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = widgetTheme.description,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // First Day of Week Configuration
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "First Day of Week",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.setFirstDayOfWeek(1) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_start_monday"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (firstDayOfWeek == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "Monday",
                                color = if (firstDayOfWeek == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            )
                        }

                        Button(
                            onClick = { viewModel.setFirstDayOfWeek(7) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_start_sunday"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (firstDayOfWeek == 7) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "Sunday",
                                color = if (firstDayOfWeek == 7) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }
                }
            }
        }

        // Premium Pin layout & Manual installation instructions
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Add Widget to Home Screen",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Customize themes above first. The widget on your home screen will update instantly to match your light/dark preferences, highlight theme colors, and weekday starts!",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (viewModel.isPinWidgetSupported()) {
                        Button(
                            onClick = {
                                try {
                                    viewModel.pinWidget()
                                    Toast.makeText(context, "Request sent! Check your home screen.", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Widget pinning not supported on this device.", Toast.LENGTH_LONG).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_pin_widget"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Pin Widget Instantly", color = Color.White)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Row(
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                "How to Add Manually:",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                "1. Go to your phone's home screen.\n" +
                                "2. Long press on an empty space.\n" +
                                "3. Tap 'Widgets' and scroll to 'Calendar'.\n" +
                                "4. Drag 'Calendar Widget' and place it. It is very resizable! Stretch it to full width for optimum details.",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 16.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DarkLightModeToggle(
    isDark: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val interactionSource = remember { MutableInteractionSource() }

    // Animate the thumb offset (Left inside padding is 4.dp, thumb size is 30.dp, so max offset is 76.dp - 30.dp - 4.dp = 42.dp)
    val thumbOffset by animateDpAsState(
        targetValue = if (isDark) 42.dp else 4.dp,
        animationSpec = spring(
            dampingRatio = 0.72f,
            stiffness = 300f
        ),
        label = "thumb_offset"
    )

    val lightModeProgress by animateFloatAsState(
        targetValue = if (isDark) 0f else 1f,
        animationSpec = tween(durationMillis = 350),
        label = "light_mode_progress"
    )

    val darkModeProgress by animateFloatAsState(
        targetValue = if (isDark) 1f else 0f,
        animationSpec = tween(durationMillis = 350),
        label = "dark_mode_progress"
    )

    // Track Background Color Transition
    val trackColor by animateColorAsState(
        targetValue = if (isDark) Color(0xFF151D35) else Color(0xFFACDEF7),
        animationSpec = tween(durationMillis = 350),
        label = "track_color"
    )

    // Thumb Background Color Transition
    val thumbColor by animateColorAsState(
        targetValue = if (isDark) Color(0xFFE2E8F0) else Color(0xFFFFCC00),
        animationSpec = tween(durationMillis = 350),
        label = "thumb_color"
    )

    // Animated border color for the toggle
    val borderColor by animateColorAsState(
        targetValue = if (isDark) Color(0xFF334155) else Color.Black,
        animationSpec = tween(durationMillis = 350),
        label = "border_color"
    )

    // Outer capsule box (width: 76.dp, height: 38.dp) with premium interaction
    Box(
        modifier = modifier
            .size(width = 76.dp, height = 38.dp)
            .border(1.5.dp, borderColor, RoundedCornerShape(19.dp))
            .clip(RoundedCornerShape(19.dp))
            .background(trackColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,  // No default ripple to keep the crisp tactile feel
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                    onToggle()
                }
            )
            .testTag("dark_mode_toggle")
    ) {
        // --- Day Scenery (Clouds on the right, slides out to the right) ---
        if (lightModeProgress > 0f) {
            // Cloud 1
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset(x = 20.dp * (1f - lightModeProgress) - 6.dp, y = (-6).dp)
                    .graphicsLayer(alpha = lightModeProgress)
                    .size(width = 16.dp, height = 10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color.White.copy(alpha = 0.75f))
            )
            // Cloud 2
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 30.dp * (1f - lightModeProgress) - 12.dp, y = (-5).dp)
                    .graphicsLayer(alpha = lightModeProgress)
                    .size(width = 12.dp, height = 8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White.copy(alpha = 0.65f))
            )
            // Cloud 3
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = (-15).dp * (1f - lightModeProgress) + 16.dp, y = 8.dp)
                    .graphicsLayer(alpha = lightModeProgress)
                    .size(width = 10.dp, height = 6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color.White.copy(alpha = 0.4f))
            )
        }

        // --- Night Scenery (Stars on the left, slides in from the left) ---
        if (darkModeProgress > 0f) {
            // Star 1
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = (-20).dp * (1f - darkModeProgress) + 12.dp, y = 8.dp)
                    .graphicsLayer(alpha = darkModeProgress)
                    .size(3.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
            // Star 2
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-30).dp * (1f - darkModeProgress) + 22.dp, y = (-8).dp)
                    .graphicsLayer(alpha = darkModeProgress)
                    .size(2.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.8f))
            )
            // Star 3
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = (-15).dp * (1f - darkModeProgress) + 8.dp, y = (-4).dp)
                    .graphicsLayer(alpha = darkModeProgress)
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
            // Star 4
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = (-10).dp * (1f - darkModeProgress) + 30.dp, y = 14.dp)
                    .graphicsLayer(alpha = darkModeProgress)
                    .size(1.5.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.5f))
            )
        }

        // --- Concentric Sunbeam / Moonlight Glowing Rings (Moving dynamically with the thumb) ---
        // Ring 1 (Largest, outer aura)
        Box(
            modifier = Modifier
                .offset(x = thumbOffset - 18.dp)
                .align(Alignment.CenterStart)
                .size(66.dp)
                .clip(CircleShape)
                .background(
                    color = if (isDark) {
                        Color.White.copy(alpha = 0.04f * darkModeProgress)
                    } else {
                        Color(0xFFFFF9C4).copy(alpha = 0.12f * lightModeProgress)
                    }
                )
        )
        // Ring 2 (Middle aura)
        Box(
            modifier = Modifier
                .offset(x = thumbOffset - 9.dp)
                .align(Alignment.CenterStart)
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    color = if (isDark) {
                        Color.White.copy(alpha = 0.08f * darkModeProgress)
                    } else {
                        Color(0xFFFFF9C4).copy(alpha = 0.20f * lightModeProgress)
                    }
                )
        )
        // Ring 3 (Inner aura)
        Box(
            modifier = Modifier
                .offset(x = thumbOffset - 4.dp)
                .align(Alignment.CenterStart)
                .size(38.dp)
                .clip(CircleShape)
                .background(
                    color = if (isDark) {
                        Color.White.copy(alpha = 0.12f * darkModeProgress)
                    } else {
                        Color(0xFFFFF9C4).copy(alpha = 0.35f * lightModeProgress)
                    }
                )
        )

        // --- Sliding Thumb (Sun or Moon) ---
        Box(
            modifier = Modifier
                .padding(start = thumbOffset)
                .align(Alignment.CenterStart)
                .size(30.dp)
                .clip(CircleShape)
                .background(thumbColor)
        ) {
            // Moon craters (fade in/out dynamically)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(alpha = darkModeProgress)
            ) {
                // Crater 1
                Box(
                    modifier = Modifier
                        .padding(start = 6.dp, top = 8.dp)
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF94A3B8).copy(alpha = 0.4f))
                )
                // Crater 2
                Box(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 14.dp)
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF94A3B8).copy(alpha = 0.4f))
                )
                // Crater 3
                Box(
                    modifier = Modifier
                        .padding(start = 18.dp, top = 6.dp)
                        .size(5.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF94A3B8).copy(alpha = 0.35f))
                )
            }

            // Sun soft core highlight (fade in/out dynamically)
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFF176).copy(alpha = 0.5f))
                    .align(Alignment.Center)
                    .graphicsLayer(alpha = lightModeProgress)
            )
        }
    }
}
