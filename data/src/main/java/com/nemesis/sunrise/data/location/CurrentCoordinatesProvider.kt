package com.nemesis.sunrise.data.location

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.location.LocationManagerCompat
import androidx.core.os.CancellationSignal
import androidx.core.util.Consumer
import com.nemesis.sunrise.domain.location.Coordinates
import com.nemesis.sunrise.domain.location.usecase.ReduceCoordinateExcessAccuracy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.selects.whileSelect
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.Executor

class CurrentCoordinatesProvider(
    private val context: Context,
    private val reduceCoordinateExcessAccuracy: ReduceCoordinateExcessAccuracy
) {
    private val locationManager: LocationManager = context.getSystemService()!!
    private val requiredLocationPermission: String = permission.ACCESS_FINE_LOCATION
    private val locationRequestExecutor: Executor = Dispatchers.IO.asExecutor()

    suspend fun requestCurrentCoordinates(): CoordinatesResult {
        if (!locationPermissionGranted()) return CoordinatesResult.LocationPermissionNotGranted(
            requiredLocationPermission
        )

        return getCurrentCoordinates()?.let(CoordinatesResult::Success)
            ?: CoordinatesResult.CurrentCoordinatesNotFound
    }

    private fun locationPermissionGranted(): Boolean =
        ContextCompat.checkSelfPermission(
            context,
            requiredLocationPermission
        ) == PackageManager.PERMISSION_GRANTED

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun getCurrentCoordinates(): Coordinates? = coroutineScope {
        var coordinates: Coordinates? = null

        val locationRequests = getAvailableLocationProvidersExceptPassive()
            .also { Log.d("CurrentCoordinatesProvider", "Available location providers: $it") }
            .mapTo(mutableListOf()) { provider -> async { getCurrentLocation(provider) } }

        if (locationRequests.isEmpty()) return@coroutineScope null

        //get result of first completed location request, if the result is null - remove еру request from the list of requests and repeat until result is not null or the list is empty
        whileSelect {
            locationRequests.forEach { request ->
                request.onAwait { resultCoordinates ->
                    if (resultCoordinates != null) {
                        coordinates = resultCoordinates
                        locationRequests.forEach { it.cancel() }
                        false
                    } else {
                        locationRequests.remove(request)
                        locationRequests.isNotEmpty()
                    }
                }
            }
        }

        coordinates
    }

    private fun getAvailableLocationProvidersExceptPassive(): List<String> =
        locationManager.getProviders(true).filterNot { it == LocationManager.PASSIVE_PROVIDER }

    @OptIn(ExperimentalCoroutinesApi::class)
    @SuppressLint("MissingPermission")
    private suspend fun getCurrentLocation(locationProvider: String): Coordinates? =
        suspendCancellableCoroutine { continuation ->
            val cancellationSignal = CancellationSignal()

            continuation.invokeOnCancellation {
                Log.d(
                    "CurrentCoordinatesProvider",
                    "Location request for provider '$locationProvider' canceled"
                )
                cancellationSignal.cancel()
            }

            val locationConsumer = Consumer<Location> { location ->
                val currentCoordinates = location?.let {
                    val latitude = reduceCoordinateExcessAccuracy(location.latitude)
                    val longitude = reduceCoordinateExcessAccuracy(location.longitude)

                    if (latitude == 0.0 && longitude == 0.0)
                        null
                    else
                        Coordinates(latitude, longitude)
                }
                Log.d(
                    "CurrentCoordinatesProvider",
                    "Provider '$locationProvider' returned coordinates: Latitude: ${currentCoordinates?.latitude} Longitude: ${currentCoordinates?.longitude}"
                )
                continuation.resume(currentCoordinates) {
                    cancellationSignal.cancel()
                }
            }

            Log.d(
                "CurrentCoordinatesProvider",
                "Requesting location with provider: '$locationProvider'"
            )
            LocationManagerCompat.getCurrentLocation(
                locationManager,
                locationProvider,
                cancellationSignal,
                locationRequestExecutor,
                locationConsumer
            )
        }
}
