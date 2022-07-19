package com.nemesis.sunrise.domain.location.usecase

import com.nemesis.sunrise.domain.location.Location
import com.nemesis.sunrise.domain.location.LocationsRepository

class DeleteLocations(private val locationsRepository: LocationsRepository) {

    suspend operator fun invoke(locations: List<Location>) =
        locationsRepository.deleteLocations(locations)
}
