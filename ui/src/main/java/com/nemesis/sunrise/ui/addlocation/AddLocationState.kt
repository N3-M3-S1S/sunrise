package com.nemesis.sunrise.ui.addlocation

data class AddLocationState(
    val name: String = "",
    val isNameAvailable: Boolean = true,
    val nameContainsOnlyAllowedCharacters: Boolean = true,
    val latitude: String = "",
    val isLatitudeValid: Boolean = true,
    val longitude: String = "",
    val isLongitudeValid: Boolean = true,
    val isAddLocationAvailable: Boolean = false
)
