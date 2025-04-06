package com.example.sunny.data.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.sunny.data.model.entities.WeatherAlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherAlertDao {
    @Query("SELECT * FROM weather_alerts")
    fun getAllAlerts(): Flow<List<WeatherAlertEntity>>

    @Insert
    suspend fun addAlert(alert: WeatherAlertEntity)

    @Delete
    suspend fun deleteAlert(alert: WeatherAlertEntity)
}


