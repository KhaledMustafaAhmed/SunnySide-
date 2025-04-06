package com.example.sunny.alarm

import com.example.sunny.data.model.entities.WeatherAlertEntity
import com.example.sunny.data.repo.WeatherRepository
import com.example.sunny.utility.ResultResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AlarmViewModelTest {
    private lateinit var viewModel: AlarmViewModel
    private lateinit var weatherRepository: WeatherRepository

    @Before
    fun setup() {
        weatherRepository = mockk(relaxed = true)
        viewModel = AlarmViewModel(weatherRepository)
    }


    @Test
    fun getAllAlertsTest() = runTest {
        val alert1 = WeatherAlertEntity(
            workManagerRequestId = "wm_12345_abc",
            latitude = 40.7128,
            longitude = -74.0060,
            cityName = "New York"
        )

        val alert2 = WeatherAlertEntity(
            workManagerRequestId = "wm_67890_xyz",
            latitude = 34.0522,
            longitude = -118.2437,
            cityName = "Los Angeles"
        )
        val list = listOf(
            alert1,
            alert2
        )
        /* كل ما انده لل فانكشن هترجع ليا flow من ال list بتاعتي  */
        coEvery { weatherRepository.getAllAlerts() }returns flowOf(list)

        // when
        viewModel.getAllAlerts()

        val x = viewModel.alertList.first()
      //  val y = x.value

        val result = viewModel.alertList.value
        assertTrue(x is ResultResponse.Success)
        //then
       assertTrue( (x as ResultResponse.Success).value == list)

    }

    @Test
    fun showAddAlertDialog_setsShowDialogTrue() {
        // to make sure that is false
        assertFalse(viewModel.showDialog)

        // when
        viewModel.showAddAlertDialog()

        //then
        assertTrue(viewModel.showDialog)
    }

    @Test
    fun dismissDialog_setsShowDialogFalse() {
        // given
        viewModel.showDialog = true

        // when
        viewModel.dismissDialog()

        // then
        assertFalse(viewModel.showDialog)
    }
}