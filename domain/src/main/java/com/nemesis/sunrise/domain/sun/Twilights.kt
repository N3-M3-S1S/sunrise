package com.nemesis.sunrise.domain.sun

import com.nemesis.sunrise.domain.utils.InstantInterval

data class Twilights(
    val civilTwilight: InstantInterval?,
    val nauticalTwilight: InstantInterval?,
    val astronomicalTwilight: InstantInterval?
)
