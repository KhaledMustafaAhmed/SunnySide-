package com.example.sunny.data.local.room

import com.example.sunny.data.model.entities.FavoritePlaceEntity
import com.example.sunny.data.model.entities.WeatherAlertEntity
import com.example.sunny.data.model.pojos.WeatherResponse
import kotlinx.coroutines.flow.Flow

class LocalDataSourceImpl(
    private val weatherAlertDao: WeatherAlertDao,
    private val favouritesDao: FavouritesDao
    ): LocalDataSource {

    override suspend fun getAllAlerts(): Flow<List<WeatherAlertEntity>> = weatherAlertDao.getAllAlerts()

    override suspend fun addAlert(alert: WeatherAlertEntity) = weatherAlertDao.addAlert(alert)

    override suspend fun deleteAlert(alert: WeatherAlertEntity) = weatherAlertDao.deleteAlert(alert)

    override suspend fun insertPlace(favPlace: FavoritePlaceEntity) = favouritesDao.insertPlace(favPlace)

    override suspend fun deletePlace(favPlace: FavoritePlaceEntity) = favouritesDao.deletePlace(favPlace)

    override suspend fun getAllFavourites(): Flow<List<FavoritePlaceEntity>> = favouritesDao.getAllFavourites()

    override suspend fun updateCashedWeather(id: Long, weatherCashed: WeatherResponse) = favouritesDao.updateCashedWeather(id, weatherCashed)

    override suspend fun getPlaceById(placeId: Long): FavoritePlaceEntity = favouritesDao.getPlaceById(placeId)
}