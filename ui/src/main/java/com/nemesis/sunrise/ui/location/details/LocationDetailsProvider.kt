package com.nemesis.sunrise.ui.location.details

import com.nemesis.sunrise.domain.location.Location
import com.nemesis.sunrise.domain.sun.usecase.GetDayTime
import com.nemesis.sunrise.domain.sun.usecase.GetSolarNoon
import com.nemesis.sunrise.domain.sun.usecase.GetTwilights
import com.nemesis.sunrise.ui.utils.LocalDateFormatter
import com.nemesis.sunrise.ui.utils.LocalTimeFormatter
import com.nemesis.sunrise.ui.utils.buildTimeString
import com.nemesis.sunrise.ui.utils.formatToLocalTimeStringRange
import com.nemesis.sunrise.ui.utils.toLocalTime
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import java.time.format.TextStyle
import java.util.*
import javax.inject.Inject
import kotlin.time.Duration

class LocationDetailsProvider @Inject constructor(
    private val getDayTime: GetDayTime,
    private val getSolarNoon: GetSolarNoon,
    private val getTwilights: GetTwilights,
    private val localDateFormatter: LocalDateFormatter,
    private val localTimeFormatter: LocalTimeFormatter
) {

    fun getLocationDetails(
        location: Location,
        date: LocalDate
    ): LocationDetailsState {
        val coordinates = location.coordinates

        val dayTime = getDayTime(coordinates, date)
        val dayDuration = dayTime?.duration
        val solarNoon = getSolarNoon(coordinates, date).toLocalTime()

        val (civilTwilight, nauticalTwilight, astronomicalTwilight) = getTwilights(
            coordinates,
            date
        )

        return LocationDetailsState(
            date = localDateFormatter.formatToString(date),
            dayOfWeek = formatDayOfWeekToString(date.dayOfWeek),
            dayTime = dayTime?.formatToLocalTimeStringRange(localTimeFormatter),
            dayDuration = dayDuration?.let(::formatDayDurationToString),
            zenith = localTimeFormatter.formatToString(solarNoon),
            civilTwilight = civilTwilight?.formatToLocalTimeStringRange(localTimeFormatter),
            nauticalTwilight = nauticalTwilight?.formatToLocalTimeStringRange(localTimeFormatter),
            astronomicalTwilight = astronomicalTwilight?.formatToLocalTimeStringRange(
                localTimeFormatter
            )
        )
    }

    private fun formatDayOfWeekToString(dayOfWeek: DayOfWeek): String = dayOfWeek.getDisplayName(
        TextStyle.FULL,
        Locale.getDefault()
    )

    private fun formatDayDurationToString(dayDuration: Duration): String =
        dayDuration.toComponents { hours, minutes, seconds, _ ->
            buildTimeString(hours = hours.toInt(), minutes = minutes, seconds = seconds)
        }
}
