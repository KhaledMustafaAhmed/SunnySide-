package com.example.sunny.alarm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sunny.data.model.entities.WeatherAlertEntity
import com.example.sunny.data.repo.WeatherRepository
import com.example.sunny.utility.ResultResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AlarmViewModel(
    private val weatherRepository: WeatherRepository
): ViewModel(){

    private val _alertList = MutableStateFlow<ResultResponse<List<WeatherAlertEntity>>>(ResultResponse.Loading)
    val alertList = _alertList.asStateFlow()

    init {
        getAllAlerts()
    }

    fun getAllAlerts(){
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.getAllAlerts()
                .collect{
                    _alertList.value = ResultResponse.Success(it)
                }
        }
    }

    fun addAlert(alert: WeatherAlertEntity){
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.addAlert(alert)
        }
    }

    fun deleteAlert(alert: WeatherAlertEntity){
        viewModelScope.launch(Dispatchers.IO) {
            weatherRepository.deleteAlert(alert)
        }
    }

    var showDialog by mutableStateOf(false)

    fun showAddAlertDialog() {
        showDialog = true
    }

    fun dismissDialog() {
        showDialog = false
    }
}

class AlarmViewModelFactory(
    private val weatherRepository: WeatherRepository
): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AlarmViewModel(weatherRepository) as T
    }
}