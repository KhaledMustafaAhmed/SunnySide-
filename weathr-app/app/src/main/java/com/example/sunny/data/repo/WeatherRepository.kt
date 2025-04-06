package com.example.sunny.data.repo

import com.example.sunny.data.model.pojos.WeatherResponse
import com.example.cloudy.utility.Constants
import com.example.sunny.data.model.entities.FavoritePlaceEntity
import com.example.sunny.data.model.entities.WeatherAlertEntity
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getCurrentWeather(
        lat: String,
        lon: String,
        lang: String =  Constants.Lang.EN.toString()
    ): Flow<WeatherResponse>

    suspend fun getAllAlerts(): Flow<List<WeatherAlertEntity>>

    suspend fun addAlert(alert: WeatherAlertEntity)

    suspend fun deleteAlert(alert: WeatherAlertEntity)

    suspend fun insertPlace(favPlace: FavoritePlaceEntity)

    suspend fun deletePlace(favPlace: FavoritePlaceEntity)

    suspend fun getAllFavourites(): Flow<List<FavoritePlaceEntity>>

    suspend fun updateCashedWeather(id: Long, weatherCashed: WeatherResponse)

    suspend fun getPlaceById(placeId: Long): FavoritePlaceEntity


    fun getPredictionsPlaces(
        placesClient: PlacesClient,
        query: String
    ): Task<FindAutocompletePredictionsResponse>

    fun getPlaceDetails(
        placesClient: PlacesClient,
        placeID: String
    ): Task<FetchPlaceResponse>
}