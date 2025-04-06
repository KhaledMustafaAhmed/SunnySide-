package com.example.sunny.data.local.room

import com.example.sunny.data.model.entities.FavoritePlaceEntity
import com.example.sunny.data.model.pojos.WeatherResponse
import kotlinx.coroutines.flow.Flow

class DummyFavouriteDao: FavouritesDao {
    override suspend fun insertPlace(favPlace: FavoritePlaceEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun deletePlace(favPlace: FavoritePlaceEntity) {
        TODO("Not yet implemented")
    }

    override fun getAllFavourites(): Flow<List<FavoritePlaceEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun updateCashedWeather(id: Long, weatherCashed: WeatherResponse) {
        TODO("Not yet implemented")
    }

    override suspend fun getPlaceById(placeId: Long): Flow<FavoritePlaceEntity> {
        TODO("Not yet implemented")
    }
}