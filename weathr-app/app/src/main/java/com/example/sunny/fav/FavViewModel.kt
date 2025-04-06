package com.example.sunny.fav

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sunny.data.model.entities.FavoritePlaceEntity
import com.example.sunny.data.model.entities.WeatherAlertEntity
import com.example.sunny.data.repo.WeatherRepository
import com.example.sunny.utility.ResultResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FavViewModel(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ResultResponse<List<FavoritePlaceEntity>>>(ResultResponse.Loading)
    val uiState = _uiState.asStateFlow()

    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog = _showDeleteDialog.asStateFlow()

    private val _placeToDelete = MutableStateFlow<FavoritePlaceEntity?>(null)
    val placeToDelete = _placeToDelete.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                weatherRepository.getAllFavourites()
                    .collect { favorites ->
                        _uiState.value = ResultResponse.Success(favorites)
                        Log.d("TAG", ": calling inside weatherRepository.getAllFavourites()")
                    }
            } catch (e: Exception) {
                Log.d("TAG", ": failed to get all favs ${e.localizedMessage} ")
                _uiState.value = ResultResponse.Failure(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun showDeleteDialog(place: FavoritePlaceEntity) {
        _placeToDelete.value = place
        _showDeleteDialog.value = true
    }

    fun dismissDeleteDialog() {
        _showDeleteDialog.value = false
        _placeToDelete.value = null
    }

    fun confirmDelete() {
        viewModelScope.launch(Dispatchers.IO) {
            _placeToDelete.value?.let { place ->
                try {
                    weatherRepository.deletePlace(place)
                } catch (e: Exception) {
                    _uiState.value = ResultResponse.Failure("Failed to delete: ${e.message}")
                }
            }
            dismissDeleteDialog()
        }
    }
}

class FavViewModelFactory(private val weatherRepository: WeatherRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavViewModel(weatherRepository) as T
    }
}
