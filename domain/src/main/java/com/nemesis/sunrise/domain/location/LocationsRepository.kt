package com.nemesis.sunrise.domain.location

import kotlinx.coroutines.flow.Flow

interface LocationsRepository {
    suspend fun saveLocation(location: Location)
    suspend fun deleteLocations(locations: List<Location>)
    suspend fun locationNameAvailable(locationName: String): Boolean
    suspend fun getLocationByName(locationName: String): Location?
    fun getLocations(): Flow<List<Location>>
}
