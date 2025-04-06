package com.example.cloudy.data.remote

import android.util.Log
import com.example.cloudy.utility.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val NETWORK_LOG_TAG = "RetrofitHelper"
object RetrofitHelper {
    val retrofitService = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ApiService::class.java)

    private fun getHttpClient() = OkHttpClient.Builder().apply {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.i(NETWORK_LOG_TAG, message)
        }
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        addInterceptor(loggingInterceptor)
    }.build()
}

