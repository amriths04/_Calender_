package com.example.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.CalendarViewModel
import com.example.data.GridDay
import com.example.ui.components.buttons.WeekStartToggle
import com.example.util.ResponsiveUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CalendarComponent(
    viewModel: CalendarViewModel,
    modifier: Modifier = Modifier
) {
    val currentMonth by viewModel.currentYearMonth.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val today by viewModel.today.collectAsState()
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
        modifier = modifier
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
                            .height(ResponsiveUtil.verticalScale(33f))
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
                                                    delay(45) // Space between swipes
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
                            Text(
                                text = "Today",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (monthItem != todayMonth) MaterialTheme.colorScheme.onPrimaryContainer else Color.Transparent
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(ResponsiveUtil.verticalScale(1f)))

                    MonthView(
                        month = monthItem,
                        today = today,
                        selectedDate = selectedDate,
                        firstDayOfWeek = firstDayOfWeek,
                        widgetTheme = widgetTheme,
                        onDateSelect = { date -> viewModel.selectDate(date) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        WeekStartToggle(
            firstDayOfWeek = firstDayOfWeek,
            isDark = isDark,
            onDaySelected = { day -> viewModel.setFirstDayOfWeek(day) },
            modifier = Modifier.padding(top = ResponsiveUtil.verticalScale(16f))
        )

        Spacer(modifier = Modifier.weight(1f))

        val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
        val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        
        val annotatedText = buildAnnotatedString {
            append("Developed by Amrith | ")
            
            pushStringAnnotation(tag = "URL", annotation = "https://github.com/amriths04")
            withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                append("GITHUB")
            }
            pop()
        }
        
        Text(
            text = annotatedText,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            ),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = bottomInset + 90.dp)
                .clickable {
                    annotatedText.getStringAnnotations(tag = "URL", start = 0, end = annotatedText.length)
                        .firstOrNull()?.let { annotation ->
                            uriHandler.openUri(annotation.item)
                        }
                }
        )
    }
}

@Composable
fun MonthView(
    month: YearMonth,
    today: LocalDate,
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
        val cellHorizontalPadding = ResponsiveUtil.moderateScale(1.5f)
        val cellVerticalPadding = ResponsiveUtil.moderateScale(0.05f)
        val cellFontSize = ResponsiveUtil.normalize(17f)

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
                            horizontalPadding = cellHorizontalPadding,
                            verticalPadding = cellVerticalPadding,
                            fontSize = cellFontSize,
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
    horizontalPadding: Dp,
    verticalPadding: Dp,
    fontSize: TextUnit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1.65f) // Increased aspect ratio (reduced height) to shrink the gap between week rows
            .padding(
                horizontal = horizontalPadding,
                vertical = verticalPadding
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
                .padding(horizontalPadding / 3)
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
                    fontSize = fontSize,
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
