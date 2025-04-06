package com.example.sunny.data.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.sunny.data.model.entities.FavoritePlaceEntity
import com.example.sunny.data.model.pojos.WeatherResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouritesDao {
    @Insert
    suspend fun insertPlace(favPlace: FavoritePlaceEntity)

    @Delete
    suspend fun deletePlace(favPlace: FavoritePlaceEntity)

    @Query("SELECT * FROM FAVORITE_PLACES")
    fun getAllFavourites(): Flow<List<FavoritePlaceEntity>>

    @Query("UPDATE FAVORITE_PLACES SET weatherCashed = :weatherCashed WHERE id= :id")
    suspend fun updateCashedWeather(id: Long, weatherCashed: WeatherResponse)

    @Query("SELECT * FROM FAVORITE_PLACES WHERE ID = :placeId  Limit 1")
    fun getPlaceById(placeId: Long): FavoritePlaceEntity
}