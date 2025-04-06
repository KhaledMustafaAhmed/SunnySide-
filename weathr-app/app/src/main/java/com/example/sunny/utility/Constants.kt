package com.example.cloudy.utility

import android.app.Activity
import android.content.Context
import android.location.Address
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.recreate
import com.example.sunny.R
import com.yariksoffice.lingver.Lingver
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import android.location.Geocoder
import android.location.Location
import android.util.Log
import com.google.android.gms.maps.model.LatLng

@RequiresApi(Build.VERSION_CODES.O)
fun timestampToDate(timestamp: Long, pattern: String): String {
    return Instant.ofEpochSecond(timestamp)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern(pattern))
}

object Constants {
    const val PATTERNS_FULL_DATE_FOR_CURRENT = "EEEE, MMMM d"
    const val API_KEY = "02f7303416defaa054fd9589e2bd7ce2"
    const val BASE_URL = "https://api.openweathermap.org/data/3.0/"
    const val WEATHER_SETTINGS_PREFERENCES_NAME = "WEATHER_SETTINGS"
    const val SYSTEM_THEME = 0
    const val METER_BY_SEC = true
    const val GPS_LOCATION = true
    enum class Lang{EN,AR}
    enum class Units{KELVIN, IMPERIAL, METRIC}
}

fun Double.metersPerSecondToMilesPerHour(): Double {
    return this * 2.23694
}

fun Double.milesPerHourToMetersPerSecond(): Double {
    return this * 0.44704
}

fun Double.celsiusToKelvin(): Double = this + 273.15

fun Double.celsiusToFahrenheit(): Double = (this * 9/5) + 32

fun Double.roundToTwoDecimal(): Double = "%.2f".format(this).toDouble()

fun unitFromString(unit:String, num: Double): Double{
    return when (unit) {
        Constants.Units.METRIC.toString() -> num
        Constants.Units.IMPERIAL.toString() -> num.celsiusToFahrenheit()
        Constants.Units.KELVIN.toString() -> num.celsiusToKelvin()
        else -> num
    }
}

fun changeLanguage(context: Context, lang: String){
    Lingver.getInstance().setLocale(context, lang)
    recreate(context as Activity)
}

fun weatherIconMapping(icon: String):Int {
    return when{
        icon == "01d" -> {R.drawable.clear_sky_day}
        icon =="01n" -> {R.drawable.clear_sky_night}
        icon =="02d" -> {R.drawable.few_clouds_day}
        icon =="02n" -> {R.drawable.few_clouds_night}
        icon =="10d" -> {R.drawable.rain_day}
        icon =="10n" -> {R.drawable.rain_night}
        icon == "03d" || icon == "03n" -> {R.drawable.scattered_clouds}
        icon == "04d" || icon == "04n" -> {R.drawable.broken_clouds}
        icon == "09d" || icon == "09n" -> {R.drawable.shower_rain}
        icon == "11d" || icon == "11n" -> {R.drawable.thunderstorm}
        icon == "13d" || icon == "13n" -> {R.drawable.snow}
        icon == "50d" || icon == "50n" -> {R.drawable.mist}
        else -> {R.drawable.mist}
    }
}
@RequiresApi(Build.VERSION_CODES.O)
fun getFormattedTime(timestamp: Long, pattern: String = "h a"): String {
    val instant = Instant.ofEpochSecond(timestamp)
    val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    return dateTime.format(DateTimeFormatter.ofPattern(pattern))
}

fun weatherDescIdsMapping(id: Int, context: Context): String = when(id) {
    200 -> context.getString(R.string.thunderstorm_light_rain)
    201 -> context.getString(R.string.thunderstorm_rain)
    202 -> context.getString(R.string.thunderstorm_heavy_rain)
    210 -> context.getString(R.string.thunderstorm_light)
    211 -> context.getString(R.string.thunderstorm)
    212 -> context.getString(R.string.thunderstorm_heavy)
    221 -> context.getString(R.string.thunderstorm_ragged)
    230 -> context.getString(R.string.thunderstorm_light_drizzle)
    231 -> context.getString(R.string.thunderstorm_drizzle)
    232 -> context.getString(R.string.thunderstorm_heavy_drizzle)

    300 -> context.getString(R.string.drizzle_light)
    301 -> context.getString(R.string.drizzle)
    302 -> context.getString(R.string.drizzle_heavy)
    310 -> context.getString(R.string.drizzle_light_rain)
    311 -> context.getString(R.string.drizzle_rain)
    312 -> context.getString(R.string.drizzle_heavy_rain)
    313 -> context.getString(R.string.drizzle_shower_rain)
    314 -> context.getString(R.string.drizzle_heavy_shower_rain)
    321 -> context.getString(R.string.drizzle_shower)

    500 -> context.getString(R.string.rain_light)
    501 -> context.getString(R.string.rain_moderate)
    502 -> context.getString(R.string.rain_heavy)
    503 -> context.getString(R.string.rain_very_heavy)
    504 -> context.getString(R.string.rain_extreme)
    511 -> context.getString(R.string.rain_freezing)
    520 -> context.getString(R.string.rain_shower_light)
    521 -> context.getString(R.string.rain_shower)
    522 -> context.getString(R.string.rain_shower_heavy)
    531 -> context.getString(R.string.rain_shower_ragged)

    600 -> context.getString(R.string.snow_light)
    601 -> context.getString(R.string.snow)
    602 -> context.getString(R.string.snow_heavy)
    611 -> context.getString(R.string.sleet)
    612 -> context.getString(R.string.sleet_shower_light)
    613 -> context.getString(R.string.sleet_shower)
    615 -> context.getString(R.string.rain_snow_light)
    616 -> context.getString(R.string.rain_snow)
    620 -> context.getString(R.string.snow_shower_light)
    621 -> context.getString(R.string.snow_shower)
    622 -> context.getString(R.string.snow_shower_heavy)

    701 -> context.getString(R.string.mist)
    711 -> context.getString(R.string.smoke)
    721 -> context.getString(R.string.haze)
    731 -> context.getString(R.string.dust_whirls)
    741 -> context.getString(R.string.fog)
    751 -> context.getString(R.string.sand)
    761 -> context.getString(R.string.dust)
    762 -> context.getString(R.string.volcanic_ash)
    771 -> context.getString(R.string.squalls)
    781 -> context.getString(R.string.tornado)

    800 -> context.getString(R.string.clear_sky)

    801 -> context.getString(R.string.clouds_few)
    802 -> context.getString(R.string.clouds_scattered)
    803 -> context.getString(R.string.clouds_broken)
    804 -> context.getString(R.string.clouds_overcast)

    else -> context.getString(R.string.weather_unknown)
}

fun LatLng.toLocation(): Location {
    return Location("").apply {
        latitude = this@toLocation.latitude
        longitude = this@toLocation.longitude
    }
}

fun getLocationName(latitude: Double, longitude: Double, context: Context): String? {
    return try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        addresses?.firstOrNull()?.let { address ->
            val locality = address.locality
            val adminArea = address.adminArea
            val country = address.countryName
            listOfNotNull(locality, country, address.subAdminArea).joinToString(", ")
        }
    } catch (e: Exception) {
        Log.d("TAG", "getLocationName: fail in get location name")
        null
    }
}

