package com.example.sunny.data.local.room

import androidx.room.TypeConverter
import com.example.sunny.data.model.pojos.WeatherResponse
import com.google.gson.Gson

class TypeConverter {
    @TypeConverter
    fun fromWeatherResponseToString(weatherResponse: WeatherResponse): String = Gson().toJson(weatherResponse)
    @TypeConverter
    fun fromStringToWeatherResponse(weatherString: String): WeatherResponse = Gson().fromJson(weatherString, WeatherResponse::class.java)
}