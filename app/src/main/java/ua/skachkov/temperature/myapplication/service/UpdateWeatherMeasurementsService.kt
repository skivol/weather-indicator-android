package ua.skachkov.temperature.myapplication.service

import android.app.IntentService
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import ua.skachkov.temperature.myapplication.DateProvider
import ua.skachkov.temperature.myapplication.network.NetworkMeasurementsLoader
import ua.skachkov.temperature.myapplication.network.MeasurementsDataLoadException
import ua.skachkov.temperature.myapplication.app
import ua.skachkov.temperature.myapplication.constants.MEASUREMENTS_LOADED_BROADCAST
import ua.skachkov.temperature.myapplication.constants.MEASUREMENTS_STARTED_LOADING_BROADCAST
import ua.skachkov.temperature.myapplication.constants.MEASUREMENTS_DATA
import ua.skachkov.temperature.myapplication.constants.MEASUREMENTS_URL_EXTRA
import ua.skachkov.temperature.myapplication.data.WeatherData
import javax.inject.Inject

const val weatherMeasurementsServiceLabel = "WeatherMeasurementsService"

class UpdateWeatherMeasurementsService : IntentService(weatherMeasurementsServiceLabel) {
    @Inject
    lateinit var dateProvider: DateProvider
    @Inject
    lateinit var networkMeasurementsLoader: NetworkMeasurementsLoader

    override fun onCreate() {
        super.onCreate()
        app.component.inject(this)
    }

    override fun onHandleIntent(intent: Intent?) {
        // TODO cache values
        val measurementsUrl = intent?.extras?.getString(MEASUREMENTS_URL_EXTRA) ?: return

        val localBroadcastManager = LocalBroadcastManager.getInstance(this)
        val measurementsStartedLoadingIntent = Intent(MEASUREMENTS_STARTED_LOADING_BROADCAST)
        localBroadcastManager.sendBroadcast(measurementsStartedLoadingIntent)

        val temperatureData = loadWeatherData(measurementsUrl, networkMeasurementsLoader, dateProvider)
        val loadedMeasurementsIntent = Intent(MEASUREMENTS_LOADED_BROADCAST)
        loadedMeasurementsIntent.putExtra(MEASUREMENTS_DATA, temperatureData)
        localBroadcastManager.sendBroadcast(loadedMeasurementsIntent)
    }

    companion object {
        fun loadWeatherData(measurementsUrl: String, networkMeasurementsLoader: NetworkMeasurementsLoader, dateProvider: DateProvider): WeatherData {
            val syncDate = dateProvider.currentDateFormatted()
            return try {
                val (temperature, humidity) = networkMeasurementsLoader.getMeasurements(measurementsUrl)
                WeatherData(temperature, humidity, syncDate)
            } catch (e: MeasurementsDataLoadException) {
                WeatherData(statusMessage = e.message, syncDate = syncDate)
            }
        }
    }
}