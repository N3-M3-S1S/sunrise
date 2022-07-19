package com.nemesis.sunrise.ui.location.calendar

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Exposure
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.nemesis.sunrise.ui.R
import com.nemesis.sunrise.ui.location.LocationEvents
import com.nemesis.sunrise.ui.theme.Red
import com.nemesis.sunrise.ui.utils.LocalDateRange
import com.nemesis.sunrise.ui.utils.buildTimeString
import com.nemesis.sunrise.ui.utils.formatToString
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import java.time.format.TextStyle
import java.util.Locale
import kotlin.time.Duration

@Composable
fun CalendarScreen(
    calendarItems: Flow<PagingData<CalendarItem>>,
    onDateSelected: (LocalDate) -> Unit,
    calendarDateRange: LocalDateRange,
    onDateRangeChanged: (LocalDateRange) -> Unit,
    scrollCalendarEventFlow: Flow<LocationEvents.ScrollCalendarListToTop>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        CalendarList(
            modifier = Modifier.weight(1f),
            calendarItems = calendarItems.collectAsLazyPagingItems(),
            scrollCalendarEventFlow = scrollCalendarEventFlow,
            onItemClicked = { onDateSelected(it.date) }
        )
        DateRangePicker(
            initialDateRange = calendarDateRange,
            onDateRangeChanged = onDateRangeChanged
        )
    }
}

@Composable
private fun CalendarList(
    calendarItems: LazyPagingItems<CalendarItem>,
    onItemClicked: (CalendarItem) -> Unit,
    scrollCalendarEventFlow: Flow<LocationEvents.ScrollCalendarListToTop>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        CalendarListHeader()

        val listState = rememberLazyListState()
        LaunchedEffect(true) {
            scrollCalendarEventFlow.collect {
                listState.scrollToItem(0)
            }
        }

        LazyColumn(state = listState) {
            this.itemsIndexed(items = calendarItems) { index, calendarItem ->
                calendarItem!!

                val yearChanged =
                    index != 0 && calendarItem.date.year != calendarItems[index - 1]!!.date.year
                if (yearChanged) {
                    YearHeader(year = calendarItem.date.year)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyMedium) {
                    CalendarRow(
                        modifier = Modifier.height(48.dp),
                        day = {
                            DayText(
                                day = calendarItem.date.dayOfMonth,
                                month = calendarItem.date.month
                            )
                        },
                        sunrise = {
                            Text(text = calendarItem.sunrise.formatToString())
                        },
                        sunset = {
                            Text(text = calendarItem.sunset.formatToString())
                        },
                        dayDuration = {
                            DayDurationText(dayDuration = calendarItem.dayDuration)
                        },
                        difference = {
                            DifferenceText(differenceWithPreviousDayDuration = calendarItem.differenceWithPreviousDayDuration)
                        },
                    ) { onItemClicked(calendarItem) }
                }
            }
        }
    }
}

@Composable
private fun CalendarListHeader(modifier: Modifier = Modifier) {
    CalendarRow(
        day = {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = "",
            )
        },
        sunrise = {
            Icon(
                painter = painterResource(id = R.drawable.ic_sunrise),
                contentDescription = ""
            )
        },
        sunset = {
            Icon(
                painter = painterResource(id = R.drawable.ic_sunset),
                contentDescription = "",
            )
        },
        dayDuration = {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = "",
            )
        },
        difference = {
            Icon(
                imageVector = Icons.Default.Exposure,
                contentDescription = "",
            )
        },
        modifier = modifier
    )
}

@Composable
private fun CalendarRow(
    day: @Composable () -> Unit,
    sunrise: @Composable () -> Unit,
    sunset: @Composable () -> Unit,
    dayDuration: @Composable () -> Unit,
    difference: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .clickable(enabled = onClick != null) { onClick!!.invoke() },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CalendarRowItem(modifier = Modifier.weight(0.2f)) {
            day()
        }
        CalendarRowItem(modifier = Modifier.weight(0.2f)) {
            sunrise()
        }
        CalendarRowItem(modifier = Modifier.weight(0.2f)) {
            sunset()
        }
        CalendarRowItem(modifier = Modifier.weight(0.2f)) {
            dayDuration()
        }
        CalendarRowItem(modifier = Modifier.weight(0.2f)) {
            difference()
        }
    }
}

