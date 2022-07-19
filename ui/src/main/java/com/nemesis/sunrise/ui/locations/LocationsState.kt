package com.nemesis.sunrise.ui.locations

import com.nemesis.sunrise.ui.app.LocationServiceStatus

data class LocationsState(
    val locationsListData: List<LocationListItemData> = emptyList(),
    val locationServiceStatus: LocationServiceStatus = LocationServiceStatus.ENABLED,
    val isLocationsListDataLoading: Boolean = true,
    val isSelectionActive: Boolean = false,
    val isCurrentLocationSearchActive: Boolean = false
)
