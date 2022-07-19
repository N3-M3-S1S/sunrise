package com.nemesis.sunrise.ui.addlocation

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nemesis.sunrise.ui.R
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationStyle

@Destination(
    style = DestinationStyle.Dialog::class,
    navArgsDelegate = AddLocationScreenNavArgs::class
)
@Composable
fun AddLocationScreen(
    navigator: DestinationsNavigator,
    viewModel: AddLocationViewModel = hiltViewModel()
) {
    //move state to viewmodel?
    val state = AddLocationState(
        name = viewModel.name.collectAsState().value,
        isNameAvailable = viewModel.isNameAvailable.collectAsState().value,
        nameContainsOnlyAllowedCharacters = viewModel.nameContainsOnlyAllowedCharacters.collectAsState().value,
        latitude = viewModel.latitude.collectAsState().value,
        isLatitudeValid = viewModel.isLatitudeValid.collectAsState().value,
        longitude = viewModel.longitude.collectAsState().value,
        isLongitudeValid = viewModel.isLongitudeValid.collectAsState().value,
        isAddLocationAvailable = viewModel.isAddLocationAvailable.collectAsState().value,
    )

    val actions = remember {
        AddLocationActions(
            onNameChanged = viewModel::onNameChanged,
            onLatitudeChanged = viewModel::onLatitudeChanged,
            onLongitudeChanged = viewModel::onLongitudeChanged,
            onAddLocationClicked = viewModel::onAddLocationClicked,
            onCancelClicked = navigator::navigateUp
        )
    }

    val context = LocalContext.current

    LaunchedEffect(true) {
        viewModel.events.collect {
            Toast.makeText(context, R.string.location_added, Toast.LENGTH_SHORT).show()
            navigator.navigateUp()
        }
    }

    AddLocationContent(state = state, actions = actions)
}

@Composable
private fun AddLocationContent(state: AddLocationState, actions: AddLocationActions) {
    Surface(shape = MaterialTheme.shapes.extraLarge) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = stringResource(id = R.string.add_location_title),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Column(
                modifier = Modifier.padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AddLocationTextField(
                    labelText = stringResource(id = R.string.name),
                    text = state.name,
                    isError = !state.nameContainsOnlyAllowedCharacters || !state.isNameAvailable,
                    errorText = stringResource(id = if (!state.nameContainsOnlyAllowedCharacters) R.string.name_contains_forbidden_characters else R.string.location_with_name_exists),
                    onTextChanged = actions.onNameChanged,
                )
                AddLocationTextField(
                    labelText = stringResource(id = R.string.latitude),
                    text = state.latitude,
                    isError = !state.isLatitudeValid,
                    errorText = stringResource(id = R.string.latitude_invalid),
                    onTextChanged = actions.onLatitudeChanged,
                    keyboardType = KeyboardType.Number
                )
                AddLocationTextField(
                    labelText = stringResource(id = R.string.longitude),
                    text = state.longitude,
                    isError = !state.isLongitudeValid,
                    errorText = stringResource(id = R.string.longitude_invalid),
                    onTextChanged = actions.onLongitudeChanged,
                    keyboardType = KeyboardType.Number
                )
            }
            Row(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(onClick = actions.onCancelClicked) {
                    Text(text = stringResource(id = android.R.string.cancel))
                }
                TextButton(
                    onClick = actions.onAddLocationClicked,
                    enabled = state.isAddLocationAvailable
                ) {
                    Text(text = stringResource(id = android.R.string.ok))
                }
            }
        }
    }
}

@Composable
private fun AddLocationTextField(
    labelText: String,
    text: String,
    isError: Boolean,
    errorText: String,
    onTextChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Ascii,
) {
    OutlinedTextField(
        modifier = modifier,
        value = text,
        textStyle = MaterialTheme.typography.bodyMedium,
        onValueChange = onTextChanged,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        label = { Text(text = labelText) },
        isError = isError
    )
    if (isError) {
        Text(
            text = errorText,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(start = 16.dp)
        )
    } else {
        Spacer(modifier = Modifier.height(16.dp)) //make space for error text
    }
}
