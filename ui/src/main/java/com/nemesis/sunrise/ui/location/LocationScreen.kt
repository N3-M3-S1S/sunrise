@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalPagerApi::class,
)

package com.nemesis.sunrise.ui.location

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.nemesis.sunrise.ui.R
import com.nemesis.sunrise.domain.location.Coordinates
import com.nemesis.sunrise.domain.location.Location
import com.nemesis.sunrise.ui.components.BackIconButton
import com.nemesis.sunrise.ui.components.pagerTabIndicatorOffsetMaterial3
import com.nemesis.sunrise.ui.destinations.LocationScreenDestination
import com.nemesis.sunrise.ui.destinations.LocationsScreenDestination
import com.nemesis.sunrise.ui.location.calendar.CalendarScreen
import com.nemesis.sunrise.ui.location.details.LocationDetails
import com.nemesis.sunrise.ui.location.details.LocationDetailsScreen
import com.nemesis.sunrise.ui.utils.LocalDateRange
import com.nemesis.sunrise.ui.utils.LocalTime
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

@Destination(navArgsDelegate = LocationScreenNavArgs::class)
@Composable
fun LocationScreen(
    navigator: DestinationsNavigator,
    viewModel: LocationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val popUpToLocationsList: () -> Unit = {
        navigator.navigate(LocationsScreenDestination) {
            popUpTo(LocationScreenDestination) {
                inclusive = true
            }
        }
    }

    val actions = LocationActions(
        onBackClicked = popUpToLocationsList,
        onDefaultLocationButtonClicked = viewModel::toggleLocationDefault,
        onDateSelected = viewModel::showDetailsForDate,
        onCalendarDateRangeChanged = viewModel::showCalendarItemsForDateRange,
        onTodayDetailsButtonClicked = viewModel::showTodayDetails
    )
    val events = viewModel.events

    BackHandler { popUpToLocationsList() }

    LocationContent(
        state = state,
        actions = actions,
        events = events
    )
}

@Composable
private fun LocationContent(
    state: LocationState,
    actions: LocationActions,
    events: Flow<LocationEvents>,
) {
    AnimatedVisibility(
        visible = state != LocationState.Loading,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        state as LocationState.Ready
        Scaffold(
            topBar = {
                LocationTopBar(
                    location = state.location,
                    locationSetAsDefault = state.locationSetAsDefault,
                    onToggleLocationDefaultClicked = actions.onDefaultLocationButtonClicked,
                    onBackClicked = actions.onBackClicked,
                )
            }
        ) { padding ->
            val pagerState = rememberPagerState()

            LaunchedEffect(true) {
                events.collect {
                    when (it) {
                        LocationEvents.NavigateToDetailsScreen -> {
                            pagerState.animateScrollToPage(0)
                        }
                        LocationEvents.ScrollCalendarListToTop -> {}
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Tabs(pagerState = pagerState)
                HorizontalPager(
                    count = 2,
                    state = pagerState,
                    verticalAlignment = Alignment.Top
                ) { page ->
                    when (page) {
                        0 -> LocationDetailsScreen(
                            locationDetails = state.locationDetails,
                            todayDetailsButtonVisible = state.todayDetailsButtonVisible,
                            onTodayDetailsButtonClicked = actions.onTodayDetailsButtonClicked
                        )
                        1 -> CalendarScreen(
                            calendarItems = state.calendarItems,
                            onDateSelected = actions.onDateSelected,
                            calendarDateRange = state.calendarDateRange,
                            onDateRangeChanged = actions.onCalendarDateRangeChanged,
                            scrollCalendarEventFlow = events.filterIsInstance()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LocationTopBar(
    location: Location,
    locationSetAsDefault: Boolean,
    onToggleLocationDefaultClicked: () -> Unit,
    onBackClicked: () -> Unit
) {
    CenterAlignedTopAppBar(
        navigationIcon = { BackIconButton(onClick = onBackClicked) },
        title = {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = location.name.uppercase(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                CoordinatesText(coordinates = location.coordinates)
            }
        },
        actions = {
            DefaultLocationButton(
                locationSetAsDefault = locationSetAsDefault,
                onClick = onToggleLocationDefaultClicked
            )
        }
    )
}


@Composable
private fun DefaultLocationButton(locationSetAsDefault: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        val icon = if (locationSetAsDefault) Icons.Default.Star else Icons.Default.StarBorder
        Icon(imageVector = icon, contentDescription = "Default location")
    }
}

@Composable
private fun CoordinatesText(coordinates: Coordinates) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val textStyle = MaterialTheme.typography.titleSmall

        Icon(
            modifier = Modifier.size(textStyle.fontSize.value.dp),
            imageVector = Icons.Filled.MyLocation,
            contentDescription = "",
        )
        val decimalFormat = "%.2f"
        Text(
            text = "$decimalFormat | $decimalFormat".format(
                coordinates.latitude,
                coordinates.longitude
            ),
            style = textStyle,
            modifier = Modifier.padding(end = textStyle.fontSize.value.dp + 4.dp)
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun Tabs(pagerState: PagerState) {
    val coroutineScope = rememberCoroutineScope()
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        indicator = {
            TabRowDefaults.Indicator(
                modifier = Modifier.pagerTabIndicatorOffsetMaterial3(
                    pagerState = pagerState,
                    tabPositions = it
                )
            )
        }
    ) {
        stringArrayResource(id = R.array.location_tabs_titles).forEachIndexed { index, title ->
            Tab(
                selected = pagerState.currentPage == index,
                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                text = { Text(text = title.uppercase()) }
            )
        }
    }
}


@Preview
@Composable
fun LocationContentPreview() {
    val state = LocationState.Ready(
        location = Location(
            name = "Location",
            coordinates = Coordinates(latitude = 123.0, longitude = 456.0)
        ),
        locationSetAsDefault = true,
        locationDetails = LocationDetails(
            date = LocalDate(2022, 1, 1),
            dayTime = null,
            solarNoonTime = LocalTime(12, 0, 0),
            dayDuration = null,
            civilTwilight = null,
            nauticalTwilight = null,
            astronomicalTwilight = null,
        ),
        calendarDateRange = LocalDateRange(
            from = LocalDate(2022, 1, 1),
            to = LocalDate(2022, 2, 1)
        ),
        calendarItems = emptyFlow(),
        todayDetailsButtonVisible = true
    )

    LocationContent(
        state = state,
        actions = LocationActions(
            onBackClicked = {},
            onDefaultLocationButtonClicked = {},
            onDateSelected = {},
            onCalendarDateRangeChanged = {},
            onTodayDetailsButtonClicked = {}
        ),
        events = emptyFlow()
    )

}