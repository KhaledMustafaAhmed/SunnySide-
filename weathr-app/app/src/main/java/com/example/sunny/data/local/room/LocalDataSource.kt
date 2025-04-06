package com.example.sunny.data.local.room

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.sunny.data.model.entities.FavoritePlaceEntity
import com.example.sunny.data.model.entities.WeatherAlertEntity
import com.example.sunny.data.model.pojos.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    suspend fun getAllAlerts(): Flow<List<WeatherAlertEntity>>

    suspend fun addAlert(alert: WeatherAlertEntity)

    suspend fun deleteAlert(alert: WeatherAlertEntity)

    suspend fun insertPlace(favPlace: FavoritePlaceEntity)

    suspend fun deletePlace(favPlace: FavoritePlaceEntity)

    suspend fun getAllFavourites(): Flow<List<FavoritePlaceEntity>>

    suspend fun updateCashedWeather(id: Long, weatherCashed: WeatherResponse)

    suspend fun getPlaceById(placeId: Long): FavoritePlaceEntity

}