package com.nemesis.sunrise.ui.location.calendar

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.nemesis.sunrise.domain.location.Location
import com.nemesis.sunrise.domain.sun.usecase.GetDayTime
import com.nemesis.sunrise.ui.utils.LocalDateRange
import com.nemesis.sunrise.ui.utils.toLocalTime
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.minus
import kotlinx.datetime.plus

class CalendarItemsPagingSource(
    private val location: Location,
    private val dateRange: LocalDateRange,
    private val getDayTime: GetDayTime,
) : PagingSource<LocalDate, CalendarItem>() {

    override fun getRefreshKey(state: PagingState<LocalDate, CalendarItem>): LocalDate? = null

    override suspend fun load(params: LoadParams<LocalDate>): LoadResult<LocalDate, CalendarItem> {
        val (from, to) = dateRange

        val startDate = params.key?.let { maxOf(it, from) } ?: from
        val days = minOf(params.loadSize, startDate.daysUntil(to))
        val calendarItems = buildCalendarItems(startDate, days)

        val prevKey = if (startDate == from) null else startDate.minus(1, DateTimeUnit.DAY)
        val lastCalendarItemDate = calendarItems.last().date
        val nextKey =
            if (lastCalendarItemDate == to) null else lastCalendarItemDate.plus(1, DateTimeUnit.DAY)

        return LoadResult.Page(
            data = calendarItems,
            prevKey = prevKey,
            nextKey = nextKey
        )
    }

    private fun buildCalendarItems(startDate: LocalDate, days: Int): List<CalendarItem> =
        buildList {
            val dates = buildDatesList(startDate, days)
            val calendarItems = this

            dates.forEachIndexed { index, date ->
                val dayTime = getDayTime(location.coordinates, date)

                val previousDayTimeDuration = if (index == 0) getDayTime(
                    location.coordinates,
                    date.minus(1, DateTimeUnit.DAY)
                )?.duration else calendarItems[index - 1].dayDuration

                val differenceBetweenPreviousDayTime =
                    if (dayTime?.duration != null && previousDayTimeDuration != null) dayTime.duration - previousDayTimeDuration else null

                calendarItems.add(
                    CalendarItem(
                        date = date,
                        sunrise = dayTime?.start?.toLocalTime(),
                        sunset = dayTime?.end?.toLocalTime(),
                        dayDuration = dayTime?.duration,
                        differenceWithPreviousDayDuration = differenceBetweenPreviousDayTime
                    )
                )
            }
        }

    private fun buildDatesList(startDate: LocalDate, days: Int): List<LocalDate> = buildList {
        add(startDate)
        for (day in 1..days) {
            add(startDate.plus(day, DateTimeUnit.DAY))
        }
    }
}
