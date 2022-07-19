package com.nemesis.sunrise.domain.sun.usecase

import com.nemesis.sunrise.domain.location.Coordinates
import com.nemesis.sunrise.domain.sun.SolarEvent
import com.nemesis.sunrise.domain.sun.SolarEventCalculator
import com.nemesis.sunrise.domain.utils.InstantRange
import kotlinx.datetime.LocalDate

class GetDayTime(private val solarEventCalculator: SolarEventCalculator) {

    operator fun invoke(coordinates: Coordinates, date: LocalDate): InstantRange? =
        solarEventCalculator.getSolarEventInterval(coordinates, date, SolarEvent.DAYTIME)
}
