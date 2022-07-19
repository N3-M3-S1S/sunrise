package com.nemesis.sunrise.domain.location.usecase

import com.nemesis.sunrise.domain.location.Coordinates
import com.nemesis.sunrise.domain.location.Location
import com.nemesis.sunrise.domain.location.LocationsRepository

class SaveLocation(
    private val locationsRepository: LocationsRepository,
    private val reduceCoordinateExcessAccuracy: ReduceCoordinateExcessAccuracy
) {

    suspend operator fun invoke(location: Location) {
        val coordinates = location.coordinates
        val latitude = reduceCoordinateExcessAccuracy(coordinates.latitude)
        val longitude = reduceCoordinateExcessAccuracy(coordinates.longitude)
        val coordinatesWithReducedAccuracy = Coordinates(latitude, longitude)
        locationsRepository.saveLocation(location.copy(coordinates = coordinatesWithReducedAccuracy))
    }
}
