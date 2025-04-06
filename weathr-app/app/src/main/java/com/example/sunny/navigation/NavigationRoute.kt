package com.example.sunny.navigation

import com.example.sunny.data.model.entities.FavoritePlaceEntity
import kotlinx.serialization.Serializable


sealed class NavigationRoute {
    @Serializable
    object Settings : NavigationRoute()

    @Serializable
    object Home : NavigationRoute()

    @Serializable
    object Fav: NavigationRoute()

    @Serializable
    object Map: NavigationRoute()

    @Serializable
    object Alarm: NavigationRoute()

    @Serializable
    data class FavDetails(val id: Long): NavigationRoute()
}