package com.nemesis.sunrise.domain.location.usecase

import kotlin.math.round

class ReduceCoordinateExcessAccuracy {

    operator fun invoke(coordinate: Double): Double {
        val maximumDecimalAccuracy = 2
        var multiplier = 1.0
        repeat(maximumDecimalAccuracy) { multiplier *= 10 }
        return round(coordinate * multiplier) / multiplier
    }
}
