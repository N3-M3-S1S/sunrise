package com.nemesis.sunrise.domain.location.usecase

class ValidateLatitude {

    operator fun invoke(latitude: Double): Boolean {
        val latitudeMinValue = -90.0
        val latitudeMaxValue = 90.0
        return latitude in latitudeMinValue..latitudeMaxValue
    }
}
