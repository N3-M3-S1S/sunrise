package com.nemesis.sunrise.data.location.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationsDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun save(locationEntity: LocationEntity)

    @Query("DELETE FROM LocationEntity WHERE name = :locationName")
    suspend fun delete(locationName: String)

    @Query("DELETE FROM LocationEntity WHERE name IN (:locationNames)")
    suspend fun delete(locationNames: List<String>)

    @Query("SELECT NOT EXISTS(SELECT 1 FROM LocationEntity WHERE name = :locationName)")
    suspend fun locationNameAvailable(locationName: String): Boolean

    @Query("SELECT * FROM LocationEntity ORDER BY id DESC")
    fun getAll(): Flow<List<LocationEntity>>

    @Query("SELECT * FROM LocationEntity WHERE lower(name) = lower(:locationName)")
    suspend fun getByName(locationName: String): LocationEntity?
}
