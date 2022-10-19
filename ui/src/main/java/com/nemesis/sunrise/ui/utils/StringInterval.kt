package com.nemesis.sunrise.ui.utils

import com.nemesis.sunrise.domain.utils.InstantInterval
import kotlinx.datetime.TimeZone

data class StringInterval(val start: String, val end: String)

fun InstantInterval.formatToLocalTimeStringRange(
    localTimeFormatter: LocalTimeFormatter,
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): StringInterval {
    val startString = localTimeFormatter.formatToString(start.toLocalTime(timeZone))
    val endString = localTimeFormatter.formatToString(end.toLocalTime(timeZone))
    return StringInterval(start = startString, end = endString)
}
