package com.nemesis.sunrise.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun BottomSheetContent(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    title: String = ""
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        BottomSheetExpandIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        BottomSheetTitle(title = title, modifier = Modifier.align(Alignment.CenterHorizontally))
        content()
    }
}

@Composable
private fun BottomSheetExpandIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(32.dp, 4.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
    )
}

@Composable
private fun BottomSheetTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(modifier = modifier, text = title, style = MaterialTheme.typography.titleLarge)
}
