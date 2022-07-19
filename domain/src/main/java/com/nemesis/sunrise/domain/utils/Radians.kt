package com.nemesis.sunrise.domain.utils

@JvmInline
internal value class Radians(val value: Double) {

    companion object {
        fun fromDegrees(degrees: Double) = Radians(Math.toRadians(degrees))
    }
}
