package com.nemesis.sunrise.ui.addlocation

import com.nemesis.sunrise.ui.location.LocationSerializer
import javax.inject.Inject

class LocationNameValidator @Inject constructor() {
    private val forbiddenCharactersRegEx =
        "(\\${CoordinatesSerializer.delimiter}|\\${LocationSerializer.delimiter})".toRegex()

    fun locationNameContainsOnlyAllowedCharacters(locationName: String): Boolean =
        !locationName.contains(forbiddenCharactersRegEx)
}