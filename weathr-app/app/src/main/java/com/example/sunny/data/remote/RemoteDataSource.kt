package com.example.sunny.data.remote

import com.example.sunny.data.model.pojos.WeatherResponse
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.flow.Flow

interface RemoteDataSource {
    suspend fun getCurrentWeather(
        lat: String,
        lon: String,
        lang: String
    ):Flow<WeatherResponse>

    fun getPredictionsPlaces(
        placesClient: PlacesClient,
        query: String
    ): Task<FindAutocompletePredictionsResponse>

    fun getPlaceDetails(
        placesClient: PlacesClient,
        placeID: String
    ): Task<FetchPlaceResponse>
}