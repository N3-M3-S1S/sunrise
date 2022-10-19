package com.nemesis.sunrise.ui.utils

import kotlinx.datetime.LocalDate

data class LocalDateInterval(val start: LocalDate, val end: LocalDate) {
    init {
        assert(start <= end)
    }
}
