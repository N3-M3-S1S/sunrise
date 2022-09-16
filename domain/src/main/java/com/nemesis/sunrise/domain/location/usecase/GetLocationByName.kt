package com.nemesis.sunrise.domain.location.usecase

import com.nemesis.sunrise.domain.location.Location
import com.nemesis.sunrise.domain.location.LocationsRepository

class GetLocationByName(private val locationsRepository: LocationsRepository) {

    suspend operator fun invoke(locationName: String): Location? =
        locationsRepository.getLocationByName(locationName)
}
