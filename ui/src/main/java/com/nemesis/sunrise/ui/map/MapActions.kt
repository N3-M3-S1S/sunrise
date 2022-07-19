package com.nemesis.sunrise.ui.map

data class MapActions(
    val onBackClicked: () -> Unit,
    val onCurrentLocationClicked: () -> Unit,
    val onMapClicked: (latitude: Double, longitude: Double) -> Unit
)
