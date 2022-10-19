package com.nemesis.sunrise.ui.launcher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nemesis.sunrise.data.location.DefaultLocationNameStore
import com.nemesis.sunrise.domain.location.Location
import com.nemesis.sunrise.domain.location.usecase.GetLocationByName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LauncherViewModel @Inject constructor(
    defaultLocationNameStore: DefaultLocationNameStore,
    getLocationByName: GetLocationByName
) : ViewModel() {

    private val _state: MutableStateFlow<LauncherState> =
        MutableStateFlow(LauncherState.Initializing)

    val state: StateFlow<LauncherState> = _state

    init {
        viewModelScope.launch {
            val defaultLocationName = defaultLocationNameStore.defaultLocationNameStateFlow.value
            val defaultLocation: Location? = defaultLocationName?.let { getLocationByName(it) }
            _state.value = LauncherState.Ready(defaultLocation = defaultLocation)
        }
    }
}
