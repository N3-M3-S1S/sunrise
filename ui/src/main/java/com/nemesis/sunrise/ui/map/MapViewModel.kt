package com.nemesis.sunrise.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nemesis.sunrise.data.location.CoordinatesResult
import com.nemesis.sunrise.data.location.CurrentCoordinatesProvider
import com.nemesis.sunrise.domain.location.Coordinates
import com.nemesis.sunrise.domain.location.usecase.ReduceCoordinateExcessAccuracy
import com.nemesis.sunrise.ui.app.LocationServiceStatus
import com.nemesis.sunrise.ui.di.AppModule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class MapViewModel @Inject constructor(
    private val currentCoordinatesProvider: CurrentCoordinatesProvider,
    @Named(AppModule.locationServiceStatusStateFlow)
    private val locationServiceStatus: StateFlow<@JvmSuppressWildcards LocationServiceStatus>,
    private val reduceCoordinateExcessAccuracy: ReduceCoordinateExcessAccuracy,
) : ViewModel() {
    private val _state = MutableStateFlow(MapState())
    val state: StateFlow<MapState> = _state

    private val _events = MutableSharedFlow<MapEvents>(extraBufferCapacity = 1)
    val mapScreenEvents = _events

    init {
        observeLocationServiceStatus()
    }

    private fun observeLocationServiceStatus() {
        locationServiceStatus.onEach { locationServiceStatus ->
            _state.update {
                it.copy(locationServiceStatus = locationServiceStatus)
            }
        }.launchIn(viewModelScope)
    }

    fun currentLocationButtonClicked() {
        searchCurrentLocation()
    }

    fun locationPermissionGranted() {
        searchCurrentLocation()
    }

    fun coordinatesSelected(latitude: Double, longitude: Double) {
        val coordinates = Coordinates(
            latitude = reduceCoordinateExcessAccuracy(latitude),
            longitude = reduceCoordinateExcessAccuracy(longitude)
        )
        _events.tryEmit(MapEvents.NavigateToAddLocation(coordinates))
    }

    private fun searchCurrentLocation() {
        viewModelScope.launch {
            _state.update { it.copy(isCurrentCoordinatesSearchActive = true) }
            val event = when (val result = currentCoordinatesProvider.requestCurrentCoordinates()) {
                is CoordinatesResult.Success -> MapEvents.MoveMapToCoordinates(result.currentCoordinates)
                is CoordinatesResult.LocationPermissionNotGranted -> MapEvents.AskForLocationPermission(
                    result.requiredPermission
                )
                CoordinatesResult.CurrentCoordinatesNotFound -> MapEvents.ShowCurrentCoordinatesNotFoundMessage
            }
            _state.update { it.copy(isCurrentCoordinatesSearchActive = false) }
            _events.emit(event)
        }
    }
}
