package com.nemesis.sunrise.ui.locations

import com.nemesis.sunrise.domain.location.Coordinates
import com.nemesis.sunrise.domain.location.Location

sealed class LocationsEvents {
    data class ShowAddLocationDialog(val coordinates: Coordinates? = null) : LocationsEvents()
    data class AskForLocationPermission(val requiredLocationPermission: String) : LocationsEvents()
    data class NavigateToLocation(val location: Location) : LocationsEvents()
    object NavigateToMap : LocationsEvents()
    object ShowAddLocationOptions : LocationsEvents()
    object ShowCurrentCoordinatesNotFoundMessage : LocationsEvents()
}
