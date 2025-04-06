package com.example.sunny.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cloudy.utility.Constants
import com.example.sunny.data.local.PreferenceManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsPreferenceManager: PreferenceManager
): ViewModel() {

    val language = settingsPreferenceManager.languageSettings
        .stateIn(scope = viewModelScope, SharingStarted.WhileSubscribed(), initialValue = Constants.Lang.EN.toString())

    val windSpeed  = settingsPreferenceManager.windSpeedSettings
        .stateIn(scope = viewModelScope, SharingStarted.WhileSubscribed(), initialValue = Constants.METER_BY_SEC)

    val temperatureUnit = settingsPreferenceManager.temperatureSettings
        .stateIn(scope = viewModelScope, SharingStarted.WhileSubscribed(), initialValue =  Constants.Units.METRIC.toString())

    val themeMode = settingsPreferenceManager.themeSettings
        .stateIn(scope = viewModelScope, SharingStarted.WhileSubscribed(), initialValue = Constants.SYSTEM_THEME)

    fun setLanguage(lang: String){
        viewModelScope.launch {
            settingsPreferenceManager.updateLanguageSettings(lang)
        }
    }

    fun setWindSpeed(windSpeed: Boolean){
        viewModelScope.launch {
            settingsPreferenceManager.updateWindSpeedSettings(windSpeed)
        }
    }

    fun setTemperatureUnit(tempUnit: String){
        viewModelScope.launch {
            settingsPreferenceManager.updateTemperatureSettings(tempUnit)
        }
    }

    fun setThemeMode(mode: Int){
        viewModelScope.launch {
            settingsPreferenceManager.updateThemeSettings(mode)
        }
    }

}

class SettingsViewModelFactory(private val settingsPreferenceManager: PreferenceManager): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(settingsPreferenceManager) as T
    }
}