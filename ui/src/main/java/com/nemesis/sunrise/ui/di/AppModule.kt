package com.nemesis.sunrise.ui.di

import android.content.Context
import com.nemesis.sunrise.data.database.SunriseDatabase
import com.nemesis.sunrise.ui.app.LocationServiceStatus
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {

    companion object {
        const val locationServiceStatusStateFlow = "locationServiceStatus"
        const val locationServiceStatusMutableStateFlow = "locationServiceMutableStatus"

        @Provides
        @Singleton
        @Named(locationServiceStatusMutableStateFlow)
        fun providesLocationStatusMutableStateFlow(@ApplicationContext appContext: Context): MutableStateFlow<LocationServiceStatus> =
            MutableStateFlow(LocationServiceStatus.getCurrentStatus(appContext))

        @Provides
        @Singleton
        fun providesDatabase(@ApplicationContext appContext: Context): SunriseDatabase =
            SunriseDatabase.create(appContext)
    }

    @Binds
    @Singleton
    @Named(locationServiceStatusStateFlow)
    fun providesLocationServiceStatusStateFlow(@Named(locationServiceStatusMutableStateFlow) f: MutableStateFlow<LocationServiceStatus>): StateFlow<LocationServiceStatus>
}
