package com.nemesis.sunrise.ui.location.details

import com.nemesis.sunrise.domain.location.Location
import com.nemesis.sunrise.domain.sun.usecase.GetDayTime
import com.nemesis.sunrise.domain.sun.usecase.GetSolarNoon
import com.nemesis.sunrise.domain.sun.usecase.GetTwilights
import com.nemesis.sunrise.ui.utils.toLocalTime
import com.nemesis.sunrise.ui.utils.toLocalTimeRange
import kotlinx.datetime.LocalDate
import javax.inject.Inject

class LocationDetailsProvider @Inject constructor(
    private val getDayTime: GetDayTime,
    private val getSolarNoon: GetSolarNoon,
    private val getTwilights: GetTwilights
) {
    fun getLocationDetails(
        location: Location,
        date: LocalDate
    ): LocationDetails {
        val coordinates = location.coordinates
        val dayTime = getDayTime(coordinates, date)
        val dayDuration = dayTime?.duration
        val solarNoon = getSolarNoon(coordinates, date)
        val (civilTwilight, nauticalTwilight, astronomicalTwilight) = getTwilights(
            coordinates,
            date
        )
        return LocationDetails(
            date = date,
            dayTime = dayTime?.toLocalTimeRange(),
            dayDuration = dayDuration,
            solarNoonTime = solarNoon.toLocalTime(),
            civilTwilight = civilTwilight?.toLocalTimeRange(),
            nauticalTwilight = nauticalTwilight?.toLocalTimeRange(),
            astronomicalTwilight = astronomicalTwilight?.toLocalTimeRange()
        )
    }
}
