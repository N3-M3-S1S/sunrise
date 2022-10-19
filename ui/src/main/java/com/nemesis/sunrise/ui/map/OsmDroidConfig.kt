package com.nemesis.sunrise.ui.map

import android.content.Context
import com.nemesis.sunrise.ui.BuildConfig
import org.osmdroid.config.Configuration

object OsmDroidConfig {
    fun configure(context: Context) {
        with(Configuration.getInstance()) {
            load(context, context.getSharedPreferences(null, Context.MODE_PRIVATE))
            userAgentValue = BuildConfig.APPLICATION_ID
        }
    }
}
