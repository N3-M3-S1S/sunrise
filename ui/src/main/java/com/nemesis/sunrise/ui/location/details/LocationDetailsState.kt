package com.nemesis.sunrise.ui.location.details

import com.nemesis.sunrise.ui.utils.StringInterval

data class LocationDetailsState(
    val date: String,
    val dayOfWeek: String,
    val dayTime: StringInterval?,
    val dayDuration: String?,
    val zenith: String,
    val civilTwilight: StringInterval?,
    val nauticalTwilight: StringInterval?,
    val astronomicalTwilight: StringInterval?
)
