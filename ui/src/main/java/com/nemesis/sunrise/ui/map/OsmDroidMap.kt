package com.nemesis.sunrise.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.nemesis.sunrise.domain.location.Coordinates
import com.nemesis.sunrise.ui.R
import kotlinx.coroutines.flow.Flow
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

@Composable
fun OsmDroidMap(
    onMapClicked: (latitude: Double, longitude: Double) -> Unit,
    moveToCoordinatesFlow: Flow<Coordinates>,
    modifier: Modifier = Modifier
) {
    val mapView = rememberMapView(onMapClicked = onMapClicked)

    LaunchedEffect(moveToCoordinatesFlow) {
        moveToCoordinatesFlow.collect { coordinates ->
            mapView.moveToCoordinatesAndPlaceMarker(coordinates) {
                onMapClicked(it.latitude, it.longitude)
            }
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { mapView }
    )
}

@Composable
private fun rememberMapView(onMapClicked: ((latitude: Double, longitude: Double) -> Unit)? = null): MapView {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current
    var latitude by rememberSaveable { mutableStateOf(66.75) }
    var longitude by rememberSaveable { mutableStateOf(93.85) }
    var zoomLevel by rememberSaveable { mutableStateOf(2.0) }

    return remember {
        MapView(context).apply {
            id = R.id.map
            isTilesScaledToDpi = true
            minZoomLevel = 2.0

            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

            overlays.add(0, CopyrightOverlay(context).apply { setAlignRight(true) })

            val mapEventsReceiver = object : MapEventsReceiver {
                override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                    onMapClicked?.invoke(p.latitude, p.longitude)
                    return true
                }

                override fun longPressHelper(p: GeoPoint?): Boolean = false
            }
            val mapEventsOverlay = MapEventsOverlay(mapEventsReceiver)
            overlays.add(1, mapEventsOverlay)

            setMultiTouchControls(true)
            setScrollableAreaLimitLatitude(
                MapView.getTileSystem().maxLatitude,
                MapView.getTileSystem().minLatitude,
                0
            )
            zoomToBoundingBox(
                BoundingBox.fromGeoPoints(
                    listOf(GeoPoint(latitude, longitude))
                ), // default map position
                false,
                0,
                zoomLevel,
                0
            )

            lifecycle.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onPause(owner: LifecycleOwner) {
                    latitude = mapCenter.latitude
                    longitude = mapCenter.longitude
                    zoomLevel = zoomLevelDouble
                    onPause()
                }

                override fun onResume(owner: LifecycleOwner) {
                    onResume()
                }
            })
        }
    }
}

private fun MapView.moveToCoordinatesAndPlaceMarker(
    coordinates: Coordinates,
    onMarkerClick: (GeoPoint) -> Unit
) {
    val geoPoint = GeoPoint(coordinates.latitude, coordinates.longitude)
    removeAllMarkers()
    moveToGeoPoint(geoPoint)
    placeMarkerAtGeoPoint(geoPoint, onMarkerClick)
}

private fun MapView.moveToGeoPoint(geoPoint: GeoPoint) {
    with(controller) {
        zoomTo(15.0)
        animateTo(geoPoint)
    }
}

private fun MapView.removeAllMarkers() {
    overlays.filterIsInstance<Marker>().forEach { marker -> marker.remove(this) }
}

private fun MapView.placeMarkerAtGeoPoint(
    geoPoint: GeoPoint,
    onMarkerClick: (GeoPoint) -> Unit
) {
    val marker = Marker(this).apply {
        position = geoPoint
        icon = ContextCompat.getDrawable(context, R.drawable.map_marker)
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
        setOnMarkerClickListener { _, _ ->
            onMarkerClick(geoPoint)
            true
        }
    }
    overlays.add(marker)
}
