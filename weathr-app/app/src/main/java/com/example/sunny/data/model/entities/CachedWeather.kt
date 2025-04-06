package com.example.sunny.data.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.sunny.data.model.pojos.WeatherResponse

@Entity(tableName = "current_weather")
data class CachedWeather(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val weatherResponse: WeatherResponse
)


