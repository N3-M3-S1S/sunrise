package com.nemesis.sunrise.domain.utils

import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

data class InstantRange(val start: Instant, val end: Instant) {
    @OptIn(ExperimentalTime::class)
    val duration = end - start

    init {
        assert(start <= end)
    }
}
