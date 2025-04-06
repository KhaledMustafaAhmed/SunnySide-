package com.example.sunny.data.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.sunny.data.model.pojos.WeatherResponse

@Entity(tableName = "favorite_places")
data class FavoritePlaceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val cityName: String,
    val lastUpdated: Long = System.currentTimeMillis(),
    val weatherCashed: WeatherResponse
)