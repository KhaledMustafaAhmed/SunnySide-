package com.example.sunny.map

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cloudy.utility.toLocation
import com.example.sunny.data.model.entities.FavoritePlaceEntity
import com.example.sunny.data.repo.WeatherRepository
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException

@OptIn(FlowPreview::class)
class MapViewModel(
    private val weatherRepository: WeatherRepository,
    private val placesClient: PlacesClient
): ViewModel() {
    private val _predictionsSearch = MutableSharedFlow<String>(replay = 1)

    private val _predictionsResult = MutableStateFlow<List<AutocompletePrediction>>(emptyList())
    val predictionsResult = _predictionsResult.asStateFlow()

    private val _placeDetails = MutableStateFlow<Location>(
        Location("").apply {
            latitude = 25.326
            longitude = 32.015
        }
    )
    val placeDetails = _placeDetails.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _predictionsSearch
                .debounce(1000)
                .collect{getPredictionPlaces(placesClient, it)}
        }
    }

    fun updateLocation(latLng: LatLng) {
        _placeDetails.value = Location("").apply {
            latitude = latLng.latitude
            longitude = latLng.longitude
        }
    }

    fun onQueryChange(query: String){
        viewModelScope.launch {
            _predictionsSearch.emit(query)
        }
    }

    fun getPredictionPlaces(placesClient: PlacesClient, query: String ) {
        weatherRepository.getPredictionsPlaces(placesClient, query)
            .addOnSuccessListener {
                _predictionsResult.value = it.autocompletePredictions
            }
            .addOnFailureListener{
                _predictionsResult.value = emptyList()
                Log.d("TAG", "getPredictionPlaces: addOnFailureListener")
            }
    }

    fun getPlacesDetails(placeId: String) {
            viewModelScope.launch {
                try {
                    weatherRepository.getPlaceDetails(placesClient, placeId)
                        .addOnSuccessListener { response ->
                        val place = response.place
                        _placeDetails.value = place.location.toLocation()

                        val address = place.address ?: "Unknown address"
                        Log.d("PlaceDetails", "Address: $address")
                    }.addOnFailureListener { exception ->
                        Log.e("PlaceDetails", "Place not found: ${exception.message}")
                    }
                } catch (e: Exception) {
                    Log.e("PlaceDetails", "Failed to fetch place", e)
                }
            }
        }

    fun addToFav(latlng: LatLng, cityName: String) {
        viewModelScope.launch {
            try {
                val cashedWeather = weatherRepository.getCurrentWeather(
                    latlng.latitude.toString(),
                    latlng.longitude.toString()
                ).first()

                val favPlaceEntity = FavoritePlaceEntity(
                    latitude = latlng.latitude,
                    longitude = latlng.longitude,
                    weatherCashed = cashedWeather,
                    cityName = cityName
                )

                weatherRepository.insertPlace(favPlaceEntity)
            } catch (e: IOException) {
                Log.e("addToFav", "Network error: ${e.message}")
            } catch (e: Exception) {
                Log.e("addToFav", "Unexpected error: ${e.message}")
            }
        }
    }
}

class MapViewModelFactory(
    private val weatherRepository: WeatherRepository,
    private val placesClient: PlacesClient
): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapViewModel(weatherRepository, placesClient) as T
    }
}