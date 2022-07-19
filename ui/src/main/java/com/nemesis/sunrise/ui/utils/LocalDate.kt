package com.nemesis.sunrise.ui.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun getCurrentLocalDate(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalDate =
    Clock.System.now().toLocalDateTime(timeZone).date

fun LocalDate.formatToString(): String = buildString {
    if (dayOfMonth < 10) {
        append("0")
    }
    append(dayOfMonth)
    append(".")
    if (monthNumber < 10) {
        append("0")
    }
    append(monthNumber)
    append(".")
    append(year)
}
