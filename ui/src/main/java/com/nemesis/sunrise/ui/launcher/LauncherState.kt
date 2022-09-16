package com.nemesis.sunrise.ui.launcher

import com.nemesis.sunrise.domain.location.Location

sealed class LauncherState {
    object Initializing : LauncherState()

    data class Ready(val defaultLocation: Location? = null) : LauncherState()
}
