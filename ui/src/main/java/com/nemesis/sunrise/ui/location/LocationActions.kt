package com.nemesis.sunrise.ui.location

import com.nemesis.sunrise.ui.utils.LocalDateInterval
import kotlinx.datetime.LocalDate

data class LocationActions(
    val onBackClicked: () -> Unit,
    val onDefaultLocationButtonClicked: () -> Unit,
    val onDateSelected: (LocalDate) -> Unit,
    val onCalendarDateRangeChanged: (LocalDateInterval) -> Unit,
    val onTodayDetailsButtonClicked: () -> Unit
)
