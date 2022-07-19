package com.nemesis.sunrise.domain.sun.usecase

import com.nemesis.sunrise.domain.location.Coordinates
import com.nemesis.sunrise.domain.sun.SolarEvent
import com.nemesis.sunrise.domain.sun.SolarEventCalculator
import com.nemesis.sunrise.domain.sun.Twilights
import kotlinx.datetime.LocalDate

class GetTwilights(private val solarEventCalculator: SolarEventCalculator) {

    operator fun invoke(coordinates: Coordinates, date: LocalDate): Twilights {
        val civilTwilight = solarEventCalculator.getSolarEventInterval(
            coordinates,
            date,
            SolarEvent.CIVIL_TWILIGHT
        )
        val nauticalTwilight = solarEventCalculator.getSolarEventInterval(
            coordinates,
            date,
            SolarEvent.NAUTICAL_TWILIGHT
        )
        val astronomicalTwilight = solarEventCalculator.getSolarEventInterval(
            coordinates,
            date,
            SolarEvent.ASTRONOMICAL_TWILIGHT
        )
        return Twilights(
            civilTwilight = civilTwilight,
            nauticalTwilight = nauticalTwilight,
            astronomicalTwilight = astronomicalTwilight
        )
    }
}
