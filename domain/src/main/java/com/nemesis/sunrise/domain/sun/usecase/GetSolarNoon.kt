package com.nemesis.sunrise.domain.sun.usecase

import com.nemesis.sunrise.domain.location.Coordinates
import com.nemesis.sunrise.domain.sun.SolarEventCalculator
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

class GetSolarNoon(private val solarEventCalculator: SolarEventCalculator) {

    operator fun invoke(coordinates: Coordinates, date: LocalDate): Instant =
        solarEventCalculator.getSolarNoonTime(coordinates, date)
}
