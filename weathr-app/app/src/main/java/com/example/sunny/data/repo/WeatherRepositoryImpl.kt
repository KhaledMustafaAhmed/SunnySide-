package com.example.sunny.data.repo

import com.example.sunny.data.local.room.LocalDataSource
import com.example.sunny.data.model.entities.FavoritePlaceEntity
import com.example.sunny.data.model.entities.WeatherAlertEntity
import com.example.sunny.data.model.pojos.WeatherResponse
import com.example.sunny.data.remote.RemoteDataSource
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.flow.Flow

class WeatherRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
    ): WeatherRepository {
    override suspend fun getCurrentWeather(
        lat: String,
        lon: String,
        lang: String
    ): Flow<WeatherResponse> = remoteDataSource.getCurrentWeather(lat = lat, lon = lon, lang = lang)

    override suspend fun getAllAlerts(): Flow<List<WeatherAlertEntity>> = localDataSource.getAllAlerts()

    override suspend fun addAlert(alert: WeatherAlertEntity) = localDataSource.addAlert(alert)

    override suspend fun deleteAlert(alert: WeatherAlertEntity) = localDataSource.deleteAlert(alert)

    override suspend fun insertPlace(favPlace: FavoritePlaceEntity) = localDataSource.insertPlace(favPlace)

    override suspend fun deletePlace(favPlace: FavoritePlaceEntity) = localDataSource.deletePlace(favPlace)

    override suspend fun getAllFavourites(): Flow<List<FavoritePlaceEntity>> = localDataSource.getAllFavourites()

    override suspend fun updateCashedWeather(id: Long, weatherCashed: WeatherResponse) = localDataSource.updateCashedWeather(id , weatherCashed)

    override suspend fun getPlaceById(placeId: Long): FavoritePlaceEntity = localDataSource.getPlaceById(placeId)

    override fun getPredictionsPlaces(
        placesClient: PlacesClient,
        query: String
    ): Task<FindAutocompletePredictionsResponse> = remoteDataSource.getPredictionsPlaces(placesClient,query)

    override fun getPlaceDetails(
        placesClient: PlacesClient,
        placeID: String
    ): Task<FetchPlaceResponse> = remoteDataSource.getPlaceDetails(placesClient, placeID)

}