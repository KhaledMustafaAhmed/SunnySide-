package com.example.sunny.data.local

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.cloudy.utility.Constants
import com.example.cloudy.utility.Constants.WEATHER_SETTINGS_PREFERENCES_NAME
import kotlinx.coroutines.flow.map
import okio.IOException

class PreferenceManager(
    private val context: Context
) {

    val Context.dataStore by preferencesDataStore(name = WEATHER_SETTINGS_PREFERENCES_NAME)

    suspend fun updateLanguageSettings(lang: String) {
        try{ context.dataStore.edit { it[LANGUAGE_KEY] = lang
            Log.d("TAG", "updateLanguageSettings: ${lang}")}
        }catch (e: IOException){
            Log.d("TAG", "updateLanguageSettings: IOException ${e.localizedMessage} ")
        }catch (e: Exception){
            Log.d("TAG", "updateLanguageSettings: Exception ${e.localizedMessage} ")
        }
    }

    val languageSettings = context.dataStore.data.map {
        it[LANGUAGE_KEY] ?: Constants.Lang.EN.toString()
    }

    suspend fun updateTemperatureSettings(temp: String) {
        try {context.dataStore.edit { it[TEMPERATURE_UNIT] = temp}
        }catch (e: IOException){
            Log.d("TAG", "updateTemperatureSettings: IOException ${e.localizedMessage} ")
        }catch (e: Exception){
            Log.d("TAG", "updateTemperatureSettings: Exception ${e.localizedMessage}")
        }
    }

    val temperatureSettings = context.dataStore.data.map { it[TEMPERATURE_UNIT] ?: Constants.Units.KELVIN.toString()}

    /* if true so it is the default meter/sec if false miles/hours */
    suspend fun updateWindSpeedSettings(speedByMeterBySec: Boolean) {
        try{context.dataStore.edit { it[WIND_SPEED_UNIT] = speedByMeterBySec }
        }catch (e: IOException){
            Log.d("TAG", "updateWindSpeedSettings: IOException ${e.localizedMessage} ")
        }catch (e: Exception){
            Log.d("TAG", "updateWindSpeedSettings: Exception ${e.localizedMessage}")
        }
    }

    val windSpeedSettings = context.dataStore.data.map {it[WIND_SPEED_UNIT] ?: Constants.METER_BY_SEC }

    suspend fun updateLocationSettings(locationByGPS: Boolean) {
        try{
            context.dataStore.edit { it [LOCATION] = locationByGPS }
        }catch (e: IOException){
            Log.d("TAG", "updateLocationSettings: IOException ${e.localizedMessage} ")
        }catch (e: Exception){
            Log.d("TAG", "updateLocationSettings: Exception ${e.localizedMessage}")
        }
    }

    val locationSettings = context.dataStore.data.map { it[LOCATION] ?: Constants.GPS_LOCATION }

    suspend fun updateThemeSettings(mode: Int) {
        try{ context.dataStore.edit { it[THEME_MODE] = mode }
        }catch (e: IOException){
            Log.d("TAG", "updateThemeSettings: IOException ${e.localizedMessage} ")
        }catch (e: Exception){
            Log.d("TAG", "updateThemeSettings: Exception ${e.localizedMessage}")
        }
    }

    val themeSettings = context.dataStore.data.map { it[THEME_MODE] ?: Constants.SYSTEM_THEME }

    companion object {
        val LANGUAGE_KEY = stringPreferencesKey("LANGUAGE_KEY")
        val TEMPERATURE_UNIT = stringPreferencesKey("TEMPERATURE_UNIT")
        val WIND_SPEED_UNIT = booleanPreferencesKey("WIND_SPEED_UNIT")
        val LOCATION = booleanPreferencesKey("LOCATION")
        val THEME_MODE = intPreferencesKey("THEME_MODE")

    }
}