package com.example.sunny.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cloudy.utility.Constants
import com.example.cloudy.utility.celsiusToFahrenheit
import com.example.cloudy.utility.celsiusToKelvin
import com.example.cloudy.utility.getFormattedTime
import com.example.cloudy.utility.getLocationName
import com.example.cloudy.utility.metersPerSecondToMilesPerHour
import com.example.cloudy.utility.milesPerHourToMetersPerSecond
import com.example.cloudy.utility.roundToTwoDecimal
import com.example.cloudy.utility.timestampToDate
import com.example.cloudy.utility.unitFromString
import com.example.cloudy.utility.weatherDescIdsMapping
import com.example.cloudy.utility.weatherIconMapping
import com.example.sunny.R
import com.example.sunny.data.model.pojos.Daily
import com.example.sunny.data.model.pojos.Hourly
import com.example.sunny.data.model.pojos.WeatherResponse
import com.example.sunny.settings.SettingsViewModel
import com.example.sunny.utility.ResultResponse

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    settingsViewModel: SettingsViewModel, /// 1
    onFavAction: ()-> Unit,
    onSettingsAction: () -> Unit,
    onAlarmAction: () -> Unit
    ) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        HomeScreenContent(modifier = Modifier.padding(innerPadding), homeViewModel,settingsViewModel, onFavAction,onSettingsAction,onAlarmAction )
        // 2
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    homeViewModel: HomeViewModel,
    settingsViewModel: SettingsViewModel, // 3
    onFavAction: () -> Unit,
    onSettingsAction: () -> Unit,
    onAlarmAction: () -> Unit
) {
    val context = LocalContext.current
    val tempUnit by settingsViewModel.temperatureUnit.collectAsStateWithLifecycle()

    val speed by settingsViewModel.windSpeed.collectAsStateWithLifecycle()
    val location by homeViewModel.currentLocation.collectAsStateWithLifecycle()
    val currentWeatherDate by homeViewModel.currentWeather.collectAsStateWithLifecycle()
//    Log.d("TAG", "HomeScreenContent: location ${location}")

    LaunchedEffect(Unit) {
        homeViewModel.getCurrentWeather( "29.8499966", "31.333332")
        Log.d("TAG", "HomeScreenContent: ${location?.latitude}")
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surface
        )
    )

    when (val result = currentWeatherDate) {
        ResultResponse.Loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is ResultResponse.Failure -> {
            Log.d("TAG", "HomeScreenContent: fail")
        }

        is ResultResponse.Success -> {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = modifier
                        .background(gradient)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item { Spacer(modifier = Modifier.height(32.dp)) }
                    item { CurrentWeatherSection(result.value, settingsViewModel, tempUnit) }
                    /* humidity card */
                  item {
                      WeatherMetricsCard(
                          result.value.current.humidity,
                          result.value.current.pressure,
                          if(speed)result.value.current.wind_speed.metersPerSecondToMilesPerHour().roundToTwoDecimal()
                          else
                              result.value.current.wind_speed.milesPerHourToMetersPerSecond().roundToTwoDecimal(),
                          result.value.current.clouds
                      )
                  }
                    /* lazy Row for hourlyWeather */
                    item { HourlyForecastRow(result.value.hourly, tempUnit) }

                    item { ForecastHeader() }

                    itemsIndexed(result.value.daily) { _, weather ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(size = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            val (displayTempMin,displayTempMax,unitSymbol) = when (tempUnit) {
                                Constants.Units.METRIC.toString() -> Triple(weather.temp.min, weather.temp.max,stringResource(R.string.c))
                                Constants.Units.IMPERIAL.toString() -> Triple(weather.temp.min.celsiusToFahrenheit(), weather.temp.max.celsiusToFahrenheit(), stringResource(R.string.f))
                                Constants.Units.KELVIN.toString() -> Triple(weather.temp.min.celsiusToKelvin(), weather.temp.max.celsiusToKelvin(), stringResource(R.string.k))
                                else -> Triple(weather.temp.min, weather.temp.max, stringResource(R.string.c))
                            }

                            Row(
                                modifier = Modifier
                                    .padding(all = 16.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(space = 16.dp)
                                ) {
                                    Image(
                                        painterResource(id = weatherIconMapping(weather.weather[0].icon)),
                                        contentDescription = "Weather Icon",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .size(50.dp)
                                            .background(MaterialTheme.colorScheme.secondary)
                                    )

                                    Column {
                                        Text(
                                            text = timestampToDate(
                                                weather.dt.toLong(),
                                                pattern = Constants.PATTERNS_FULL_DATE_FOR_CURRENT
                                            ).split(",")[0],
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = weatherDescIdsMapping(weather.weather[0].id, context),
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${displayTempMin.roundToTwoDecimal()}°",
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = " / ",
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                    Text(
                                        text = "${displayTempMax.roundToTwoDecimal()}°",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }


                    }
                    item { Spacer(modifier = Modifier.height(32.dp)) }
                }
            }
            FloatingActionButtons(onFavAction, onSettingsAction, onAlarmAction)
        }
    }
}

@Composable
fun FloatingActionButtons(
    onFavAction: () -> Unit,
    onSettingsAction: () -> Unit,
    onAlarmAction: () -> Unit
    ) {
    Box(modifier = Modifier.fillMaxSize()) {
        FloatingActionButton(
            onClick = {
                onSettingsAction.invoke()
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(20.dp)
                .padding(vertical = 24.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Filled.Settings, contentDescription = "Settings")
        }

        FloatingActionButton(
            onClick = {
                onAlarmAction.invoke()
            },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(20.dp)
                .padding(vertical = 24.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Filled.DateRange, contentDescription = "Calendar")
        }

        FloatingActionButton(
            onClick = {
                onFavAction.invoke()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .padding(vertical = 30.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Filled.Favorite, contentDescription = "Favorites")
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CurrentWeatherSection(data: WeatherResponse, settingsViewModel: SettingsViewModel, tempUnit: String) {
    val context = LocalContext.current
    val weatherIcon = if (data.current.weather.isNotEmpty()) {
        weatherIconMapping(data.current.weather[0].icon)
    } else {
        R.drawable.mist
    }

    val (displayTemp, unitSymbol) = when (tempUnit) {
        Constants.Units.METRIC.toString() -> Pair(data.current.temp, stringResource(R.string.c))
        Constants.Units.IMPERIAL.toString() -> Pair(data.current.temp.celsiusToFahrenheit(), stringResource(R.string.f))
        Constants.Units.KELVIN.toString() -> Pair(data.current.temp.celsiusToKelvin(), stringResource(R.string.k))
        else -> Pair(data.current.temp, stringResource(R.string.c))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 20.dp)
            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(fraction = 0.9f)
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(size = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = timestampToDate(
                        data.current.dt.toLong(),
                        pattern = Constants.PATTERNS_FULL_DATE_FOR_CURRENT
                    ),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                getLocationName(data.lat, data.lon, context)?.let {
                    Text(
                        text = it,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text ="${displayTemp.roundToTwoDecimal()} ", // 5
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = unitSymbol,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = weatherDescIdsMapping(data.current.weather[0].id, context),
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Image(
                    painterResource(id =weatherIcon),
                    contentDescription = "Weather Icon",
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(120.dp)
                        .background(MaterialTheme.colorScheme.secondary)
                )
            }
        }
    }
}

@Composable
fun ForecastHeader() {
    Text(
        text = stringResource(R.string._7_day_forecast),
        modifier = Modifier.fillMaxWidth(),
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center
    )
}

@Composable
fun WeatherMetricsCard(
    humidity: Int,
    pressure: Int,
    windSpeed: Double,
    clouds: Int,
    modifier: Modifier = Modifier
) {
    val metrics = listOf(
        WeatherMetric(stringResource(R.string.humidity), "$humidity%", R.drawable.humidity),
        WeatherMetric(stringResource(R.string.pressure),
            stringResource(R.string.hpd, pressure), R.drawable.weather_pressure),
        WeatherMetric(stringResource(R.string.wind_speed),
            stringResource(R.string.m_s, windSpeed), R.drawable.wind_speed),
        WeatherMetric(stringResource(R.string.clouds), "$clouds%", R.drawable.cloud)
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        items(metrics) { metric ->
            MetricCard(metric)
        }
    }
}
@Composable
private fun MetricCard(metric: WeatherMetric) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .width(100.dp)
            .height(120.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = metric.iconRes),
                contentDescription = metric.name,
                modifier = Modifier.size(32.dp),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                    MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = metric.value,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = metric.name,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

private data class WeatherMetric(
    val name: String,
    val value: String,
    val iconRes: Int
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HourlyForecastRow(
    hourlyData: List<Hourly>,
    unit: String
) {

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        items(hourlyData) { hourly ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier
                    .width(80.dp)
                    .height(120.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = getFormattedTime(hourly.dt.toLong()),
                        style = MaterialTheme.typography.labelMedium,
                        textAlign = TextAlign.Center
                    )

                    Image(
                        painter = painterResource(id = weatherIconMapping(hourly.weather[0].icon)),
                        contentDescription = "Weather Icon",
                        modifier = Modifier.size(32.dp),
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                            MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    Text(
                        text =" ${unitFromString(unit, hourly.temp)}" /*hourly.temp.toString()*/,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HourlyForecastCard(hourly: Hourly, unit: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .width(80.dp)
            .height(120.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = getFormattedTime(hourly.dt.toLong()),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )

            Image(
                painter = painterResource(id = weatherIconMapping(hourly.weather[0].icon)),
                contentDescription = "Weather Icon",
                modifier = Modifier.size(32.dp),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                    MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Text(
                text = hourly.temp.toString(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}