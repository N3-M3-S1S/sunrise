package com.nemesis.sunrise.data.location

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DefaultLocationNameStore(private val sharedPreferences: SharedPreferences) {
    private val defaultLocationNameKey = "defaultLocation"
    private val _defaultLocationNameFlow: MutableStateFlow<String?> =
        MutableStateFlow(sharedPreferences.getString(defaultLocationNameKey, null))

    val defaultLocationNameStateFlow: StateFlow<String?> = _defaultLocationNameFlow

    fun setDefaultLocationName(locationName: String) {
        sharedPreferences.edit {
            putString(defaultLocationNameKey, locationName)
            _defaultLocationNameFlow.value = locationName
        }
    }

    fun clearDefaultLocationName() {
        sharedPreferences.edit {
            remove(defaultLocationNameKey)
            _defaultLocationNameFlow.value = null
        }
    }

}