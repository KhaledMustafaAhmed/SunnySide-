package com.example.sunny.settings

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.cloudy.utility.Constants
import com.example.cloudy.utility.changeLanguage
import com.example.sunny.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val language by settingsViewModel.language.collectAsState()
    val temperatureUnit by settingsViewModel.temperatureUnit.collectAsState()
    val windSpeed by settingsViewModel.windSpeed.collectAsState()
    val mode by settingsViewModel.themeMode.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SettingsCard(title = stringResource(R.string.language)) {
                        Column {
                            RadioGroup(
                                options = listOf(
                                    Constants.Lang.EN.toString() to stringResource(R.string.english),
                                    Constants.Lang.AR.toString() to stringResource(R.string.arabic)
                                ),
                                currentValue = language,
                                onSelection = {
                                    settingsViewModel.setLanguage(it)
                                    changeLanguage(context, it)
                                }
                            )
                        }
                    }

                    SettingsCard(title = stringResource(R.string.temperature_unit)) {
                        Column {
                            Log.d("TAG", "SettingsScreen: ")
                            RadioGroup(
                                options = listOf(
                                    Constants.Units.KELVIN.toString() to stringResource(R.string.kelvin_k),
                                    Constants.Units.METRIC.toString() to stringResource(R.string.celsius_c),
                                    Constants.Units.IMPERIAL.toString() to stringResource(R.string.fahrenheit_f)
                                ),
                                currentValue = temperatureUnit,
                                onSelection = {
                                    settingsViewModel.setTemperatureUnit(it)
                                }
                            )
                        }
                    }

                    SettingsCard(title = stringResource(R.string.wind_speed_unit)) {
                        Column {
                            RadioGroup(
                                options = listOf(
                                    true to stringResource(R.string.meters_second_m_s),
                                    false to stringResource(R.string.miles_hour_mph)
                                ),
                                currentValue = windSpeed,
                                onSelection = { settingsViewModel.setWindSpeed(it) }
                            )
                        }
                    }

                    SettingsCard(title = stringResource(R.string.dark_mode)) {
                        Column {
                            RadioGroup(
                                options = listOf(
                                    1 to stringResource(R.string.off),
                                    2 to stringResource(R.string.on)
                                ),
                                currentValue = mode,
                                onSelection = {
                                    settingsViewModel.setThemeMode(it)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            content()
        }
    }
}

@Composable
fun <T> RadioGroup(
    options: List<Pair<T, String>>,
    currentValue: T,
    onSelection: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        options.forEach { (value, label) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (value == currentValue),
                        onClick = { onSelection(value) }
                    )
                    .padding(vertical = 8.dp)
            ) {
                RadioButton(
                    selected = (value == currentValue),
                    onClick = null
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}