package com.nemesis.sunrise.ui.utils

fun buildTimeString(hours: Int? = null, minutes: Int? = null, seconds: Int? = null): String =
    buildString {
        fun timeUnitToString(timeUnit: Int): String =
            if (timeUnit < 10) "0$timeUnit" else timeUnit.toString()

        hours?.let {
            append(timeUnitToString(it))
            append(":")
        }

        minutes?.let {
            append(timeUnitToString(it))
            append(":")
        }

        seconds?.let {
            append(timeUnitToString(it))
        }
    }
