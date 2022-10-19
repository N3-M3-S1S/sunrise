package com.nemesis.sunrise.domain.utils

import kotlinx.datetime.Instant

data class InstantInterval(val start: Instant, val end: Instant) {
    val duration = end - start

    init {
        assert(start <= end)
    }
}
