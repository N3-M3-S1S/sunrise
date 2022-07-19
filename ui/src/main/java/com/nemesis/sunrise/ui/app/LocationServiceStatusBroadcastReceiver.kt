package com.nemesis.sunrise.ui.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import com.nemesis.sunrise.ui.di.AppModule
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class LocationServiceStatusBroadcastReceiver : BroadcastReceiver() {
    @Inject
    @Named(AppModule.locationServiceStatusMutableStateFlow)
    lateinit var locationEnabledStatusFlow: MutableStateFlow<LocationServiceStatus>

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent!!.action == LocationManager.PROVIDERS_CHANGED_ACTION) {
            locationEnabledStatusFlow.update { LocationServiceStatus.getCurrentStatus(context!!) }
        }
    }
}
