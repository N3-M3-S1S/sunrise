package com.nemesis.sunrise.ui.location.calendar

import androidx.paging.PagingData
import com.nemesis.sunrise.ui.utils.LocalDateInterval
import com.nemesis.sunrise.ui.utils.StringInterval
import kotlinx.coroutines.flow.Flow

data class CalendarState(
    val calendarDateInterval: LocalDateInterval,
    val calendarDateIntervalText: StringInterval,
    val calendarItems: Flow<PagingData<CalendarItem>>
)
