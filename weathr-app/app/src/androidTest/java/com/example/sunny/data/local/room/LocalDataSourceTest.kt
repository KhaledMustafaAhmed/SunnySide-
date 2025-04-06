package com.example.sunny.data.local.room

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.sunny.data.model.entities.WeatherAlertEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@SmallTest
class LocalDataSourceTest {
    private lateinit var weatherAlertDao: WeatherAlertDao
    private lateinit var appDatabase: AppDatabase
    private  var favouriteDao: DummyFavouriteDao = DummyFavouriteDao()
    private lateinit var localDataSource: LocalDataSource

    @Before
    fun setup(){
        appDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()

        weatherAlertDao = appDatabase.weatherAlertDao()
        localDataSource =  LocalDataSourceImpl(weatherAlertDao, favouriteDao)
    }


    @After
    fun closeDB() = appDatabase.close()

    @Test
    fun getAllAlertsTest() = runTest {
        // Given
        val alert1 = WeatherAlertEntity("1", 1.1, 2.2, "US")
        val alert2 = WeatherAlertEntity("2", 1.1, 2.2, "UK")
        localDataSource.addAlert(alert1)
        localDataSource.addAlert(alert2)

        // When
        val alerts = localDataSource.getAllAlerts().first()

        // Then
        assert(alerts.contains(alert1))
        assert(alerts.contains(alert2))
    }

    @Test
    fun addAlertTest() = runBlocking{
        // Given
        val alert = WeatherAlertEntity("1", 1.1, 2.2, "London")

        // When
        localDataSource.addAlert(alert)
        val alerts = localDataSource.getAllAlerts().first()

        // Then
        assert(alerts.contains(alert))
    }

    @Test
    fun deleteAlertTest() = runBlocking{
        // Given
        val alert = WeatherAlertEntity("1", 1.1, 2.2, "Paris")
        localDataSource.addAlert(alert)

        // When
        localDataSource.deleteAlert(alert)
        val alerts = localDataSource.getAllAlerts().first()

        // Then
        assert(!alerts.contains(alert))
    }
}

/*
*
*
* suspend fun getAllAlerts(): Flow<List<WeatherAlertEntity>>

    suspend fun addAlert(alert: WeatherAlertEntity)

    suspend fun deleteAlert(alert: WeatherAlertEntity)

    suspend fun insertPlace(favPlace: FavoritePlaceEntity)

    suspend fun deletePlace(favPlace: FavoritePlaceEntity)

    suspend fun getAllFavourites(): Flow<List<FavoritePlaceEntity>>

    suspend fun updateCashedWeather(id: Long, weatherCashed: WeatherResponse)*/