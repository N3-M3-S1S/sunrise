package com.nemesis.sunrise.ui.utils

import kotlinx.datetime.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalTimeFormatter @Inject constructor() {

    fun formatToString(localTime: LocalTime): String = buildTimeString(
        hours = localTime.hour,
        minutes = localTime.minute,
        seconds = localTime.second
    )
}
