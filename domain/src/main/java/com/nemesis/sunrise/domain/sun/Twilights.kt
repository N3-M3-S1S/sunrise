package com.nemesis.sunrise.domain.sun

import com.nemesis.sunrise.domain.utils.InstantRange

data class Twilights(
    val civilTwilight: InstantRange?,
    val nauticalTwilight: InstantRange?,
    val astronomicalTwilight: InstantRange?
)
