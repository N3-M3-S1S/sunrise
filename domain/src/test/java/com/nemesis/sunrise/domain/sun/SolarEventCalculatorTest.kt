package com.nemesis.sunrise.domain.sun

import com.nemesis.sunrise.domain.location.Coordinates
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

/**
 * Compare results of SolarEventCalculator with data from https://voshod-solnca.ru/sun/%D1%81%D0%B0%D0%BD%D0%BA%D1%82-%D0%BF%D0%B5%D1%82%D0%B5%D1%80%D0%B1%D1%83%D1%80%D0%B3
 */
@OptIn(ExperimentalTime::class)
internal class SolarEventCalculatorTest {
    private val coordinates = Coordinates(latitude = 59.894, longitude = 30.264)
    private val date = LocalDate(2022, 7, 6)
    private val timeZone = TimeZone.currentSystemDefault()
    private lateinit var solarEventCalculator: SolarEventCalculator

    @BeforeTest
    fun init() {
        solarEventCalculator = SolarEventCalculator()
    }

    @Test
    fun getSolarNoonTime() {
        val expected = date.atStartOfDayIn(timeZone) + 13.hours + 4.minutes + 45.seconds
        val result = solarEventCalculator.getSolarNoonTime(coordinates, date)
        assertDifferenceBetweenInstantsAcceptable(expected, result)
    }

    @Test
    fun getDayTime() {
        val expectedDayTimeStart = date.atStartOfDayIn(timeZone) + 3.hours + 49.minutes + 12.seconds
        val expectedDayTimeEnd = date.atStartOfDayIn(timeZone) + 22.hours + 20.minutes + 18.seconds
        val result =
            solarEventCalculator.getSolarEventInterval(coordinates, date, SolarEvent.DAYTIME)
        assertNotNull(result)
        assertDifferenceBetweenInstantsAcceptable(expectedDayTimeStart, result.start)
        assertDifferenceBetweenInstantsAcceptable(expectedDayTimeEnd, result.end)
    }

    @Test
    fun getCivilTwilight() {
        val expectedStart = date.atStartOfDayIn(timeZone) + 2.hours + 18.minutes + 58.seconds
        val expectedEnd = date.atStartOfDayIn(timeZone) + 23.hours + 50.minutes + 32.seconds
        val result =
            solarEventCalculator.getSolarEventInterval(coordinates, date, SolarEvent.CIVIL_TWILIGHT)
        assertNotNull(result)
        assertDifferenceBetweenInstantsAcceptable(expectedStart, result.start)
        assertDifferenceBetweenInstantsAcceptable(expectedEnd, result.end)
    }

    @Test
    fun getNauticalTwilight() {
        val result = solarEventCalculator.getSolarEventInterval(
            coordinates,
            date,
            SolarEvent.NAUTICAL_TWILIGHT
        )
        assertNull(result)
    }

    @Test
    fun getAstronomicalTwilight() {
        val result = solarEventCalculator.getSolarEventInterval(
            coordinates,
            date,
            SolarEvent.ASTRONOMICAL_TWILIGHT
        )
        assertNull(result)
    }

    private fun assertDifferenceBetweenInstantsAcceptable(instant1: Instant, instant2: Instant) {
        val acceptableDifference = 3.minutes
        assertTrue(
            (instant1 - instant2).absoluteValue <= acceptableDifference,
            message = "Instant1: '$instant1', Instant2: '$instant2'"
        )
    }
}
