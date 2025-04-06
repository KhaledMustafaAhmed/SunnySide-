package com.example.sunny

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.cloudy.data.remote.RetrofitHelper
import com.example.sunny.alarm.AlarmViewModel
import com.example.sunny.alarm.AlarmViewModelFactory
import com.example.sunny.data.local.PreferenceManager
import com.example.sunny.data.local.room.AppDatabase
import com.example.sunny.data.local.room.LocalDataSourceImpl
import com.example.sunny.data.remote.RemoteDataSourceImpl
import com.example.sunny.data.repo.WeatherRepositoryImpl
import com.example.sunny.fav.FavViewModel
import com.example.sunny.fav.FavViewModelFactory
import com.example.sunny.fav.FavouriteDetailsViewModel
import com.example.sunny.fav.FavouriteDetailsViewModelFactory
import com.example.sunny.home.HomeViewModel
import com.example.sunny.home.HomeViewModelFactory
import com.example.sunny.map.MapViewModel
import com.example.sunny.map.MapViewModelFactory
import com.example.sunny.navigation.SetupNavGraph
import com.example.sunny.settings.SettingsViewModel
import com.example.sunny.settings.SettingsViewModelFactory
import com.example.sunny.utility.LocationService
import com.google.android.libraries.places.api.Places


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LocationPermission() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            WeatherRepositoryImpl(
                RemoteDataSourceImpl(RetrofitHelper.retrofitService),
                LocalDataSourceImpl(
                    AppDatabase.getDatabase(context.applicationContext).weatherAlertDao(),
                    AppDatabase.getDatabase(context.applicationContext).FavouritesDao()
                )
            )
        )
    )
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(PreferenceManager(context))
    )
    val favViewModel: FavViewModel = viewModel(
        factory = FavViewModelFactory(
            WeatherRepositoryImpl(
                RemoteDataSourceImpl(RetrofitHelper.retrofitService),
                LocalDataSourceImpl(
                    AppDatabase.getDatabase(context.applicationContext).weatherAlertDao(),
                    AppDatabase.getDatabase(context.applicationContext).FavouritesDao()
                )
            )
        )
    )

    val favouriteDetailsViewModel: FavouriteDetailsViewModel = viewModel(
        factory = FavouriteDetailsViewModelFactory(
            WeatherRepositoryImpl(
                RemoteDataSourceImpl(RetrofitHelper.retrofitService),
                LocalDataSourceImpl(
                    AppDatabase.getDatabase(context.applicationContext).weatherAlertDao(),
                    AppDatabase.getDatabase(context.applicationContext).FavouritesDao()
                )
            )
        )
    )

    val mapViewModel: MapViewModel = viewModel(
        factory = MapViewModelFactory(
            WeatherRepositoryImpl(
                RemoteDataSourceImpl(RetrofitHelper.retrofitService),
                LocalDataSourceImpl(
                    AppDatabase.getDatabase(context.applicationContext).weatherAlertDao(),
                    AppDatabase.getDatabase(context.applicationContext).FavouritesDao()),
                ),
            Places.createClient(context)
            )
    )

    val alarmViewModel: AlarmViewModel = viewModel(
        factory = AlarmViewModelFactory(WeatherRepositoryImpl(
            RemoteDataSourceImpl(RetrofitHelper.retrofitService),
            LocalDataSourceImpl(
                AppDatabase.getDatabase(context.applicationContext).weatherAlertDao(),
                AppDatabase.getDatabase(context.applicationContext).FavouritesDao()
            )
        ))
    )


    val locationService = remember { LocationService(context) }

    var permissionState by remember { mutableStateOf<PermissionState>(PermissionState.IDLE) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionState = when {
            isGranted -> {
                locationService.getLastLocation { location ->
                    location?.let { homeViewModel.updateLocation(it) }
                }
                PermissionState.GRANTED
            }
            (context as ComponentActivity).shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) ->
                PermissionState.SHOW_RATIONALE
            else -> PermissionState.PERMANENTLY_DENIED
        }
    }


    LaunchedEffect(Unit) {
        permissionState = if (hasLocationPermission(context)) {
            locationService.getLastLocation { location ->
                location?.let { homeViewModel.updateLocation(it) }
            }
            PermissionState.GRANTED
        } else {
            PermissionState.REQUEST_PERMISSION
        }
    }

    when (permissionState) {
        PermissionState.IDLE -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        PermissionState.REQUEST_PERMISSION -> {
            LaunchedEffect(Unit) {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        PermissionState.GRANTED -> {
            SetupNavGraph(navController, homeViewModel, settingsViewModel, favViewModel, mapViewModel, alarmViewModel, favouriteDetailsViewModel)
        }
        PermissionState.SHOW_RATIONALE -> {
            RationaleDialog(
                onConfirm = {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    permissionState = PermissionState.IDLE
                },
                onDismiss = { permissionState = PermissionState.IDLE }
            )
        }
        PermissionState.PERMANENTLY_DENIED -> {
            SettingsDialog(
                onConfirm = {
                    openAppSettings(context)
                    permissionState = PermissionState.REQUEST_PERMISSION
                },
                onDismiss = { permissionState = PermissionState.IDLE }
            )
        }
    }
}

@Composable
private fun RationaleDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Location Permission Required",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(
                text = "This app needs location access to provide accurate weather information for your area. Please grant location permission to continue.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Grant Permission")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


@Composable
private fun SettingsDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Permission Required",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column {
                Text(
                    text = "You have permanently denied location permission.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "To use this app, please enable location permission in app settings:",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Start
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Open Settings")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun hasLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

private fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
}

sealed class PermissionState {
    object IDLE : PermissionState()
    object REQUEST_PERMISSION : PermissionState()
    object GRANTED : PermissionState()
    object SHOW_RATIONALE : PermissionState()
    object PERMANENTLY_DENIED : PermissionState()
}
