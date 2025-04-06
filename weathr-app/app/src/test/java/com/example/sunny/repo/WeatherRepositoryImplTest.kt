package com.example.sunny.repo

import com.example.sunny.data.model.entities.WeatherAlertEntity
import com.example.sunny.data.remote.RemoteDataSource
import com.example.sunny.data.repo.WeatherRepositoryImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class WeatherRepositoryImplTest {
    private lateinit var fakeLocalDataSource: FakeLocalDataSource
    private lateinit var weatherRepository: WeatherRepositoryImpl

    @Before
    fun setup() {
        fakeLocalDataSource = FakeLocalDataSource()
        weatherRepository = WeatherRepositoryImpl(remoteDataSource = mock(RemoteDataSource::class.java), localDataSource = fakeLocalDataSource)
    }

    @Test
    fun getAllAlertsTest() = runTest {
        // Given
        val alert1 = WeatherAlertEntity("1", 1.1, 2.2, "US")
        val alert2 = WeatherAlertEntity("2", 1.1, 2.2, "UK")
        weatherRepository.addAlert(alert1)
        weatherRepository.addAlert(alert2)

        // When
        val alerts = weatherRepository.getAllAlerts().first()

        // Then
        assert(alerts.contains(alert1))
        assert(alerts.contains(alert2))
    }

    @Test
    fun addAlertTest() = runBlocking {
        // Given
        val alert = WeatherAlertEntity("1", 1.1, 2.2, "London")

        // When
        weatherRepository.addAlert(alert)

        // Then
        assert(weatherRepository.getAllAlerts().first().contains(alert))
    }

    @Test
    fun deleteAlertTest() = runBlocking {
        // Given
        val alert = WeatherAlertEntity("1", 1.1, 2.2, "Paris")
        weatherRepository.addAlert(alert)

        // When
        weatherRepository.deleteAlert(alert)

        // Then
        assertFalse(weatherRepository.getAllAlerts().first().contains(alert))
    }
}