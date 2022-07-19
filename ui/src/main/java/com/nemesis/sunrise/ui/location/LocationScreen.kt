@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalPagerApi::class, ExperimentalMaterial3Api::class
)

package com.nemesis.sunrise.ui.location

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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import com.nemesis.sunrise.ui.location.calendar.CalendarScreen
import com.nemesis.sunrise.ui.location.details.LocationDetailsScreen
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch

@Destination(navArgsDelegate = LocationScreenNavArgs::class)
@Composable
fun LocationScreen(
    navigator: DestinationsNavigator,
    viewModel: LocationViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val actions = LocationActions(
        onBackClicked = navigator::navigateUp,
        onDateSelected = viewModel::onDetailsDateSelected,
        onCalendarDateRangeChanged = viewModel::onCalendarDateRangeChanged
    )
    val events = viewModel.events
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
                LocationTopBar(location = state.location, onBackClicked = actions.onBackClicked)
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
                HorizontalPager(count = 2, state = pagerState, verticalAlignment = Alignment.Top) { page ->
                    when (page) {
                        0 -> LocationDetailsScreen(locationDetails = state.details)
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
    )
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
            text = "$decimalFormat | $decimalFormat".format(coordinates.latitude, coordinates.longitude),
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
