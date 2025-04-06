package com.example.sunny.data.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_alerts")
data class WeatherAlertEntity(
    @PrimaryKey
    val workManagerRequestId: String,
    val latitude: Double,
    val longitude: Double,
    val cityName: String,
)
