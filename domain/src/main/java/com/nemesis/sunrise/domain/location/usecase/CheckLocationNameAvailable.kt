package com.nemesis.sunrise.domain.location.usecase

import com.nemesis.sunrise.domain.location.LocationsRepository

class CheckLocationNameAvailable(private val locationsRepository: LocationsRepository) {

    suspend operator fun invoke(locationName: String): Boolean =
        locationsRepository.locationNameAvailable(locationName)
}
