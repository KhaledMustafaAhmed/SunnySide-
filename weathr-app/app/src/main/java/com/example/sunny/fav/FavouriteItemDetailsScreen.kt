package com.example.sunny.fav

import android.os.Build
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cloudy.utility.Constants
import com.example.cloudy.utility.celsiusToFahrenheit
import com.example.cloudy.utility.celsiusToKelvin
import com.example.cloudy.utility.roundToTwoDecimal
import com.example.cloudy.utility.timestampToDate
import com.example.cloudy.utility.weatherDescIdsMapping
import com.example.cloudy.utility.weatherIconMapping
import com.example.sunny.R
import com.example.sunny.home.ForecastHeader
import com.example.sunny.home.HourlyForecastRow
import com.example.sunny.home.WeatherMetricsCard
import com.example.sunny.settings.SettingsViewModel
import com.example.sunny.utility.ResultResponse

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteDetailsScreen(
    viewModel: FavouriteDetailsViewModel,
    settingsViewModel: SettingsViewModel,
    placeId: Long,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val tempUnit by settingsViewModel.temperatureUnit.collectAsStateWithLifecycle()

    LaunchedEffect(placeId) {
        viewModel.getFavByPlaceId(placeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.favourites)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        when (val result = uiState) {
            ResultResponse.Loading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ResultResponse.Failure -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = result.message, color = MaterialTheme.colorScheme.error)
                }
            }

            is ResultResponse.Success -> {
                val favoritePlace = result.value
                FavoriteDetailsContent(
                    modifier = Modifier.padding(innerPadding),
                    weatherResponse = favoritePlace.weatherCashed,
                    settingsViewModel = settingsViewModel,
                    tempUnit = tempUnit,
                    placeName = favoritePlace.cityName
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FavoriteDetailsContent(
    modifier: Modifier = Modifier,
    weatherResponse: com.example.sunny.data.model.pojos.WeatherResponse,
    settingsViewModel: SettingsViewModel,
    tempUnit: String,
    placeName: String
) {
    val context = LocalContext.current
    val gradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surface
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = modifier
                .background(gradient)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            item {
                CurrentWeatherSection(
                    weatherResponse = weatherResponse,
                    settingsViewModel = settingsViewModel,
                    tempUnit = tempUnit,
                    placeName = placeName
                )
            }

            item {
                WeatherMetricsCard(
                    weatherResponse.current.humidity,
                    weatherResponse.current.pressure,
                    weatherResponse.current.wind_speed,
                    weatherResponse.current.clouds
                )
            }

            item {
                HourlyForecastRow(
                    weatherResponse.hourly,
                    tempUnit
                )
            }

            item { ForecastHeader() }

            items(weatherResponse.daily) { daily ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(size = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    val (displayTempMin, displayTempMax, unitSymbol) = when (tempUnit) {
                        Constants.Units.METRIC.toString() -> Triple(daily.temp.min, daily.temp.max, stringResource(R.string.c))
                        Constants.Units.IMPERIAL.toString() -> Triple(daily.temp.min.celsiusToFahrenheit(), daily.temp.max.celsiusToFahrenheit(), stringResource(R.string.f))
                        Constants.Units.KELVIN.toString() -> Triple(daily.temp.min.celsiusToKelvin(), daily.temp.max.celsiusToKelvin(), stringResource(R.string.k))
                        else -> Triple(daily.temp.min, daily.temp.max, stringResource(R.string.c))
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
                                painterResource(id = weatherIconMapping(daily.weather[0].icon)),
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
                                        daily.dt.toLong(),
                                        pattern = Constants.PATTERNS_FULL_DATE_FOR_CURRENT
                                    ).split(",")[0],
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = weatherDescIdsMapping(daily.weather[0].id, context),
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
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CurrentWeatherSection(
    weatherResponse: com.example.sunny.data.model.pojos.WeatherResponse,
    settingsViewModel: SettingsViewModel,
    tempUnit: String,
    placeName: String
) {
    val context = LocalContext.current
    val weatherIcon = if (weatherResponse.current.weather.isNotEmpty()) {
        weatherIconMapping(weatherResponse.current.weather[0].icon)
    } else {
        R.drawable.mist
    }

    val (displayTemp, unitSymbol) = when (tempUnit) {
        Constants.Units.METRIC.toString() -> Pair(weatherResponse.current.temp, stringResource(R.string.c))
        Constants.Units.IMPERIAL.toString() -> Pair(weatherResponse.current.temp.celsiusToFahrenheit(), stringResource(R.string.f))
        Constants.Units.KELVIN.toString() -> Pair(weatherResponse.current.temp.celsiusToKelvin(), stringResource(R.string.k))
        else -> Pair(weatherResponse.current.temp, stringResource(R.string.c))
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
                        weatherResponse.current.dt.toLong(),
                        pattern = Constants.PATTERNS_FULL_DATE_FOR_CURRENT
                    ),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = placeName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${displayTemp.roundToTwoDecimal()} ",
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
                    text = weatherDescIdsMapping(weatherResponse.current.weather[0].id, context),
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Image(
                    painterResource(id = weatherIcon),
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