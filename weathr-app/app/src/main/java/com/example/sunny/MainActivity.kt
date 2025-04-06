package com.example.sunny

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sunny.data.local.PreferenceManager
import com.example.sunny.settings.SettingsViewModel
import com.example.sunny.settings.SettingsViewModelFactory
import com.example.sunny.ui.theme.SunnyTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModelFactory(PreferenceManager(this))
            )
            val mode by settingsViewModel.themeMode.collectAsState()
            SunnyTheme (darkTheme = if(mode == 2)true else false ) {
                LocationPermission()
            }
        }
    }
}
