package com.example.sunny.repo

import com.example.sunny.data.local.room.LocalDataSource
import com.example.sunny.data.model.entities.FavoritePlaceEntity
import com.example.sunny.data.model.entities.WeatherAlertEntity
import com.example.sunny.data.model.pojos.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalDataSource:LocalDataSource {

    private val alerts = mutableListOf<WeatherAlertEntity>()

    override suspend fun getAllAlerts(): Flow<List<WeatherAlertEntity>> = flowOf(alerts)

    override suspend fun addAlert(alert: WeatherAlertEntity) {
        alerts.add(alert)
    }

    override suspend fun deleteAlert(alert: WeatherAlertEntity) {
        alerts.remove(alert)
    }

    override suspend fun insertPlace(favPlace: FavoritePlaceEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun deletePlace(favPlace: FavoritePlaceEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun getAllFavourites(): Flow<List<FavoritePlaceEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateCashedWeather(id: Long, weatherCashed: WeatherResponse) {
        TODO("Not yet implemented")
    }

    override suspend fun getPlaceById(placeId: Long): Flow<FavoritePlaceEntity> {
        TODO("Not yet implemented")
    }
}