package com.nemesis.sunrise.ui.location

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.nemesis.sunrise.data.location.DefaultLocationNameStore
import com.nemesis.sunrise.domain.location.Location
import com.nemesis.sunrise.ui.destinations.LocationScreenDestination
import com.nemesis.sunrise.ui.location.calendar.CalendarItem
import com.nemesis.sunrise.ui.location.calendar.CalendarItemsFactory
import com.nemesis.sunrise.ui.location.calendar.CalendarItemsPagingSource
import com.nemesis.sunrise.ui.location.calendar.CalendarState
import com.nemesis.sunrise.ui.location.details.LocationDetailsProvider
import com.nemesis.sunrise.ui.utils.LocalDateFormatter
import com.nemesis.sunrise.ui.utils.LocalDateInterval
import com.nemesis.sunrise.ui.utils.StringInterval
import com.nemesis.sunrise.ui.utils.getCurrentLocalDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationDetailsProvider: LocationDetailsProvider,
    private val defaultLocationNameStore: DefaultLocationNameStore,
    private val localDateFormatter: LocalDateFormatter,
    private val calendarItemsFactory: CalendarItemsFactory,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val detailsDateSavedStateKey = "date"
    private val calendarStartDateSavedStateKey = "start"
    private val calendarEndDateSavedStateKey = "end"

    private val location: Location = LocationScreenDestination.argsFrom(savedStateHandle).location

    private var detailsDate: LocalDate =
        getLocalDateFromSavedStateHandle(detailsDateSavedStateKey) ?: getCurrentLocalDate()

    private var calendarDateInterval: LocalDateInterval = run {
        val startDate =
            getLocalDateFromSavedStateHandle(calendarStartDateSavedStateKey) ?: detailsDate

        val endDate = getLocalDateFromSavedStateHandle(calendarEndDateSavedStateKey)
            ?: startDate.plus(1, DateTimeUnit.YEAR)

        LocalDateInterval(start = startDate, end = endDate)
    }

    private var calendarItemsPagingSource: CalendarItemsPagingSource? = null

    private val _state: MutableStateFlow<LocationState> = MutableStateFlow(LocationState.Loading)
    val state: StateFlow<LocationState> = _state

    private val _events: MutableSharedFlow<LocationEvents> = MutableSharedFlow()
    val events: Flow<LocationEvents> = _events

    init {
        initializeLocationData()
        observeDefaultLocationName()
    }

    private fun getLocalDateFromSavedStateHandle(key: String): LocalDate? =
        savedStateHandle.get<String>(key)?.let(LocalDate.Companion::parse)

    private fun putLocalDateToSavedStateHandle(date: LocalDate, key: String) {
        savedStateHandle[key] = date.toString()
    }

    private fun initializeLocationData() {
        _state.update {
            LocationState.Ready(
                location = location,
                locationSetAsDefault = location.name == defaultLocationNameStore.defaultLocationNameStateFlow.value,
                locationDetailsState = locationDetailsProvider.getLocationDetails(
                    location,
                    detailsDate
                ),
                todayDetailsButtonVisible = selectedDetailsDateIsNotCurrentDate(),
                calendarState = CalendarState(
                    calendarDateInterval = calendarDateInterval,
                    calendarDateIntervalText = formatCalendarDateIntervalToText(),
                    calendarItems = initializeCalendarItemsPagingFlow()
                )
            )
        }
    }

    private fun formatCalendarDateIntervalToText(): StringInterval = StringInterval(
        start = localDateFormatter.formatToString(calendarDateInterval.start),
        end = localDateFormatter.formatToString(calendarDateInterval.end)
    )

    private fun observeDefaultLocationName() {
        viewModelScope.launch {
            defaultLocationNameStore.defaultLocationNameStateFlow.drop(1)
                .collect { defaultLocationName ->
                    val currentState = _state.value
                    if (currentState is LocationState.Ready) {
                        _state.update { currentState.copy(locationSetAsDefault = currentState.location.name == defaultLocationName) }
                    }
                }
        }
    }

    private fun initializeCalendarItemsPagingFlow(): Flow<PagingData<CalendarItem>> = Pager(
        config = PagingConfig(pageSize = 90, enablePlaceholders = false),
        pagingSourceFactory = {
            calendarItemsPagingSource = CalendarItemsPagingSource(
                coordinates = location.coordinates,
                dateRange = calendarDateInterval,
                calendarItemsFactory = calendarItemsFactory
            )
            calendarItemsPagingSource!!
        }
    ).flow.cachedIn(viewModelScope)

    fun toggleLocationDefault() {
        val locationSetAsDefault = (_state.value as LocationState.Ready).locationSetAsDefault

        if (locationSetAsDefault) {
            defaultLocationNameStore.clearDefaultLocationName()
        } else {
            defaultLocationNameStore.setDefaultLocationName(location.name)
        }
    }

    fun showTodayDetails() {
        showDetailsForDate(getCurrentLocalDate())
    }

    fun showDetailsForDate(date: LocalDate) {
        detailsDate = date

        putLocalDateToSavedStateHandle(date, detailsDateSavedStateKey)

        viewModelScope.launch {
            _state.update {
                val readyState = it as LocationState.Ready

                readyState.copy(
                    locationDetailsState = locationDetailsProvider.getLocationDetails(
                        location,
                        detailsDate
                    ),
                    todayDetailsButtonVisible = selectedDetailsDateIsNotCurrentDate()
                )
            }
            _events.emit(LocationEvents.NavigateToDetailsScreen)
        }
    }

    private fun selectedDetailsDateIsNotCurrentDate(): Boolean =
        detailsDate != getCurrentLocalDate()

    fun calendarDateIntervalChanged(dateInterval: LocalDateInterval) {
        calendarDateInterval = dateInterval

        putLocalDateToSavedStateHandle(dateInterval.start, calendarStartDateSavedStateKey)
        putLocalDateToSavedStateHandle(dateInterval.end, calendarEndDateSavedStateKey)

        calendarItemsPagingSource?.invalidate()

        viewModelScope.launch {
            _state.update {
                val readyState = _state.value as LocationState.Ready
                val calendarState = readyState.calendarState.copy(
                    calendarDateInterval = calendarDateInterval,
                    calendarDateIntervalText = formatCalendarDateIntervalToText()
                )
                readyState.copy(calendarState = calendarState)
            }
            _events.emit(LocationEvents.ScrollCalendarListToTop)
        }

    }
}
