package com.nemesis.sunrise.ui.locations

data class LocationListItemData(
    val locationName: String,
    val locationDefault: Boolean,
    val selected: Boolean = false
)