package com.nemesis.sunrise.data.location.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.nemesis.sunrise.domain.location.Coordinates
import com.nemesis.sunrise.domain.location.Location

@Entity(indices = [Index(value = ["name"], unique = true)])
data class LocationEntity(
    @ColumnInfo(collate = ColumnInfo.NOCASE)
    val name: String,
    val latitude: Double,
    val longitude: Double
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0
}

internal fun Location.toEntity() = LocationEntity(name, coordinates.latitude, coordinates.longitude)

internal fun LocationEntity.toLocation() = Location(name, Coordinates(latitude, longitude))
