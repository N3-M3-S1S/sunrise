package com.nemesis.sunrise.ui.map

import com.nemesis.sunrise.domain.location.Coordinates

sealed class MapEvents {
    data class AskForLocationPermission(val locationPermission: String) : MapEvents()
    data class MoveMapToCoordinates(val coordinates: Coordinates) : MapEvents()
    data class NavigateToAddLocation(val coordinates: Coordinates) : MapEvents()
    object ShowCurrentCoordinatesNotFoundMessage : MapEvents()
}
