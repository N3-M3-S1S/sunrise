package com.nemesis.sunrise.ui.map

import com.nemesis.sunrise.ui.app.LocationServiceStatus

data class MapState(
    val locationServiceStatus: LocationServiceStatus = LocationServiceStatus.ENABLED,
    val isCurrentCoordinatesSearchActive: Boolean = false,
)
