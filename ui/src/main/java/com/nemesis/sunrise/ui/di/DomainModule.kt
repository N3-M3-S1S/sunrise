package com.nemesis.sunrise.ui.di

import com.nemesis.sunrise.data.location.DefaultLocationNameStore
import com.nemesis.sunrise.data.location.database.LocationsDao
import com.nemesis.sunrise.data.location.database.LocationsDatabaseRepository
import com.nemesis.sunrise.domain.location.LocationsRepository
import com.nemesis.sunrise.domain.location.usecase.CheckLocationNameAvailable
import com.nemesis.sunrise.domain.location.usecase.DeleteLocations
import com.nemesis.sunrise.domain.location.usecase.GetLocationByName
import com.nemesis.sunrise.domain.location.usecase.GetLocations
import com.nemesis.sunrise.domain.location.usecase.ReduceCoordinateExcessAccuracy
import com.nemesis.sunrise.domain.location.usecase.SaveLocation
import com.nemesis.sunrise.domain.location.usecase.ValidateLatitude
import com.nemesis.sunrise.domain.location.usecase.ValidateLongitude
import com.nemesis.sunrise.domain.sun.SolarEventCalculator
import com.nemesis.sunrise.domain.sun.usecase.GetDayTime
import com.nemesis.sunrise.domain.sun.usecase.GetSolarNoon
import com.nemesis.sunrise.domain.sun.usecase.GetTwilights
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    fun providesSolarEventsCalculator(): SolarEventCalculator = SolarEventCalculator()

    @Provides
    fun providesGetDayTime(solarEventCalculator: SolarEventCalculator): GetDayTime =
        GetDayTime(solarEventCalculator)

    @Provides
    fun providesGetSolarNoon(solarEventCalculator: SolarEventCalculator): GetSolarNoon =
        GetSolarNoon(solarEventCalculator)

    @Provides
    fun providesGetTwilights(solarEventCalculator: SolarEventCalculator): GetTwilights =
        GetTwilights(solarEventCalculator)

    @Provides
    fun providesCheckLocationNameAvailable(locationsRepository: LocationsRepository) =
        CheckLocationNameAvailable(locationsRepository)

    @Provides
    fun providesDeleteLocations(locationsRepository: LocationsRepository) =
        DeleteLocations(locationsRepository)

    @Provides
    fun providesGetLocationByName(locationsRepository: LocationsRepository) =
        GetLocationByName(locationsRepository)

    @Provides
    fun providesLatitudeStringValidation() = ValidateLatitude()

    @Provides
    fun providesLongitudeStringValidation() = ValidateLongitude()

    @Provides
    fun providesSaveLocation(
        locationsRepository: LocationsRepository,
        reduceCoordinateExcessAccuracy: ReduceCoordinateExcessAccuracy
    ): SaveLocation =
        SaveLocation(locationsRepository, reduceCoordinateExcessAccuracy)

    @Provides
    @Singleton
    fun providesLocationRepository(
        locationsDao: LocationsDao,
        defaultLocationNameStore: DefaultLocationNameStore
    ): LocationsRepository =
        LocationsDatabaseRepository(locationsDao, defaultLocationNameStore)

    @Provides
    fun providesGetLocation(locationsRepository: LocationsRepository): GetLocations =
        GetLocations(locationsRepository)

    @Provides
    fun providesScaleCoordinateDecimalPrecision() = ReduceCoordinateExcessAccuracy()
}
