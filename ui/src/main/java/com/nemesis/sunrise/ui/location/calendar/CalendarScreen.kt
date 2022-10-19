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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.nemesis.sunrise.ui.R
import com.nemesis.sunrise.ui.location.LocationEvents
import com.nemesis.sunrise.ui.utils.LocalDateInterval
import com.nemesis.sunrise.ui.utils.StringInterval
import com.nemesis.sunrise.ui.utils.notAvailableString
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Composable
fun CalendarScreen(
    state: CalendarState,
    onDateSelected: (LocalDate) -> Unit,
    onDateIntervalChanged: (LocalDateInterval) -> Unit,
    scrollCalendarEventFlow: Flow<LocationEvents.ScrollCalendarListToTop>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        CalendarList(
            modifier = Modifier.weight(1f),
            calendarItems = state.calendarItems.collectAsLazyPagingItems(),
            scrollCalendarEventFlow = scrollCalendarEventFlow,
            onItemClicked = { onDateSelected(it.date) }
        )
        DateIntervalPicker(
            dateIntervalText = state.calendarDateIntervalText,
            dateInterval = state.calendarDateInterval,
            onDateIntervalChanged = onDateIntervalChanged
        )
    }
}

@Composable
private fun CalendarList(
    calendarItems: LazyPagingItems<CalendarItem>,
    onItemClicked: (CalendarItem) -> Unit,
    scrollCalendarEventFlow: Flow<LocationEvents.ScrollCalendarListToTop>,
    modifier: Modifier = Modifier
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
                            Text(text = calendarItem.day)
                        },
                        sunrise = {
                            Text(text = calendarItem.dayTime?.start ?: notAvailableString())
                        },
                        sunset = {
                            Text(text = calendarItem.dayTime?.end ?: notAvailableString())
                        },
                        dayDuration = {
                            Text(text = calendarItem.dayDuration ?: notAvailableString())
                        },
                        difference = {
                            val text = calendarItem.differenceWithPreviousDayDuration
                                ?: notAvailableString()
                            val textColor =
                                calendarItem.differenceTextColor ?: LocalTextStyle.current.color
                            Text(text = text, color = textColor)
                        }
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
                contentDescription = ""
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
                contentDescription = ""
            )
        },
        dayDuration = {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = ""
            )
        },
        difference = {
            Icon(
                imageVector = Icons.Default.Exposure,
                contentDescription = ""
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
private fun DateIntervalPicker(
    dateIntervalText: StringInterval,
    dateInterval: LocalDateInterval,
    onDateIntervalChanged: (LocalDateInterval) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        DatePicker(
            title = stringResource(id = R.string.daterange_from),
            text = dateIntervalText.start,
            initialDate = dateInterval.start,
            isDateAvailableForSelection = { it <= dateInterval.end },
            onDateSelected = {
                onDateIntervalChanged(dateInterval.copy(start = it))
            }
        )

        Divider(
            modifier = Modifier.width(40.dp)
        )

        DatePicker(
            title = stringResource(id = R.string.daterange_to),
            text = dateIntervalText.end,
            initialDate = dateInterval.end,
            isDateAvailableForSelection = { it >= dateInterval.start },
            onDateSelected = {
                onDateIntervalChanged(dateInterval.copy(end = it))
            }
        )
    }
}

@Composable
private fun DatePicker(
    title: String,
    text: String,
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
            Text(text)
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
