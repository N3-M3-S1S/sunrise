package com.nemesis.sunrise.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationDisabled
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LocationDisabledIcon(modifier: Modifier = Modifier) = Icon(
    modifier = modifier,
    imageVector = Icons.Default.LocationDisabled,
    contentDescription = "Location disabled",
    tint = MaterialTheme.colorScheme.error
)