@Composable
private fun CalendarRowItem(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun YearHeader(year: Int, modifier: Modifier = Modifier) {
    Text(modifier = modifier.fillMaxWidth(), text = year.toString(), textAlign = TextAlign.Center)
}

@Composable
private fun DayText(day: Int, month: Month, modifier: Modifier = Modifier) {
    val monthShortName = month.getDisplayName(
        TextStyle.SHORT,
        Locale.getDefault()
    )
    Text(modifier = modifier, text = "$day $monthShortName")
}

@Composable
private fun DayDurationText(dayDuration: Duration?, modifier: Modifier = Modifier) {
    val dayDurationText = dayDuration?.toComponents { hours, minutes, seconds, _ ->
        buildTimeString(
            hours = hours.toInt(),
            minutes = minutes,
            seconds = seconds
        )
    } ?: stringResource(id = R.string.not_available)
    Text(modifier = modifier, text = dayDurationText)
}

@Composable
private fun DifferenceText(
    differenceWithPreviousDayDuration: Duration?,
    modifier: Modifier = Modifier
) {
    if (differenceWithPreviousDayDuration != null) {
        val (textColor, sign) = when {
            differenceWithPreviousDayDuration.isNegative() -> Red to "−"
            differenceWithPreviousDayDuration.isPositive() -> Color.Green to "+"
            else -> LocalTextStyle.current.color to ""
        }

        val differenceText =
            differenceWithPreviousDayDuration.absoluteValue.toComponents { minutes, seconds, _ ->
                "$sign ${buildTimeString(minutes = minutes.toInt(), seconds = seconds)}"
            }

        Text(
            text = differenceText,
            modifier = modifier,
            color = textColor,
            textAlign = TextAlign.Center
        )
    } else {
        Text(text = stringResource(id = R.string.not_available), modifier = modifier)
    }
}

@Composable
private fun DateRangePicker(
    initialDateRange: LocalDateRange,
    onDateRangeChanged: (LocalDateRange) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        DatePicker(
            title = stringResource(id = R.string.daterange_from),
            initialDate = initialDateRange.from,
            isDateAvailableForSelection = { it <= initialDateRange.to },
            onDateSelected = {
                onDateRangeChanged(initialDateRange.copy(from = it))
            }
        )

        Divider(
            modifier = Modifier.width(40.dp)
        )

        DatePicker(
            title = stringResource(id = R.string.daterange_to),
            initialDate = initialDateRange.to,
            isDateAvailableForSelection = { it >= initialDateRange.from },
            onDateSelected = {
                onDateRangeChanged(initialDateRange.copy(to = it))
            }
        )
    }
}

@Composable
private fun DatePicker(
    title: String,
    initialDate: LocalDate,
    isDateAvailableForSelection: (LocalDate) -> Boolean,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val datePickerDialogState = rememberMaterialDialogState()
    Box(modifier = modifier.clickable { datePickerDialogState.show() }) {
        DatePickerDialog(
            dialogState = datePickerDialogState,
            title = title,
            initialDate = initialDate,
            isDateAvailableForSelection = isDateAvailableForSelection,
            onDateSelected = onDateSelected
        )

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .border(
                    border = ButtonDefaults.outlinedButtonBorder,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(8.dp)
        ) {
            Text(remember(initialDate) { initialDate.formatToString() })
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = "",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun DatePickerDialog(
    dialogState: MaterialDialogState,
    title: String,
    initialDate: LocalDate,
    isDateAvailableForSelection: (LocalDate) -> Boolean,
    onDateSelected: (LocalDate) -> Unit
) {
    var selectedDate = initialDate

    MaterialDialog(
        dialogState = dialogState,
        backgroundColor = MaterialTheme.colorScheme.surface,
        buttons = {
            val buttonTextStyle =
                androidx.compose.material.MaterialTheme.typography.button.copy(color = MaterialTheme.colorScheme.primary)
            negativeButton(
                res = android.R.string.cancel,
                textStyle = buttonTextStyle
            ) {
                dialogState.hide()
            }
            positiveButton(
                res = android.R.string.ok,
                textStyle = buttonTextStyle
            ) {
                onDateSelected(selectedDate)
                dialogState.hide()
            }
        }
    ) {
        datepicker(
            title = title,
            initialDate = initialDate,
            allowedDateValidator = isDateAvailableForSelection,
            colors = DatePickerDefaults.colors(
                dateInactiveTextColor = MaterialTheme.colorScheme.onSurface,
                dateActiveBackgroundColor = MaterialTheme.colorScheme.primary,
                dateActiveTextColor = MaterialTheme.colorScheme.onPrimary,
                calendarHeaderTextColor = MaterialTheme.colorScheme.onSurface,
                headerBackgroundColor = MaterialTheme.colorScheme.surface,
                headerTextColor = MaterialTheme.colorScheme.onSurface
            ),
            onDateChange = {
                selectedDate = it
            }
        )
    }
}