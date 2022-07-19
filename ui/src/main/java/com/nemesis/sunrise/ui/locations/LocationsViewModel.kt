package com.nemesis.sunrise.ui.locations

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nemesis.sunrise.data.location.CoordinatesResult
import com.nemesis.sunrise.data.location.CurrentCoordinatesProvider
import com.nemesis.sunrise.domain.location.Location
import com.nemesis.sunrise.domain.location.usecase.DeleteLocations
import com.nemesis.sunrise.domain.location.usecase.GetLocations
import com.nemesis.sunrise.ui.app.LocationServiceStatus
import com.nemesis.sunrise.ui.di.AppModule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class LocationsViewModel @Inject constructor(
    private val currentCoordinatesProvider: CurrentCoordinatesProvider,
    private val getLocations: GetLocations,
    private val deleteLocations: DeleteLocations,
    @Named(AppModule.locationServiceStatusStateFlow)
    private val locationServiceStatus: StateFlow<@JvmSuppressWildcards LocationServiceStatus>,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val selectionSavedStateKey = "selectionActive"
    private val selectedItemsNamesSavedStateKey = "selectedItems"

    private val _state: MutableStateFlow<LocationsState> = MutableStateFlow(
        LocationsState(
            isSelectionActive = savedStateHandle[selectionSavedStateKey] ?: false
        )
    )
    val state: StateFlow<LocationsState> = _state

    private val _events = MutableSharedFlow<LocationsEvents>(extraBufferCapacity = 1)
    val events: Flow<LocationsEvents> = _events

    private var currentCoordinatesSearchJob: Job? = null

    private var locations: List<Location> = emptyList()

    private val selectedLocationsNames =
        savedStateHandle[selectedItemsNamesSavedStateKey] ?: mutableListOf<String>()

    init {
        savedStateHandle[selectedItemsNamesSavedStateKey] = selectedLocationsNames
        observeLocations()
        observeLocationServiceStatus()
    }

    private fun observeLocations() {
        getLocations()
            .onEach { newLocations -> locations = newLocations }
            .map { locations ->
                locations.map {
                    LocationListItemData(
                        locationName = it.name,
                        isSelected = selectedLocationsNames.contains(it.name)
                    )
                }
            }
            .onEach { locationsListData ->
                _state.update {
                    it.copy(
                        locationsListData = locationsListData,
                        isLocationsListDataLoading = false,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeLocationServiceStatus() {
        locationServiceStatus
            .onEach { locationServiceStatus -> _state.update { it.copy(locationServiceStatus = locationServiceStatus) } }
            .launchIn(viewModelScope)
    }

    fun onAddLocationClicked() {
        viewModelScope.launch {
            _events.emit(LocationsEvents.ShowAddLocationOptions)
        }
    }

    fun onAddLocationOptionSelected(option: LocationsAddOptions) {
        cancelCurrentCoordinatesSearch()
        viewModelScope.launch {
            when (option) {
                LocationsAddOptions.CURRENT_LOCATION -> addCurrentLocation()
                LocationsAddOptions.SELECT_ON_MAP -> _events.emit(LocationsEvents.NavigateToMap)
                LocationsAddOptions.ENTER_COORDINATES -> _events.emit(LocationsEvents.ShowAddLocationDialog())
            }
        }
    }

    private fun addCurrentLocation() {
        currentCoordinatesSearchJob = viewModelScope.launch {
            _state.update { it.copy(isCurrentLocationSearchActive = true) }
            val event = when (val result = currentCoordinatesProvider.requestCurrentCoordinates()) {
                is CoordinatesResult.Success -> LocationsEvents.ShowAddLocationDialog(result.currentCoordinates)
                CoordinatesResult.CurrentCoordinatesNotFound -> LocationsEvents.ShowCurrentCoordinatesNotFoundMessage
                is CoordinatesResult.LocationPermissionNotGranted -> LocationsEvents.AskForLocationPermission(
                    result.requiredPermission
                )
            }
            _state.update { it.copy(isCurrentLocationSearchActive = false) }
            _events.emit(event)
        }
    }

    fun onLocationPermissionGranted() {
        addCurrentLocation()
    }

    fun onCancelSelectionClicked() {
        selectedLocationsNames.clear()
        savedStateHandle[selectionSavedStateKey] = false
        _state.update { state ->
            val listData = state.locationsListData.map {
                it.copy(isSelected = false)
            }
            state.copy(
                locationsListData = listData,
                isSelectionActive = false
            )
        }
    }

    fun onDeleteSelectedLocationsClicked() {
        viewModelScope.launch {
            val selectedLocations =
                locations.filter { location -> selectedLocationsNames.contains(location.name) }
            deleteLocations(selectedLocations)
            selectedLocationsNames.clear()
            savedStateHandle[selectionSavedStateKey] = false
            _state.update {
                it.copy(isSelectionActive = false)
            }
        }
    }

    fun onLocationListItemClicked(listItemData: LocationListItemData) {
        if (_state.value.isSelectionActive) {
            toggleLocationListItemSelected(listItemData)
        } else {
            _events.tryEmit(LocationsEvents.NavigateToLocation(locations.first { it.name == listItemData.locationName }))
        }
    }

    fun onLocationListItemLongClicked(item: LocationListItemData) {
        if (!_state.value.isSelectionActive) {
            toggleLocationListItemSelected(item)
        }
    }

    private fun toggleLocationListItemSelected(item: LocationListItemData) {
        val isItemSelected = if (selectedLocationsNames.contains(item.locationName)) {
            selectedLocationsNames.remove(item.locationName)
            false
        } else {
            selectedLocationsNames.add(item.locationName)
            true
        }

        val locationsListData = _state.value.locationsListData.map {
            if (it.locationName == item.locationName)
                it.copy(isSelected = isItemSelected)
            else
                it
        }

        val isSelectionActive = selectedLocationsNames.isNotEmpty()

        savedStateHandle[selectionSavedStateKey] = isSelectionActive

        _state.update {
            it.copy(
                locationsListData = locationsListData,
                isSelectionActive = isSelectionActive
            )
        }
    }

    fun cancelCurrentCoordinatesSearch() {
        if (_state.value.isCurrentLocationSearchActive) {
            currentCoordinatesSearchJob?.cancel()
            _state.update { it.copy(isCurrentLocationSearchActive = false) }
        }
    }
}
