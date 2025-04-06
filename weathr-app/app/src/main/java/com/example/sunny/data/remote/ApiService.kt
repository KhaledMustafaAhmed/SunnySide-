package com.example.cloudy.data.remote

import com.example.sunny.data.model.pojos.WeatherResponse
import com.example.cloudy.utility.Constants
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("onecall")
    suspend fun getCurrentWeather(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("lang") lang: String = Constants.Lang.EN.toString(),
        @Query("appid") appid: String = Constants.API_KEY,
        @Query("units") units: String = Constants.Units.METRIC.toString()
    ): WeatherResponse
}