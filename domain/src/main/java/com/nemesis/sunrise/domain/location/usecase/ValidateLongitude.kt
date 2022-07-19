package com.nemesis.sunrise.domain.location.usecase

class ValidateLongitude {

    operator fun invoke(longitude: Double): Boolean {
        val longitudeMinValue = -180.0
        val longitudeMaxValue = 180.0
        return longitude in longitudeMinValue..longitudeMaxValue
    }
}
