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
import com.nemesis.sunrise.domain.sun.usecase.GetDayTime
import com.nemesis.sunrise.ui.destinations.LocationScreenDestination
import com.nemesis.sunrise.ui.location.calendar.CalendarItem
import com.nemesis.sunrise.ui.location.calendar.CalendarItemsPagingSource
import com.nemesis.sunrise.ui.location.details.LocationDetailsStateProvider
import com.nemesis.sunrise.ui.utils.LocalDateRange
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
    private val getDayTime: GetDayTime,
    private val locationDetailsStateProvider: LocationDetailsStateProvider,
    private val defaultLocationNameStore: DefaultLocationNameStore,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val detailsDateSavedStateKey = "date"
    private val calendarFromDateSavedStateKey = "from"
    private val calendarToDateSavedStateKey = "to"

    private val location: Location = LocationScreenDestination.argsFrom(savedStateHandle).location

    private var detailsDate: LocalDate =
        getLocalDateFromSavedStateHandle(detailsDateSavedStateKey) ?: getCurrentLocalDate()

    private var calendarDateRange: LocalDateRange = run {
        val calendarFromDate =
            getLocalDateFromSavedStateHandle(calendarFromDateSavedStateKey) ?: detailsDate

        val calendarToDate = getLocalDateFromSavedStateHandle(calendarToDateSavedStateKey)
            ?: calendarFromDate.plus(1, DateTimeUnit.YEAR)

        LocalDateRange(from = calendarFromDate, to = calendarToDate)
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
                details = locationDetailsStateProvider.getLocationDetails(
                    location,
                    detailsDate
                ),
                calendarDateRange = calendarDateRange,
                calendarItems = initializeCalendarItemsPagingFlow()
            )
        }
    }

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
                location = location,
                dateRange = calendarDateRange,
                getDayTime = getDayTime
            )
            calendarItemsPagingSource!!
        },
    ).flow.cachedIn(viewModelScope)

    fun toggleLocationDefault() {
        val locationSetAsDefault = (_state.value as LocationState.Ready).locationSetAsDefault

        if (locationSetAsDefault) {
            defaultLocationNameStore.clearDefaultLocationName()
        } else {
            defaultLocationNameStore.setDefaultLocationName(location.name)
        }
    }

    fun onDetailsDateSelected(date: LocalDate) {
        detailsDate = date

        putLocalDateToSavedStateHandle(date, detailsDateSavedStateKey)

        viewModelScope.launch {
            _state.update {
                val readyState = it as LocationState.Ready

                readyState.copy(
                    details = locationDetailsStateProvider.getLocationDetails(
                        location,
                        detailsDate
                    )
                )
            }
            _events.emit(LocationEvents.NavigateToDetailsScreen)
        }
    }

    fun onCalendarDateRangeChanged(dateRange: LocalDateRange) {
        calendarDateRange = dateRange

        putLocalDateToSavedStateHandle(dateRange.from, calendarFromDateSavedStateKey)
        putLocalDateToSavedStateHandle(dateRange.to, calendarToDateSavedStateKey)

        calendarItemsPagingSource?.invalidate()

        viewModelScope.launch {
            _state.update {
                val readyState = _state.value as LocationState.Ready
                readyState.copy(calendarDateRange = dateRange)
            }
            _events.emit(LocationEvents.ScrollCalendarListToTop)
        }
    }
}
