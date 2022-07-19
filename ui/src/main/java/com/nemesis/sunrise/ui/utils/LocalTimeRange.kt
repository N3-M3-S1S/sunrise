package com.nemesis.sunrise.ui.utils

import com.nemesis.sunrise.domain.utils.InstantRange

data class LocalTimeRange(val start: LocalTime, val end: LocalTime)

fun InstantRange.toLocalTimeRange(): LocalTimeRange =
    LocalTimeRange(start.toLocalTime(), end.toLocalTime())
