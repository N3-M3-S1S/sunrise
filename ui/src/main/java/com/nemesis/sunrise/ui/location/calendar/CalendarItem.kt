package com.nemesis.sunrise.ui.location.calendar

import androidx.compose.ui.graphics.Color
import com.nemesis.sunrise.ui.utils.StringInterval
import kotlinx.datetime.LocalDate

data class CalendarItem(
    val date: LocalDate,
    val day: String,
    val dayTime: StringInterval?,
    val dayDuration: String?,
    val differenceWithPreviousDayDuration: String?,
    val differenceTextColor: Color?
)
