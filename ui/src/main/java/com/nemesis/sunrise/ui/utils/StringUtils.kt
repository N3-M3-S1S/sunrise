package com.nemesis.sunrise.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nemesis.sunrise.ui.R

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

@Composable
fun notAvailableString(): String = stringResource(id = R.string.not_available)
