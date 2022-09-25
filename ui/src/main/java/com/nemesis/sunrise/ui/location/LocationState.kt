package com.nemesis.sunrise.ui.location

import androidx.paging.PagingData
import com.nemesis.sunrise.domain.location.Location
import com.nemesis.sunrise.ui.location.calendar.CalendarItem
import com.nemesis.sunrise.ui.location.details.LocationDetails
import com.nemesis.sunrise.ui.utils.LocalDateRange
import kotlinx.coroutines.flow.Flow

sealed class LocationState {
    object Loading : LocationState()
    data class Ready(
        val location: Location,
        val locationSetAsDefault: Boolean,
        val locationDetails: LocationDetails,
        val todayDetailsButtonVisible: Boolean,
        val calendarDateRange: LocalDateRange,
        val calendarItems: Flow<PagingData<CalendarItem>>
    ) : LocationState()
}
