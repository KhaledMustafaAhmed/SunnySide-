package com.example.sunny.data.remote

import com.example.sunny.data.model.pojos.WeatherResponse
import com.example.cloudy.data.remote.ApiService
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class RemoteDataSourceImpl(private val apiService: ApiService): RemoteDataSource {
    override suspend fun getCurrentWeather(
        lat: String,
        lon: String,
        lang: String
    ): Flow<WeatherResponse>  {
        return flowOf(apiService.getCurrentWeather(lat = lat, lon = lon, lang = lang))
    }

    override fun getPredictionsPlaces(
        placesClient: PlacesClient,
        query: String
    ): Task<FindAutocompletePredictionsResponse> {
        return placesClient.findAutocompletePredictions(FindAutocompletePredictionsRequest.builder().setQuery(query).build())
    }

    override fun getPlaceDetails(
        placesClient: PlacesClient,
        placeID: String
    ): Task<FetchPlaceResponse> {
        return placesClient.fetchPlace(
            FetchPlaceRequest.builder(placeID, listOf(
                Place.Field.LOCATION,Place.Field.FORMATTED_ADDRESS)).build())
    }
}