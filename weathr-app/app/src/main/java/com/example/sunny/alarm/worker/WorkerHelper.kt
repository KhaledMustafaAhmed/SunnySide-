package com.example.sunny.alarm.worker

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.cloudy.utility.getLocationName
import com.example.sunny.alarm.AlarmViewModel
import com.example.sunny.alarm.screens.WeatherAlert
import com.example.sunny.data.model.entities.WeatherAlertEntity
import java.util.UUID
import java.util.concurrent.TimeUnit

object WorkerHelper {
    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleAlert(
        alert: WeatherAlert,
        alarmViewModel: AlarmViewModel,
        workManager: WorkManager,
        context: Context
    ) {
        val cityName = getLocationName(
            alert.latitude ?: 0.0,
            alert.longitude ?: 0.0,
            context
        ) ?: "Unknown Location"

        val data = workDataOf(
            "alertType" to alert.alertType.name,
            "latitude" to (alert.latitude ?: 0.0),
            "longitude" to (alert.longitude ?: 0.0),
            "cityName" to (cityName)
        )

        val constraint = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<AlarmWorker>()
            .setInitialDelay(alert.duration.inWholeMilliseconds, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .setConstraints(constraint)
            .build()

        val newEntity = WeatherAlertEntity(
            workManagerRequestId = request.id.toString(),
            latitude = alert.latitude ?: 0.0,
            longitude = alert.longitude ?: 0.0,
            cityName = cityName,
        )

        alarmViewModel.addAlert(newEntity)
        workManager.enqueue(request)
    }

    fun cancelAlert(
        alert: WeatherAlertEntity,
        alarmViewModel: AlarmViewModel,
        workManager: WorkManager
    ) {
        alarmViewModel.deleteAlert(alert)
        workManager.cancelWorkById(UUID.fromString(alert.workManagerRequestId))
    }
}