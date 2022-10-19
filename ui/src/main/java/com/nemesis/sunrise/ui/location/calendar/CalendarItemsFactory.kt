package com.nemesis.sunrise.ui.location.calendar

import androidx.compose.ui.graphics.Color
import com.nemesis.sunrise.domain.location.Coordinates
import com.nemesis.sunrise.domain.sun.usecase.GetDayTime
import com.nemesis.sunrise.ui.theme.Red
import com.nemesis.sunrise.ui.utils.LocalTimeFormatter
import com.nemesis.sunrise.ui.utils.buildTimeString
import com.nemesis.sunrise.ui.utils.formatToLocalTimeStringRange
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import java.time.format.TextStyle
import java.util.*
import javax.inject.Inject
import kotlin.time.Duration

class CalendarItemsFactory @Inject constructor(
    private val getDayTime: GetDayTime,
    private val localTimeFormatter: LocalTimeFormatter
) {

    fun createCalendarItems(
        coordinates: Coordinates,
        startDate: LocalDate,
        days: Int
    ): List<CalendarItem> = List(days + 1) {
        val date = if (it > 0) startDate.plus(it, DateTimeUnit.DAY) else startDate

        val dayTime = getDayTime(coordinates, date)

        val previousDayTimeDuration =
            getDayTime(coordinates, date.minus(1, DateTimeUnit.DAY))?.duration

        val differenceBetweenPreviousDayTime =
            if (dayTime?.duration != null && previousDayTimeDuration != null) dayTime.duration - previousDayTimeDuration else null

        CalendarItem(
            date = date,
            day = formatDateToString(date),
            dayTime = dayTime?.formatToLocalTimeStringRange(localTimeFormatter),
            dayDuration = dayTime?.duration?.let(::formatDayDurationToString),
            differenceWithPreviousDayDuration = differenceBetweenPreviousDayTime?.let(::formatDayDurationDifferenceToString),
            differenceTextColor = differenceBetweenPreviousDayTime?.let(::getDayDurationDifferenceColor)
        )
    }

    private fun formatDateToString(date: LocalDate): String {
        val monthShortName = date.month.getDisplayName(
            TextStyle.SHORT,
            Locale.getDefault()
        )
        return "${date.dayOfMonth} $monthShortName"
    }

    private fun formatDayDurationToString(dayDuration: Duration): String =
        dayDuration.toComponents { hours, minutes, seconds, _ ->
            buildTimeString(hours = hours.toInt(), minutes = minutes, seconds = seconds)
        }

    private fun formatDayDurationDifferenceToString(difference: Duration): String {
        val differenceSign = when {
            difference == Duration.ZERO -> ""
            difference.isPositive() -> "+"
            else -> "-"
        }
        return difference.absoluteValue.toComponents { minutes, seconds, _ ->
            "$differenceSign ${
            buildTimeString(
                minutes = minutes.toInt(),
                seconds = seconds
            )
            }"
        }
    }

    private fun getDayDurationDifferenceColor(difference: Duration): Color? = when {
        difference == Duration.ZERO -> null
        difference.isPositive() -> Color.Green
        else -> Red
    }
}
