package com.nemesis.sunrise.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nemesis.sunrise.data.location.database.LocationEntity
import com.nemesis.sunrise.data.location.database.LocationsDao

@Database(entities = [LocationEntity::class], version = 1)
abstract class SunriseDatabase : RoomDatabase() {

    companion object {
        private const val databaseName = "db"

        fun create(context: Context): SunriseDatabase =
            Room.databaseBuilder(context, SunriseDatabase::class.java, databaseName).build()
    }

    abstract fun locationsDao(): LocationsDao
}
