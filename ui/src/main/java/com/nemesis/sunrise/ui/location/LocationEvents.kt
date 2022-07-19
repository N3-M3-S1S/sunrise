package com.nemesis.sunrise.ui.location

sealed class LocationEvents {
    object NavigateToDetailsScreen : LocationEvents()
    object ScrollCalendarListToTop : LocationEvents()
}
