package com.nemesis.sunrise.ui.app

import android.app.Application
import android.content.IntentFilter
import android.location.LocationManager
import com.nemesis.sunrise.ui.map.OsmDroidConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        OsmDroidConfig.configure(this)
        val locationStateBroadcastReceiver = LocationServiceStatusBroadcastReceiver()
        registerReceiver(
            locationStateBroadcastReceiver,
            IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
        )
    }
}
