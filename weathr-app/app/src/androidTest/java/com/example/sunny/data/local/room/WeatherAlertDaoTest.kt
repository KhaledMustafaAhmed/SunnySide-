package com.example.sunny.data.local.room

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.sunny.data.model.entities.WeatherAlertEntity
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class WeatherAlertDaoTest {
    private lateinit var weatherAlertDao: WeatherAlertDao
    private lateinit var appDatabase: AppDatabase

    @Before
    fun setup(){
        appDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()

        weatherAlertDao = appDatabase.weatherAlertDao()
    }

    @After
    fun closeDB() = appDatabase.close()

    @Test
    fun testAddAlert() = runBlocking {

        //given
        val alert = WeatherAlertEntity("1", 1.1,2.2,"helwan")
        weatherAlertDao.addAlert(alert)


        //when
        val alerts = weatherAlertDao.getAllAlerts().first()

        //then
        assertEquals(1, alerts.size)
        assertEquals(alert, alerts[0])
    }


    @Test
    fun testGetAllAlerts() = runTest {
        //given
        val alert1 = WeatherAlertEntity("1", 1.1,2.2,"helwan")
        val alert2= WeatherAlertEntity("2", 1.1,2.2,"helwan")
        weatherAlertDao.addAlert(alert1)
        weatherAlertDao.addAlert(alert2)

        //when
        val alers = weatherAlertDao.getAllAlerts().first()

        //then
        assertEquals(2, alers.size)
        assertEquals(alert1, alers[0])
        assertEquals(alert2, alers[1])
    }

    @Test
    fun testDeleteAlert() = runBlocking {
        //given
        val alert = WeatherAlertEntity("1", 1.1,2.2,"helwan")
        weatherAlertDao.addAlert(alert)
        weatherAlertDao.deleteAlert(alert)

        //when
        val alerts = weatherAlertDao.getAllAlerts().first()

        //then
        assertEquals(0, alerts.size)
    }


}

/*  *

@Dao
interface WeatherAlertDao {
    @Query("SELECT * FROM weather_alerts")
    fun getAllAlerts(): Flow<List<WeatherAlertEntity>>

    @Insert
    suspend fun addAlert(alert: WeatherAlertEntity)

    @Delete
    suspend fun deleteAlert(alert: WeatherAlertEntity)
}



/
 */