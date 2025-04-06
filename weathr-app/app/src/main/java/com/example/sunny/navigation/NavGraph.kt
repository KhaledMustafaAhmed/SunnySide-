package com.example.sunny.navigation

import AlertsScreen
import MapScreen
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.sunny.alarm.AlarmViewModel
import com.example.sunny.fav.FavViewModel
import com.example.sunny.fav.FavoriteDetailsScreen
import com.example.sunny.fav.FavouriteDetailsViewModel
import com.example.sunny.fav.FavouritesScreen
import com.example.sunny.home.HomeScreen
import com.example.sunny.home.HomeViewModel
import com.example.sunny.map.MapViewModel
import com.example.sunny.settings.SettingsScreen
import com.example.sunny.settings.SettingsViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetupNavGraph(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    settingsViewModel: SettingsViewModel,
    favViewModel: FavViewModel,
    mapViewModel: MapViewModel,
    alarmViewModel: AlarmViewModel,
    favouriteDetailsViewModel: FavouriteDetailsViewModel
    ) {
    NavHost(navController = navController , startDestination = NavigationRoute.Home){
        composable<NavigationRoute.Home>(){
            HomeScreen(
                homeViewModel,
                settingsViewModel,
                {navController.navigate(NavigationRoute.Fav)},
                {navController.navigate(NavigationRoute.Settings)},
                {navController.navigate(NavigationRoute.Alarm)}
            )
        }

        composable<NavigationRoute.Fav> {
            FavouritesScreen(
                favViewModel,
                onAddPlaceClick = {
                    navController.navigate(NavigationRoute.Map)
                },
                onBackClick = { navController.popBackStack()},
                onPlaceClick = {navController.navigate(NavigationRoute.FavDetails(it))}
            )
        }

        composable<NavigationRoute.Map> {
            MapScreen(mapViewModel)
        }

        composable<NavigationRoute.Settings> {
            SettingsScreen(settingsViewModel){
                navController.popBackStack()
            }
        }

        composable<NavigationRoute.Alarm> {
            AlertsScreen(alarmViewModel = alarmViewModel)
        }

        composable<NavigationRoute.FavDetails> {navBackStackEntry ->
            val data = navBackStackEntry.toRoute<NavigationRoute.FavDetails>()
            val placeId = data.id
            FavoriteDetailsScreen(
                favouriteDetailsViewModel,
                settingsViewModel,
                placeId,
                {navController.popBackStack()}
            )
        }
    }
}