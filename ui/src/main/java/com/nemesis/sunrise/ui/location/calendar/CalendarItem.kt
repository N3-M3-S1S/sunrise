package com.nemesis.sunrise.ui.location.calendar

import com.nemesis.sunrise.ui.utils.LocalTime
import kotlinx.datetime.LocalDate
import kotlin.time.Duration

data class CalendarItem(
    val date: LocalDate,
    val sunrise: LocalTime?,
    val sunset: LocalTime?,
    val dayDuration: Duration?,
    val differenceWithPreviousDayDuration: Duration?
)
