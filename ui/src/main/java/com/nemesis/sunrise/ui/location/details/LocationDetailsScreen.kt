@file:OptIn(ExperimentalMaterial3Api::class)

package com.nemesis.sunrise.ui.location.details

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nemesis.sunrise.ui.R
import com.nemesis.sunrise.ui.utils.LocalTime
import com.nemesis.sunrise.ui.utils.LocalTimeRange
import com.nemesis.sunrise.ui.utils.buildTimeString
import com.nemesis.sunrise.ui.utils.formatToString
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import kotlin.time.Duration
import androidx.compose.animation.AnimatedVisibility

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LocationDetailsScreen(
    locationDetails: LocationDetails,
    todayDetailsButtonVisible: Boolean,
    onTodayDetailsButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val contentPadding = 16.dp
    val contentPaddingInPixels = density.run { contentPadding.roundToPx() }
    var todayDetailsButtonHeightInPixels by remember { mutableStateOf(0) }

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(
                visible = todayDetailsButtonVisible,
                enter = slideInVertically { it + contentPaddingInPixels },
                exit = slideOutVertically { it + contentPaddingInPixels }
            ) {
                TodayDetailsButton(
                    onClick = onTodayDetailsButtonClicked,
                    modifier = Modifier.onGloballyPositioned {
                        todayDetailsButtonHeightInPixels = it.size.height
                    })
            }
        }) {
        Column(
            modifier = modifier
                .padding(contentPadding)
                .verticalScroll(rememberScrollState())
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
        ) {
            Date(date = locationDetails.date)

            DayTimeDetails(
                dayTime = locationDetails.dayTime,
                dayDuration = locationDetails.dayDuration,
                solarNoonTime = locationDetails.solarNoonTime
            )

            Twilights(
                civilTwilight = locationDetails.civilTwilight,
                nauticalTwilight = locationDetails.nauticalTwilight,
                astronomicalTwilight = locationDetails.astronomicalTwilight,
            )

            if (todayDetailsButtonVisible) { //add space to prevent today details button from covering the content
                Spacer(modifier = Modifier.height(density.run { todayDetailsButtonHeightInPixels.toDp() }))
            }
        }
    }
}

@Composable
private fun TodayDetailsButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    ExtendedFloatingActionButton(
        modifier = modifier,
        text = { Text(stringResource(id = R.string.today_details)) },
        icon = {
            Icon(
                imageVector = Icons.Default.Today,
                contentDescription = stringResource(id = R.string.today_details)
            )
        },
        onClick = onClick
    )
}

@Composable
private fun Date(date: LocalDate, modifier: Modifier = Modifier) {
    OutlinedCard(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            DateTitle(date = date)
            DayOfWeek(dayOfWeek = date.dayOfWeek)
        }
    }
}

@Composable
private fun DateTitle(date: LocalDate, modifier: Modifier = Modifier) {
    val dateText = remember(date) { date.formatToString() }
    Text(text = dateText, style = MaterialTheme.typography.titleLarge, modifier = modifier)
}

@Composable
private fun DayOfWeek(dayOfWeek: DayOfWeek, modifier: Modifier = Modifier) {
    val dayOfWeekText = remember(dayOfWeek) {
        dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
    }
    Text(text = dayOfWeekText, modifier = modifier)
}

@Composable
private fun DayTimeDetails(
    dayTime: LocalTimeRange?,
    dayDuration: Duration?,
    solarNoonTime: LocalTime,
    modifier: Modifier = Modifier
) {
    OutlinedCard(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.day_details),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            )

            Sunrise(sunriseTime = dayTime?.start)
            Sunset(sunsetTime = dayTime?.end)
            DayDuration(dayDuration = dayDuration)
            Zenith(zenithTime = solarNoonTime)
        }
    }
}

@Composable
private fun Sunrise(sunriseTime: LocalTime?, modifier: Modifier = Modifier) {
    DayTimeDetails(
        iconResId = R.drawable.ic_sunrise,
        title = stringResource(id = R.string.sunrise),
        time = sunriseTime.formatToString(),
        modifier = modifier
    )
}

