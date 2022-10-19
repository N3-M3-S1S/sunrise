package com.nemesis.sunrise.ui.location

import com.nemesis.sunrise.domain.location.Location
import com.nemesis.sunrise.ui.location.calendar.CalendarState
import com.nemesis.sunrise.ui.location.details.LocationDetailsState

sealed class LocationState {
    object Loading : LocationState()
    data class Ready(
        val location: Location,
        val locationSetAsDefault: Boolean,
        val todayDetailsButtonVisible: Boolean,
        val locationDetailsState: LocationDetailsState,
        val calendarState: CalendarState
    ) : LocationState()
}
