package com.nemesis.sunrise.data.location

import com.nemesis.sunrise.domain.location.Coordinates

sealed class CoordinatesResult {
    data class Success(val currentCoordinates: Coordinates) : CoordinatesResult()
    data class LocationPermissionNotGranted(val requiredPermission: String) : CoordinatesResult()
    object CurrentCoordinatesNotFound : CoordinatesResult()
}