@Composable
private fun Sunset(sunsetTime: LocalTime?, modifier: Modifier = Modifier) {
    DayTimeDetails(
        iconResId = R.drawable.ic_sunset,
        title = stringResource(id = R.string.sunset),
        time = sunsetTime.formatToString(),
        modifier = modifier
    )
}

@Composable
private fun DayDuration(modifier: Modifier = Modifier, dayDuration: Duration?) {
    val dayDurationText = dayDuration?.toComponents { hours, minutes, seconds, _ ->
        buildTimeString(hours = hours.toInt(), minutes = minutes, seconds = seconds)
    } ?: stringResource(id = R.string.not_available)

    DayTimeDetails(
        modifier = modifier,
        iconResId = R.drawable.ic_day_duration,
        title = stringResource(id = R.string.duration),
        time = dayDurationText
    )
}

@Composable
private fun Zenith(zenithTime: LocalTime, modifier: Modifier = Modifier) {
    DayTimeDetails(
        iconResId = R.drawable.ic_zenith,
        title = stringResource(id = R.string.zenith),
        time = zenithTime.formatToString(),
        modifier = modifier
    )
}

@Composable
private fun DayTimeDetails(
    modifier: Modifier = Modifier,
    @DrawableRes
    iconResId: Int,
    title: String,
    time: String
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        TextWithIcon(iconResId = iconResId, text = title)
        Text(text = time)
    }
}

@Composable
private fun Twilights(
    civilTwilight: LocalTimeRange?,
    nauticalTwilight: LocalTimeRange?,
    astronomicalTwilight: LocalTimeRange?,
    modifier: Modifier = Modifier
) {
    OutlinedCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.twilights),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            )

            TwilightIntervalStartEndHeader()
            CivilTwilight(civilTwilightInterval = civilTwilight)
            NauticalTwilight(nauticalTwilightInterval = nauticalTwilight)
            AstronomicalTwilight(astronomicalTwilightInterval = astronomicalTwilight)
        }
    }
}

@Composable
private fun TwilightIntervalStartEndHeader(modifier: Modifier = Modifier) {
    Twilight(
        title = null,
        start = stringResource(id = R.string.start),
        end = stringResource(id = R.string.end),
        modifier = modifier
    )
}

@Composable
private fun CivilTwilight(
    civilTwilightInterval: LocalTimeRange?,
    modifier: Modifier = Modifier
) {
    TwilightInterval(
        iconResId = R.drawable.ic_twilight_civil,
        title = stringResource(id = R.string.twilight_civil),
        twilightInterval = civilTwilightInterval,
        modifier = modifier
    )
}

@Composable
private fun NauticalTwilight(
    nauticalTwilightInterval: LocalTimeRange?,
    modifier: Modifier = Modifier
) {
    TwilightInterval(
        iconResId = R.drawable.ic_twilight_nautical,
        title = stringResource(id = R.string.twilight_nautical),
        twilightInterval = nauticalTwilightInterval,
        modifier = modifier
    )
}

@Composable
private fun AstronomicalTwilight(
    astronomicalTwilightInterval: LocalTimeRange?,
    modifier: Modifier = Modifier
) {
    TwilightInterval(
        iconResId = R.drawable.ic_twilight_astronomical,
        title = stringResource(id = R.string.twilight_astronomical),
        twilightInterval = astronomicalTwilightInterval,
        modifier = modifier
    )
}

@Composable
private fun TwilightInterval(
    @DrawableRes
    iconResId: Int,
    title: String,
    twilightInterval: LocalTimeRange?,
    modifier: Modifier = Modifier,
) {
    Twilight(
        title = { TextWithIcon(iconResId = iconResId, text = title) },
        start = twilightInterval?.start.formatToString(),
        end = twilightInterval?.end.formatToString(),
        modifier = modifier
    )
}

@Composable
private fun Twilight(
    title: @Composable (() -> Unit)?,
    start: String,
    end: String,
    modifier: Modifier = Modifier
) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = modifier) {
        Box(modifier = Modifier.weight(0.5f)) { title?.invoke() }
        Text(
            text = start,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(0.25f)
        )
        Text(
            text = end,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(0.25f)
        )
    }
}

@Composable
private fun TextWithIcon(
    @DrawableRes iconResId: Int,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = "",
        )
        Text(text = text)
    }
}
