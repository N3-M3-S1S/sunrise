@file:OptIn(ExperimentalTime::class)

package com.nemesis.sunrise.domain.sun

import com.nemesis.sunrise.domain.location.Coordinates
import com.nemesis.sunrise.domain.utils.Degrees
import com.nemesis.sunrise.domain.utils.InstantInterval
import com.nemesis.sunrise.domain.utils.Radians
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import java.time.Year
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.ExperimentalTime

/**
 * Calculates various solar events using data from https://gml.noaa.gov/grad/solcalc/solareqns.PDF
 */
class SolarEventCalculator {

    internal fun getSolarNoonTime(
        coordinates: Coordinates,
        date: LocalDate
    ): Instant {
        val fractionalYear = getFractionalYear(date)
        val equationOfTime = getEquationOfTime(fractionalYear)
        return getUTCTime(
            date,
            (720 - (4 * coordinates.longitude)).minutes - equationOfTime
        )
    }

    internal fun getSolarEventInterval(
        coordinates: Coordinates,
        date: LocalDate,
        solarEvent: SolarEvent
    ): InstantInterval? {
        val fractionalYear = getFractionalYear(date)
        val equationOfTime = getEquationOfTime(fractionalYear)
        val solarDeclination = getSolarDeclination(fractionalYear)
        val solarEventZenithDistance = getZenithDistanceForSolarEvent(solarEvent)

        val solarHourAngle =
            getSolarHourAngle(
                solarDeclination,
                Radians.fromDegrees(coordinates.latitude),
                solarEventZenithDistance
            )

        if (solarHourAngle.value.isNaN()) return null

        val sunEventStartInterval =
            getSunEventUTCTime(date, Degrees(coordinates.longitude), solarHourAngle, equationOfTime)

        val sunEventEndInterval =
            getSunEventUTCTime(
                date,
                Degrees(coordinates.longitude),
                Degrees(-solarHourAngle.value),
                equationOfTime
            )

        return InstantInterval(sunEventStartInterval, sunEventEndInterval)
    }

    private fun getZenithDistanceForSolarEvent(solarEvent: SolarEvent): Radians {
        val zenithDistanceDegrees = when (solarEvent) {
            SolarEvent.DAYTIME -> 90.833
            SolarEvent.CIVIL_TWILIGHT -> 96.0
            SolarEvent.NAUTICAL_TWILIGHT -> 102.0
            SolarEvent.ASTRONOMICAL_TWILIGHT -> 108.0
        }
        return Radians.fromDegrees(zenithDistanceDegrees)
    }

    private fun getSolarDeclination(fractionalYear: Radians): Radians {
        return Radians(
            (
                0.006918 - 0.399912 * cos(fractionalYear.value) + 0.070257 * sin(fractionalYear.value) - 0.006758 * cos(
                    2 * fractionalYear.value
                ) + 0.000907 * sin(2 * fractionalYear.value) - 0.002697 * cos(3 * fractionalYear.value) + 0.00148 * sin(
                    3 * fractionalYear.value
                )
                )
        )
    }

    private fun getEquationOfTime(fractionalYear: Radians): Duration {
        return (
            229.18 * (
                0.000075 + 0.001868 * cos(fractionalYear.value) - 0.032077 *
                    sin(fractionalYear.value) - 0.014615 * cos(2 * fractionalYear.value) - 0.040849 *
                    sin(2 * fractionalYear.value)
                )
            ).minutes
    }

    private fun getSolarHourAngle(
        solarDeclination: Radians,
        latitudeRadians: Radians,
        zenithDistance: Radians
    ): Degrees {
        val x =
            cos(zenithDistance.value) / (
                cos(latitudeRadians.value) *
                    cos(solarDeclination.value)
                ) - tan(latitudeRadians.value) * tan(
                solarDeclination.value
            )

        return Degrees(Math.toDegrees(acos(x)))
    }

    private fun getFractionalYear(date: LocalDate): Radians {
        val daysInYear = if (Year.isLeap(date.year.toLong())) 366 else 365
        return Radians(date.dayOfYear * (PI * 2 / daysInYear))
    }

    private fun getSunEventUTCTime(
        date: LocalDate,
        longitude: Degrees,
        solarHourAngle: Degrees,
        equationOfTime: Duration
    ): Instant = getUTCTime(
        date,
        (720 - 4 * (longitude.value + solarHourAngle.value)).minutes - equationOfTime
    )

    private fun getUTCTime(date: LocalDate, durationSinceDayStart: Duration): Instant =
        date.atStartOfDayIn(TimeZone.UTC) + durationSinceDayStart
}
