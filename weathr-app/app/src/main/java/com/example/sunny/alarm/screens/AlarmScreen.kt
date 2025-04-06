import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.sunny.alarm.AlarmViewModel
import com.example.sunny.alarm.screens.WeatherAlert
import com.example.sunny.alarm.worker.WorkerHelper
import com.example.sunny.data.model.entities.WeatherAlertEntity
import com.example.sunny.utility.ResultResponse
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    alarmViewModel: AlarmViewModel
) {
    val context = LocalContext.current
    val alertState by alarmViewModel.alertList.collectAsState()
    val workManager = remember { WorkManager.getInstance(context) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weather Alerts") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { alarmViewModel.showAddAlertDialog() }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Alert")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (val state = alertState) {
                is ResultResponse.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ResultResponse.Failure -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: ${state.message}")
                    }
                }
                is ResultResponse.Success -> {
                    if (state.value.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No alerts set\nTap + to add one",
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        AlertList(
                            alerts = state.value,
                            onCancelAlert = { alert ->
                                WorkerHelper.cancelAlert(
                                    alert = alert,
                                    alarmViewModel = alarmViewModel,
                                    workManager = workManager
                                )
                            }
                        )
                    }
                }
            }
        }

        if (alarmViewModel.showDialog) {
            AddAlertDialog(
                onDismiss = { alarmViewModel.dismissDialog() },
                onConfirm = { alert ->
                    WorkerHelper.scheduleAlert(
                        alert = alert,
                        alarmViewModel = alarmViewModel,
                        workManager = workManager,
                        context = context
                    )
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun AlertList(
    alerts: List<WeatherAlertEntity>,
    onCancelAlert: (WeatherAlertEntity) -> Unit
) {
    LazyColumn {
        items(alerts, key = { it.workManagerRequestId }) { alert ->
            AlertItem(
                alert = alert,
                onCancel = { onCancelAlert(alert) }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun AlertItem(
    alert: WeatherAlertEntity,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = alert.cityName,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onCancel,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Cancel Alert")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun AddAlertDialog(
    onDismiss: () -> Unit,
    onConfirm: (WeatherAlert) -> Unit
) {
    var hours by remember { mutableStateOf(0) }
    var minutes by remember { mutableStateOf(0) }
    var alertType by remember { mutableStateOf(WeatherAlert.AlertType.NOTIFICATION) }
    var showTimePicker by remember { mutableStateOf(false) }
    var cameraPositionState by remember {
        mutableStateOf(
            CameraPositionState(
                CameraPosition.fromLatLngZoom(LatLng(23.32, 32.546), 4f)
            )
        )
    }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Set Duration") },
            text = {
                Column {
                    Text("Hours: $hours")
                    Slider(
                        value = hours.toFloat(),
                        onValueChange = { hours = it.toInt() },
                        valueRange = 0f..24f,
                        steps = 23
                    )

                    Text("Minutes: $minutes")
                    Slider(
                        value = minutes.toFloat(),
                        onValueChange = { minutes = it.toInt() },
                        valueRange = 0f..59f,
                        steps = 58
                    )

                    Text("Selected: ${hours}h ${minutes}m")
                }
            },
            confirmButton = {
                Button(onClick = { showTimePicker = false }) {
                    Text("OK")
                }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Weather Alert") },
        text = {
            Column(
                modifier = Modifier.heightIn(min = 400.dp)
            ) {
                Text("Select Location:", modifier = Modifier.padding(bottom = 8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        onMapClick = { latLng ->
                            selectedLocation = latLng
                            cameraPositionState.move(CameraUpdateFactory.newLatLng(latLng))
                        }
                    ) {
                        selectedLocation?.let { location ->
                            Marker(
                                state = MarkerState(position = location),
                                title = "Alert Location"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (hours == 0 && minutes == 0) "Select Duration"
                        else "Duration: ${hours}h ${minutes}m"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Column {
                    Text("Alert Type:")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        FilterChip(
                            selected = alertType == WeatherAlert.AlertType.NOTIFICATION,
                            onClick = { alertType = WeatherAlert.AlertType.NOTIFICATION },
                            label = { Text("Notification") }
                        )
                        FilterChip(
                            selected = alertType == WeatherAlert.AlertType.ALARM_SOUND,
                            onClick = { alertType = WeatherAlert.AlertType.ALARM_SOUND },
                            label = { Text("Alarm Sound") }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (hours > 0 || minutes > 0) {
                        val duration = hours.hours + minutes.minutes
                        onConfirm(
                            WeatherAlert(
                                duration = duration,
                                alertType = alertType,
                                latitude = selectedLocation?.latitude ?: 0.0,
                                longitude = selectedLocation?.longitude ?: 0.0
                            )
                        )
                        onDismiss()
                    }
                },
                enabled = (hours > 0 || minutes > 0) && selectedLocation != null
            ) {
                Text("Set Alert")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}