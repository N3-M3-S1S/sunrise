package com.nemesis.sunrise.ui.location

import com.nemesis.sunrise.domain.location.Coordinates
import com.nemesis.sunrise.domain.location.Location
import com.ramcosta.composedestinations.navargs.DestinationsNavTypeSerializer
import com.ramcosta.composedestinations.navargs.NavTypeSerializer

@NavTypeSerializer
object LocationSerializer : DestinationsNavTypeSerializer<Location> {
    const val delimiter = "|"

    override fun fromRouteString(routeStr: String): Location {
        val args = routeStr.split(delimiter)
        val name = args[0]
        val latitude = args[1].toDouble()
        val longitude = args[2].toDouble()
        return Location(
            name = name,
            coordinates = Coordinates(latitude = latitude, longitude = longitude)
        )
    }

    override fun toRouteString(value: Location): String = buildString {
        append(value.name)
        append(delimiter)
        append(value.coordinates.latitude)
        append(delimiter)
        append(value.coordinates.longitude)
    }
}