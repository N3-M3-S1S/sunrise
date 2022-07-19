package com.nemesis.sunrise.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nemesis.sunrise.ui.R
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class LocalTime(val hour: Int, val minute: Int, val second: Int) {
    init {
        assert(hour in 0..23)
        assert(minute in 0..59)
        assert(second in 0..59)
    }

    override fun toString(): String {
        fun timeUnitToString(timeUnit: Int): String =
            if (timeUnit < 10) "0$timeUnit" else timeUnit.toString()
        return "${timeUnitToString(hour)}:${timeUnitToString(minute)}:${timeUnitToString(second)}"
    }
}

fun Instant.toLocalTime(timeZone: TimeZone = TimeZone.currentSystemDefault()): LocalTime {
    val localDateTime = this.toLocalDateTime(timeZone)
    return LocalTime(
        hour = localDateTime.hour,
        minute = localDateTime.minute,
        second = localDateTime.second
    )
}

@Composable
fun LocalTime?.formatToString(): String =
    this?.toString() ?: stringResource(id = R.string.not_available)
