package com.nemesis.sunrise.ui.utils

import kotlinx.datetime.LocalDate

data class LocalDateRange(val from: LocalDate, val to: LocalDate) {
    init {
        assert(from <= to)
    }
}
