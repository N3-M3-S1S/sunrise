package com.nemesis.sunrise.ui.addlocation

import com.nemesis.sunrise.domain.location.Coordinates
import com.ramcosta.composedestinations.navargs.DestinationsNavTypeSerializer
import com.ramcosta.composedestinations.navargs.NavTypeSerializer

@NavTypeSerializer
object CoordinatesSerializer : DestinationsNavTypeSerializer<Coordinates> {
    const val delimiter = "|"

    override fun fromRouteString(routeStr: String): Coordinates {
        val args = routeStr.split(delimiter)
        val latitude = args[0].toDouble()
        val longitude = args[1].toDouble()
        return Coordinates(latitude = latitude, longitude = longitude)
    }

    override fun toRouteString(value: Coordinates): String =
        "${value.latitude}$delimiter${value.longitude}"
}