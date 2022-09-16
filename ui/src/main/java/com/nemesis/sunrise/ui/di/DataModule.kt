package com.nemesis.sunrise.ui.di

import android.content.Context
import android.content.SharedPreferences
import com.nemesis.sunrise.data.database.SunriseDatabase
import com.nemesis.sunrise.data.location.CurrentCoordinatesProvider
import com.nemesis.sunrise.data.location.DefaultLocationNameStore
import com.nemesis.sunrise.data.location.database.LocationsDao
import com.nemesis.sunrise.domain.location.usecase.ReduceCoordinateExcessAccuracy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun providesLocationsDao(database: SunriseDatabase): LocationsDao =
        database.locationsDao()

    @Provides
    fun providesCurrentCoordinatesProvider(
        @ApplicationContext appContext: Context,
        reduceCoordinateExcessAccuracy: ReduceCoordinateExcessAccuracy
    ): CurrentCoordinatesProvider =
        CurrentCoordinatesProvider(appContext, reduceCoordinateExcessAccuracy)

    @Provides
    @Singleton
    fun providesDefaultLocationNameStore(
        sharedPreferences: SharedPreferences
    ): DefaultLocationNameStore = DefaultLocationNameStore(sharedPreferences)


    @Provides
    @Singleton
    fun providesSunriseDatabase(@ApplicationContext appContext: Context): SunriseDatabase =
        SunriseDatabase.create(appContext)

    @Provides
    @Singleton
    fun providesSharedPreferences(@ApplicationContext appContext: Context): SharedPreferences =
        appContext.getSharedPreferences(
            appContext.packageName + "_preferences",
            Context.MODE_PRIVATE
        )

}