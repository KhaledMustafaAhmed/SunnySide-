package com.example.sunny.fav

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sunny.data.model.entities.FavoritePlaceEntity
import com.example.sunny.data.repo.WeatherRepository
import com.example.sunny.utility.ResultResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavouriteDetailsViewModel(
    private val weatherRepository: WeatherRepository
) : ViewModel(){

    private val _uiState = MutableStateFlow<ResultResponse<FavoritePlaceEntity>>(ResultResponse.Loading)
     val uiState = _uiState.asStateFlow()

    fun getFavByPlaceId(placeId: Long){
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                weatherRepository.getPlaceById(placeId)
            }.onSuccess {
                _uiState.value = ResultResponse.Success(it)
            }.onFailure {
                _uiState.value = ResultResponse.Failure("${it.localizedMessage}")
            }
        }
    }
}

class FavouriteDetailsViewModelFactory(
    private val weatherRepository: WeatherRepository
): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavouriteDetailsViewModel(weatherRepository) as T
    }
}