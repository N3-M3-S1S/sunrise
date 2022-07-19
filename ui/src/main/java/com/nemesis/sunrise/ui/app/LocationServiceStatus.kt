package com.nemesis.sunrise.ui.app

import android.content.Context
import androidx.core.content.getSystemService
import androidx.core.location.LocationManagerCompat

enum class LocationServiceStatus {
    ENABLED, DISABLED;

    companion object {
        fun getCurrentStatus(context: Context): LocationServiceStatus =
            if (LocationManagerCompat.isLocationEnabled(context.getSystemService()!!)) ENABLED else DISABLED
    }
}
