package com.nemesis.sunrise.ui.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Instant.toLocalTime(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalTime {
    val localDateTime = this.toLocalDateTime(timeZone)
    return LocalTime(
        hour = localDateTime.hour,
        minute = localDateTime.minute,
        second = localDateTime.second
    )
}
