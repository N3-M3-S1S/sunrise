package com.nemesis.sunrise.ui.launcher

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import com.nemesis.sunrise.domain.location.Location
import com.nemesis.sunrise.ui.destinations.LauncherScreenDestination
import com.nemesis.sunrise.ui.destinations.LocationScreenDestination
import com.nemesis.sunrise.ui.destinations.LocationsScreenDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import com.ramcosta.composedestinations.spec.Direction

@Composable
@Destination
@RootNavGraph(start = true)
fun LauncherScreen(
    navigator: DestinationsNavigator,
    viewModel: LauncherViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    if (state is LauncherState.Ready) {
        navigateToStartDestination(navigator, (state as LauncherState.Ready).defaultLocation)
    }
}

private fun navigateToStartDestination(
    navigator: DestinationsNavigator,
    defaultLocation: Location?
) {
    val startDestination = getStartDestination(defaultLocation)

    navigator.navigate(startDestination) {
        popUpTo(LauncherScreenDestination) {
            inclusive = true
        }
    }
}

private fun getStartDestination(defaultLocation: Location?): Direction =
    defaultLocation?.let { LocationScreenDestination(it) } ?: LocationsScreenDestination
