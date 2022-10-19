package com.nemesis.sunrise.ui.location.calendar

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.nemesis.sunrise.domain.location.Coordinates
import com.nemesis.sunrise.ui.utils.LocalDateInterval
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import kotlinx.datetime.minus
import kotlinx.datetime.plus

class CalendarItemsPagingSource(
    private val coordinates: Coordinates,
    private val dateRange: LocalDateInterval,
    private val calendarItemsFactory: CalendarItemsFactory
) : PagingSource<LocalDate, CalendarItem>() {

    override fun getRefreshKey(state: PagingState<LocalDate, CalendarItem>): LocalDate? = null

    override suspend fun load(params: LoadParams<LocalDate>): LoadResult<LocalDate, CalendarItem> {
        val (from, to) = dateRange

        val startDate = params.key?.let { maxOf(it, from) } ?: from
        val days = minOf(params.loadSize, startDate.daysUntil(to))
        val calendarItems = calendarItemsFactory.createCalendarItems(coordinates, startDate, days)

        val prevKey = if (startDate == from) null else startDate.minus(1, DateTimeUnit.DAY)
        val lastDay = startDate.plus(days, DateTimeUnit.DAY)
        val nextKey =
            if (lastDay == to) null else lastDay.plus(1, DateTimeUnit.DAY)

        return LoadResult.Page(
            data = calendarItems,
            prevKey = prevKey,
            nextKey = nextKey
        )
    }
}
