package com.nemesis.sunrise.ui.addlocation

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nemesis.sunrise.domain.location.Coordinates
import com.nemesis.sunrise.domain.location.Location
import com.nemesis.sunrise.domain.location.usecase.CheckLocationNameAvailable
import com.nemesis.sunrise.domain.location.usecase.SaveLocation
import com.nemesis.sunrise.domain.location.usecase.ValidateLatitude
import com.nemesis.sunrise.domain.location.usecase.ValidateLongitude
import com.nemesis.sunrise.ui.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddLocationViewModel @Inject constructor(
    private val locationNameValidator: LocationNameValidator,
    private val validateLatitude: ValidateLatitude,
    private val validateLongitude: ValidateLongitude,
    private val checkLocationNameAvailable: CheckLocationNameAvailable,
    private val saveLocation: SaveLocation,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val nameSavedStateKey = "name"
    private val latitudeSavedStateKey = "lat"
    private val longitudeSavedStateKey = "lng"

    private val _name = MutableStateFlow(savedStateHandle[nameSavedStateKey] ?: "")
    val name: StateFlow<String> = _name

    val isNameAvailable = _name.map(checkLocationNameAvailable::invoke).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = true
    )

    val nameContainsOnlyAllowedCharacters =
        _name.map(locationNameValidator::locationNameContainsOnlyAllowedCharacters).stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = true
        )

    private val _latitude = MutableStateFlow(savedStateHandle[latitudeSavedStateKey] ?: "")
    val latitude: StateFlow<String> = _latitude

    val isLatitudeValid = _latitude.map {
        it.isEmpty() || it.toDoubleOrNull()?.let(validateLatitude::invoke) ?: false
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = true
    )

    private val _longitude = MutableStateFlow(savedStateHandle[longitudeSavedStateKey] ?: "")
    val longitude: StateFlow<String> = _longitude

    val isLongitudeValid: StateFlow<Boolean> = _longitude.map {
        it.isEmpty() || it.toDoubleOrNull()?.let(validateLongitude::invoke) ?: false
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = true
    )

    val isAddLocationAvailable: StateFlow<Boolean> =
        combine(
            name.map(String::isNotBlank),
            isNameAvailable,
            nameContainsOnlyAllowedCharacters,
            latitude.map(String::isNotBlank),
            isLatitudeValid,
            longitude.map(String::isNotBlank),
            isLongitudeValid
        ) { flows ->
            flows.all { it } // returns true if all flows return true
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    private val _events = MutableSharedFlow<AddLocationEvents>()
    val events: Flow<AddLocationEvents> = _events

    init {
        val initialCoordinates =
            savedStateHandle.navArgs<AddLocationScreenNavArgs>().initialCoordinates
        Log.d("AddLocationViewModel", "Initial coordinates: $initialCoordinates")
        if (initialCoordinates != null) {
            if (!savedStateHandle.contains(latitudeSavedStateKey)) {
                _latitude.update { initialCoordinates.latitude.toString() }
            }

            if (!savedStateHandle.contains(longitudeSavedStateKey)) {
                _longitude.update { initialCoordinates.longitude.toString() }
            }
        }
    }

    fun onNameChanged(locationName: String) {
        _name.update { locationName }
        savedStateHandle[nameSavedStateKey] = locationName
    }

    fun onLatitudeChanged(value: String) {
        _latitude.update { value }
        savedStateHandle[latitudeSavedStateKey] = value
    }

    fun onLongitudeChanged(value: String) {
        _longitude.update { value }
        savedStateHandle[longitudeSavedStateKey] = value
    }

    fun onAddLocationClicked() {
        viewModelScope.launch {
            val locationName = name.value
            val latitude = latitude.value.toDouble()
            val longitude = longitude.value.toDouble()
            val location = Location(locationName, Coordinates(latitude, longitude))
            saveLocation(location)
            _events.emit(AddLocationEvents.LocationAdded)
        }
    }
}
