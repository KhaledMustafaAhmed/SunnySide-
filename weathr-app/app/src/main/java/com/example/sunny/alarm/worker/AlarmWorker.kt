package com.example.sunny.alarm.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.cloudy.data.remote.RetrofitHelper
import com.example.cloudy.utility.getLocationName
import com.example.sunny.MainActivity
import com.example.sunny.R
import com.example.sunny.alarm.screens.WeatherAlert
import com.example.sunny.data.local.room.AppDatabase
import com.example.sunny.data.local.room.LocalDataSourceImpl
import com.example.sunny.data.model.entities.WeatherAlertEntity
import com.example.sunny.data.model.pojos.WeatherResponse
import com.example.sunny.data.remote.RemoteDataSourceImpl
import com.example.sunny.data.repo.WeatherRepositoryImpl
import kotlinx.coroutines.flow.first
import java.util.Locale

class AlarmWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val alertType = inputData.getString("alertType")
            ?.let { WeatherAlert.AlertType.valueOf(it) }
            ?: return Result.failure()

        val latitude = inputData.getDouble("latitude", 0.0)
        val longitude = inputData.getDouble("longitude", 0.0)
        val locationName = getLocationName(latitude, longitude, applicationContext) ?: "Unknown Location"

        val weatherRepository = WeatherRepositoryImpl(
            RemoteDataSourceImpl(RetrofitHelper.retrofitService),
            LocalDataSourceImpl(
                AppDatabase.getDatabase(context = applicationContext).weatherAlertDao(),
                AppDatabase.getDatabase(context = applicationContext).FavouritesDao()
                )
        )

        val weatherResponse = try {
            weatherRepository.getCurrentWeather(
                lat = latitude.toString(),
                lon = longitude.toString(),
                lang = Locale.getDefault().language
            ).first()
        } catch (e: Exception) {
            Log.e("AlarmWorker", "Error fetching weather data", e)
            null
        }
        Log.d("TAG", "doWork: ${weatherResponse}")
        when (alertType) {
            WeatherAlert.AlertType.NOTIFICATION -> showNotification(locationName, weatherResponse)
            WeatherAlert.AlertType.ALARM_SOUND -> {}
        }

        weatherRepository.deleteAlert(
            WeatherAlertEntity(
                workManagerRequestId = id.toString(),
                latitude = latitude,
                longitude = longitude,
                cityName = locationName
            )
        )

        return Result.success()
    }

    private suspend fun showNotification(locationName: String, weatherResponse: WeatherResponse?) {
        val notificationManager = NotificationManagerCompat.from(applicationContext)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "weather_alerts",
                "Weather Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Weather alert notifications"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationContent = if (weatherResponse != null) {
            val currentWeather = weatherResponse.current
            val temp = currentWeather.temp.let { "%.1f°C".format(it) }
            val description = currentWeather.weather.firstOrNull()?.description ?: "No weather data"

            "Current: $temp, $description"
        } else {
            "Weather data unavailable"
        }

        val notification = NotificationCompat.Builder(applicationContext, "weather_alerts")
            .setContentTitle("Weather Alert for $locationName!")
            .setContentText(notificationContent)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Notification!\n$notificationContent"))
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
            .build()

        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(System.currentTimeMillis().toInt(), notification)
        }
    }

}