package ua.skachkov.temperature.myapplication.service

import android.app.IntentService
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import ua.skachkov.temperature.myapplication.DateProvider
import ua.skachkov.temperature.myapplication.MeasurementsLoadService
import ua.skachkov.temperature.myapplication.TemperatureDataLoadException
import ua.skachkov.temperature.myapplication.app
import ua.skachkov.temperature.myapplication.constants.MEASUREMENTS_LOADED_BROADCAST
import ua.skachkov.temperature.myapplication.constants.MEASUREMENTS_STARTED_LOADING_BROADCAST
import ua.skachkov.temperature.myapplication.constants.TEMPERATURE_DATA
import ua.skachkov.temperature.myapplication.data.WeatherData
import javax.inject.Inject

const val weatherMeasurementsServiceLabel = "WeatherMeasurementsService"

class UpdateWeatherMeasurementsService : IntentService(weatherMeasurementsServiceLabel) {
    @Inject
    lateinit var dateProvider: DateProvider
    @Inject
    lateinit var measurementsLoadService: MeasurementsLoadService

    override fun onCreate() {
        super.onCreate()
        app.component.inject(this)
    }

    override fun onHandleIntent(intent: Intent?) {
        val localBroadcastManager = LocalBroadcastManager.getInstance(this)
        val measurementsStartedLoadingIntent = Intent(MEASUREMENTS_STARTED_LOADING_BROADCAST)
        localBroadcastManager.sendBroadcast(measurementsStartedLoadingIntent)

        val temperatureData = loadWeatherData(dateProvider, measurementsLoadService)
        val loadedMeasurementsIntent = Intent(MEASUREMENTS_LOADED_BROADCAST)
        loadedMeasurementsIntent.putExtra(TEMPERATURE_DATA, temperatureData)
        localBroadcastManager.sendBroadcast(loadedMeasurementsIntent)
    }

    companion object {
        fun loadWeatherData(dateProvider: DateProvider, measurementsLoadService: MeasurementsLoadService): WeatherData {
            val syncDate = dateProvider.currentDateFormatted()
            return try {
                val (temperature, humidity) = measurementsLoadService.getMeasurements()
                WeatherData(temperature, humidity, syncDate)
            } catch (e: TemperatureDataLoadException) {
                WeatherData(statusMessage = e.message ?: "", syncDate = syncDate)
            }
        }
    }
}