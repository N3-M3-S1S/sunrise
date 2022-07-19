@file:OptIn(ExperimentalMaterial3Api::class)

package com.nemesis.sunrise.ui.map

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nemesis.sunrise.ui.R
import com.nemesis.sunrise.domain.location.Coordinates
import com.nemesis.sunrise.ui.app.LocationServiceStatus
import com.nemesis.sunrise.ui.components.BackIconButton
import com.nemesis.sunrise.ui.components.LocationDisabledIcon
import com.nemesis.sunrise.ui.destinations.AddLocationScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@Destination
@Composable
fun MapScreen(navigator: DestinationsNavigator, mapViewModel: MapViewModel = hiltViewModel()) {
    val state by mapViewModel.state.collectAsState()

    val actions = remember {
        MapActions(
            onBackClicked = navigator::navigateUp,
            onCurrentLocationClicked = mapViewModel::currentLocationButtonClicked,
            onMapClicked = mapViewModel::coordinatesSelected
        )
    }

    val locationPermissionRequestLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { permissionGranted ->
            if (permissionGranted) mapViewModel.locationPermissionGranted()
        }
    )

    val context = LocalContext.current
    LaunchedEffect(true) {
        mapViewModel.mapScreenEvents.collect { event ->
            // MapEvents.MoveMapToCoordinates is handled by moveMapToCoordinatesFlow
            @Suppress("NON_EXHAUSTIVE_WHEN_STATEMENT")
            when (event) {
                is MapEvents.AskForLocationPermission -> locationPermissionRequestLauncher.launch(
                    event.locationPermission
                )
                is MapEvents.NavigateToAddLocation ->
                    navigator
                        .navigate(
                            AddLocationScreenDestination(initialCoordinates = event.coordinates)
                        )
                MapEvents.ShowCurrentCoordinatesNotFoundMessage -> {
                    Toast.makeText(context, R.string.current_location_not_found, Toast.LENGTH_SHORT)
                        .show()
                }

            }
        }
    }

    val moveMapToCoordinatesFlow = remember {
        mapViewModel.mapScreenEvents
            .filter { it is MapEvents.MoveMapToCoordinates }
            .map { (it as MapEvents.MoveMapToCoordinates).coordinates }
    }

    MapContent(
        state = state,
        actions = actions,
        moveToCoordinatesFlow = moveMapToCoordinatesFlow,
    )
}

@Composable
private fun MapContent(
    state: MapState,
    actions: MapActions,
    moveToCoordinatesFlow: Flow<Coordinates>
) {
    Scaffold(
        topBar = {
            MapTopBar(
                locationServiceStatus = state.locationServiceStatus,
                onBackClicked = actions.onBackClicked,
                isCurrentLocationSearchActive = state.isCurrentCoordinatesSearchActive,
                onCurrentLocationClicked = actions.onCurrentLocationClicked
            )
        },
    ) { padding ->
        OsmDroidMap(
            onMapClicked = actions.onMapClicked,
            moveToCoordinatesFlow = moveToCoordinatesFlow,
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
private fun MapTopBar(
    onBackClicked: () -> Unit,
    locationServiceStatus: LocationServiceStatus,
    isCurrentLocationSearchActive: Boolean,
    onCurrentLocationClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        navigationIcon = { BackIconButton(onClick = onBackClicked) },
        title = { Text(text = stringResource(id = R.string.map)) },
        actions = {
            Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                if (locationServiceStatus == LocationServiceStatus.ENABLED) {
                    if (isCurrentLocationSearchActive) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(onClick = onCurrentLocationClicked) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = ""
                            )
                        }
                    }
                } else {
                    LocationDisabledIcon()
                }
            }
        }
    )
}
