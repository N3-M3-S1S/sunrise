package com.nemesis.sunrise.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ColorScheme = darkColorScheme(
    background = Color(0xff232739),
    surface = Color(0xff232739),
    surfaceVariant = Color(0xff232739),
    primary = Color(0xff6A8AF6),
    error = Red,
    onPrimary = Color.Black
)

@Composable
fun SunriseTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColorScheme,
        content = content
    )
}
