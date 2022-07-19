package com.nemesis.sunrise.ui.addlocation

data class AddLocationActions(
    val onNameChanged: (String) -> Unit,
    val onLatitudeChanged: (String) -> Unit,
    val onLongitudeChanged: (String) -> Unit,
    val onAddLocationClicked: () -> Unit,
    val onCancelClicked: () -> Unit
)
