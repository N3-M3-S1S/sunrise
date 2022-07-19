package com.nemesis.sunrise.ui.location.details

import com.nemesis.sunrise.ui.utils.LocalTime
import com.nemesis.sunrise.ui.utils.LocalTimeRange
import kotlinx.datetime.LocalDate
import kotlin.time.Duration

data class LocationDetails(
    val date: LocalDate,
    val dayTime: LocalTimeRange?,
    val solarNoonTime: LocalTime,
    val dayDuration: Duration?,
    val civilTwilight: LocalTimeRange?,
    val nauticalTwilight: LocalTimeRange?,
    val astronomicalTwilight: LocalTimeRange?
)
