@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.nemesis.sunrise.ui.locations

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.with
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddLocation
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nemesis.sunrise.ui.R
import com.nemesis.sunrise.ui.app.LocationServiceStatus
import com.nemesis.sunrise.ui.components.BottomSheetContent
import com.nemesis.sunrise.ui.components.LocationDisabledIcon
import com.nemesis.sunrise.ui.destinations.AddLocationScreenDestination
import com.nemesis.sunrise.ui.destinations.LocationScreenDestination
import com.nemesis.sunrise.ui.destinations.MapScreenDestination
import com.nemesis.sunrise.ui.theme.Red
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun LocationsScreen(
    navigator: DestinationsNavigator,
    viewModel: LocationsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val addLocationOptionsBottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    val actions = remember {
        LocationsActions(
            onCancelSelectionClicked = viewModel::onCancelSelectionClicked,
            onAddLocationClicked = viewModel::onAddLocationClicked,
            onDeleteSelectedLocationsClicked = viewModel::onDeleteSelectedLocationsClicked,
            onAddLocationOptionSelected = viewModel::onAddLocationOptionSelected,
            onLocationListItemClicked = viewModel::onLocationListItemClicked,
            onLocationListItemLongClicked = viewModel::onLocationListItemLongClicked
        )
    }

    val coroutineScope = rememberCoroutineScope()
    val locationPermissionRequestLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { permissionGranted ->
            if (permissionGranted) viewModel.onLocationPermissionGranted()
        }
    )

    BackHandler(enabled = addLocationOptionsBottomSheetState.isVisible) {
        viewModel.cancelCurrentCoordinatesSearch()
        coroutineScope.launch {
            addLocationOptionsBottomSheetState.hide()
        }
    }

    BackHandler(
        enabled = state.isSelectionActive,
        onBack = viewModel::onCancelSelectionClicked
    )

    LaunchedEffect(true) {
        // observe bottom sheet state and ask for cancel current location search when bottom sheet is closed
        snapshotFlow { addLocationOptionsBottomSheetState.currentValue }.collect { bottomSheetState ->
            if (bottomSheetState == ModalBottomSheetValue.Hidden) {
                viewModel.cancelCurrentCoordinatesSearch()
            }
        }
    }

    val context = LocalContext.current
    LaunchedEffect(true) {
        viewModel.events.collect { event ->
            when (event) {
                is LocationsEvents.AskForLocationPermission ->
                    locationPermissionRequestLauncher.launch(event.requiredLocationPermission)

                is LocationsEvents.ShowAddLocationDialog -> {
                    coroutineScope.launch {
                        addLocationOptionsBottomSheetState.hide()
                        navigator.navigate(
                            AddLocationScreenDestination(initialCoordinates = event.coordinates)
                        )
                    }
                }
                LocationsEvents.ShowAddLocationOptions -> launch {
                    addLocationOptionsBottomSheetState.show()
                }
                LocationsEvents.NavigateToMap -> launch {
                    addLocationOptionsBottomSheetState.hide()
                    navigator.navigate(MapScreenDestination)
                }
                is LocationsEvents.NavigateToLocation -> navigator.navigate(
                    LocationScreenDestination(location = event.location)
                )
                LocationsEvents.ShowCurrentCoordinatesNotFoundMessage ->
                    Toast.makeText(context, R.string.current_location_not_found, Toast.LENGTH_SHORT)
                        .show()
            }
        }
    }

    LocationsContent(
        bottomSheetState = addLocationOptionsBottomSheetState,
        screenState = state,
        actions = actions
    )
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun LocationsContent(
    bottomSheetState: ModalBottomSheetState,
    screenState: LocationsState,
    actions: LocationsActions,
) {
    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        sheetBackgroundColor = MaterialTheme.colorScheme.surface,
        sheetContentColor = MaterialTheme.colorScheme.contentColorFor(MaterialTheme.colorScheme.surface),
        sheetContent = {
            BottomSheetContent({
                AddLocationOptionsList(
                    locationServiceStatus = screenState.locationServiceStatus,
                    isCurrentLocationSearchActive = screenState.isCurrentLocationSearchActive,
                    onOptionSelected = actions.onAddLocationOptionSelected
                )
            }, title = stringResource(id = R.string.add_location_title))
        }
    ) {
        Scaffold(
            topBar = {
                LocationsTopBar(
                    isSelectionActive = screenState.isSelectionActive,
                    onCancelSelectionClicked = actions.onCancelSelectionClicked,
                    onAddLocationClicked = actions.onAddLocationClicked,
                    onDeleteSelectedLocationsClicked = actions.onDeleteSelectedLocationsClicked
                )
            },
        ) {
            LocationsList(
                data = screenState.locationsListData,
                isDataLoading = screenState.isLocationsListDataLoading,
                onItemClick = actions.onLocationListItemClicked,
                onItemLongClick = actions.onLocationListItemLongClicked,
                modifier = Modifier.padding(it)
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun AddLocationOptionsList(
    locationServiceStatus: LocationServiceStatus,
    isCurrentLocationSearchActive: Boolean,
    onOptionSelected: (LocationsAddOptions) -> Unit,
    modifier: Modifier = Modifier

) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ListItem(
            modifier = modifier
                .clickable(
                    enabled = !isCurrentLocationSearchActive && locationServiceStatus == LocationServiceStatus.ENABLED,
                    onClick = { onOptionSelected(LocationsAddOptions.CURRENT_LOCATION) }
                ),
            text = {
                if (isCurrentLocationSearchActive)
                    Text(
                        text = stringResource(id = R.string.looking_for_current_location),
                        modifier = Modifier.alpha(ContentAlpha.disabled)
                    )
                else
                    Text(
                        text = stringResource(id = R.string.current_location),
                        modifier = if (locationServiceStatus == LocationServiceStatus.DISABLED)
                            Modifier.alpha(ContentAlpha.disabled) else Modifier
                    )
            },
            trailing = {
                if (isCurrentLocationSearchActive)
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                else if (locationServiceStatus == LocationServiceStatus.DISABLED) {
                    LocationDisabledIcon()
                }
            }
        )

        Divider()
        ListItem(
            modifier = Modifier.clickable(onClick = { onOptionSelected(LocationsAddOptions.SELECT_ON_MAP) }),
            text = { Text(text = stringResource(id = R.string.select_on_map)) }
        )
        Divider()
        ListItem(
            modifier = Modifier.clickable(onClick = { onOptionSelected(LocationsAddOptions.ENTER_COORDINATES) }),
            text = { Text(text = stringResource(id = R.string.enter_coordinates)) }
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun LocationsTopBar(
    isSelectionActive: Boolean,
    onCancelSelectionClicked: () -> Unit,
    onAddLocationClicked: () -> Unit,
    onDeleteSelectedLocationsClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = isSelectionActive,
        transitionSpec = {
            val slideDirection =
                if (targetState) AnimatedContentScope.SlideDirection.Up else AnimatedContentScope.SlideDirection.Down
            (slideIntoContainer(slideDirection) + fadeIn()) with slideOutOfContainer(slideDirection) + fadeOut()
        }
    ) { selectionActive ->
        CenterAlignedTopAppBar(
            modifier = modifier,
            navigationIcon = {
                if (isSelectionActive) {
                    IconButton(onClick = onCancelSelectionClicked) {
                        Icon(
                            imageVector = Icons.Outlined.Cancel,
                            contentDescription = ""
                        )
                    }
                }
            },
            title = {
                val titleId = if (selectionActive) R.string.delete else R.string.locations_title
                Text(
                    text = stringResource(id = titleId),
                )
            },
            actions = {
                IconButton(
                    onClick = if (selectionActive) onDeleteSelectedLocationsClicked else onAddLocationClicked
                ) {
                    if (selectionActive) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "",
                            tint = Red
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.AddLocation,
                            contentDescription = ""
                        )
                    }
                }
            },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun LocationsList(
    data: List<LocationListItemData>,
    isDataLoading: Boolean,
    onItemClick: (LocationListItemData) -> Unit,
    onItemLongClick: (LocationListItemData) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = !isDataLoading,
        enter = fadeIn() + scaleIn(initialScale = 0.9f)
    ) {
        AnimatedContent(targetState = data.isEmpty()) { dataIsEmpty ->
            if (dataIsEmpty) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyListMessage()
                }
            } else {
                LazyColumn(
                    modifier = modifier,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(data) { locationListItem ->
                        LocationListItem(
                            data = locationListItem,
                            onClick = onItemClick,
                            onLongClick = onItemLongClick,
                            modifier = Modifier.animateItemPlacement()
                        )
                    }
                }
            }
        }
    }
}

// TODO: Localization
@Composable
private fun EmptyListMessage() {
    val addLocationIconId = "addLocationIcon"
    val text = buildAnnotatedString {
        append("Press ")
        appendInlineContent(addLocationIconId, "[add]")
        append(" to add a location")
    }
    val textStyle = MaterialTheme.typography.titleLarge
    val inlineContent = mapOf(
        addLocationIconId to
            InlineTextContent(
                Placeholder(
                    width = textStyle.fontSize,
                    height = textStyle.fontSize,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.AddLocation,
                    contentDescription = ""
                )
            }
    )
    Text(text = text, style = textStyle, inlineContent = inlineContent)
}

@Composable
fun LocationListItem(
    data: LocationListItemData,
    onClick: (LocationListItemData) -> Unit,
    onLongClick: (LocationListItemData) -> Unit,
    modifier: Modifier = Modifier
) {
    val cardScale by animateFloatAsState(targetValue = if (data.isSelected) 0.97f else 1f)
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .scale(cardScale)
            .clip(RoundedCornerShape(12.0.dp)) // clip to Outlined card shape to prevent click animation to draw out of card's bounds
            .combinedClickable(
                onClick = { onClick(data) },
                onLongClick = { onLongClick(data) }
            ),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = data.locationName.uppercase())
            AnimatedVisibility(
                visible = data.isSelected,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                SelectedLocationListItemIndicator()
            }
        }
    }
}

@Composable
private fun SelectedLocationListItemIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .size(12.dp)
            .background(Red)
    )
}