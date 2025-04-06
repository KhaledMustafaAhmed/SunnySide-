package com.example.sunny.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.sunny.data.model.entities.FavoritePlaceEntity
import com.example.sunny.data.model.entities.WeatherAlertEntity

@Database(entities = [WeatherAlertEntity::class, FavoritePlaceEntity::class], version = 3)
@TypeConverters(TypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherAlertDao(): WeatherAlertDao
    abstract fun FavouritesDao(): FavouritesDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "weatherdb"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}