package com.nemesis.sunrise.ui.locations

data class LocationsActions(
    val onCancelSelectionClicked: () -> Unit,
    val onAddLocationClicked: () -> Unit,
    val onDeleteSelectedLocationsClicked: () -> Unit,
    val onAddLocationOptionSelected: (LocationsAddOptions) -> Unit,
    val onLocationListItemClicked: (LocationListItemData) -> Unit,
    val onLocationListItemLongClicked: (LocationListItemData) -> Unit
)
