package com.example.sunny.alarm.screens

import kotlin.time.Duration

data class WeatherAlert(
    val duration: Duration,
    val alertType: AlertType,
    val latitude: Double,
    val longitude: Double
) {
    enum class AlertType {
        NOTIFICATION, ALARM_SOUND
    }
}
