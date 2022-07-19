package com.nemesis.sunrise.domain.location.usecase

import com.nemesis.sunrise.domain.location.Location
import com.nemesis.sunrise.domain.location.LocationsRepository
import kotlinx.coroutines.flow.Flow

class GetLocations(private val locationsRepository: LocationsRepository) {

    operator fun invoke(): Flow<List<Location>> = locationsRepository.getLocations()
}
