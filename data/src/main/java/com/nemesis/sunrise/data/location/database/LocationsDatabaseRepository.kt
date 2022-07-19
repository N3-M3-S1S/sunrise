package com.nemesis.sunrise.data.location.database

import com.nemesis.sunrise.domain.location.Location
import com.nemesis.sunrise.domain.location.LocationsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocationsDatabaseRepository(private val locationsDao: LocationsDao) : LocationsRepository {

    override suspend fun saveLocation(location: Location) {
        locationsDao.save(location.toEntity())
    }

    override suspend fun deleteLocations(locations: List<Location>) {
        locationsDao.delete(locations.map(Location::name))
    }

    override suspend fun locationNameAvailable(locationName: String): Boolean =
        locationsDao.locationNameAvailable(locationName)

    override fun getLocations(): Flow<List<Location>> =
        locationsDao.getAll().map { entities -> entities.map { entity -> entity.toLocation() } }
}
