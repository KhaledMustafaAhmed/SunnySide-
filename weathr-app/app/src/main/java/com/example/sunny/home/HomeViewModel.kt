package com.example.sunny.home

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sunny.data.model.pojos.WeatherResponse
import com.example.sunny.data.repo.WeatherRepository
import com.example.sunny.utility.ResultResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(
    private val weatherRepository: WeatherRepository
): ViewModel() {

    private val _currentWeather = MutableStateFlow<ResultResponse<WeatherResponse>>(ResultResponse.Loading)
    val currentWeather = _currentWeather.asStateFlow()

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation = _currentLocation.asStateFlow()

    fun updateLocation(location: Location) {
        viewModelScope.launch {
            _currentLocation.value = location
        }
    }

    fun getCurrentWeather(lat: String, lon: String) {
        viewModelScope.launch(Dispatchers.IO) {

            weatherRepository.getCurrentWeather(lat, lon)
                .catch { e ->
                    Log.e("WeatherError", "Failed to get weather", e)
                    _currentWeather.value = ResultResponse.Failure(e.toString())
                }
                .collect {
                    _currentWeather.value = ResultResponse.Success(it)
                    Log.d("WeatherSuccess", "Received weather data: $it")
                }
        }
    }
}

class HomeViewModelFactory(private val weatherRepository: WeatherRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(weatherRepository) as T
    }
}