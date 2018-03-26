package ua.skachkov.temperature.myapplication.service

import android.app.IntentService
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import ua.skachkov.temperature.myapplication.DateProvider
import ua.skachkov.temperature.myapplication.TemperatureDataLoadException
import ua.skachkov.temperature.myapplication.TemperatureLoadService
import ua.skachkov.temperature.myapplication.app
import ua.skachkov.temperature.myapplication.constants.TEMPERATURE_DATA
import ua.skachkov.temperature.myapplication.constants.TEMPERATURE_LOADED_BROADCAST
import ua.skachkov.temperature.myapplication.constants.TEMPERATURE_STARTED_LOADING_BROADCAST
import ua.skachkov.temperature.myapplication.data.TemperatureData
import javax.inject.Inject

const val temperatureServiceLabel = "TemperatureService"

class UpdateTemperatureService : IntentService(temperatureServiceLabel) {
    @Inject
    lateinit var dateProvider: DateProvider
    @Inject
    lateinit var temperatureLoadService: TemperatureLoadService

    override fun onCreate() {
        super.onCreate()
        app.component.inject(this)
    }

    override fun onHandleIntent(intent: Intent?) {
        val localBroadcastManager = LocalBroadcastManager.getInstance(this)
        val temperatureStartedLoadingIntent = Intent(TEMPERATURE_STARTED_LOADING_BROADCAST)
        localBroadcastManager.sendBroadcast(temperatureStartedLoadingIntent)

        val temperatureData = loadTemperatureData(dateProvider, temperatureLoadService)
        val loadedTemperatureIntent = Intent(TEMPERATURE_LOADED_BROADCAST)
        loadedTemperatureIntent.putExtra(TEMPERATURE_DATA, temperatureData)
        localBroadcastManager.sendBroadcast(loadedTemperatureIntent)
    }

    companion object {
        fun loadTemperatureData(dateProvider: DateProvider, temperatureLoadService: TemperatureLoadService): TemperatureData {
            val syncDate = dateProvider.currentDateFormatted()
            return try {
                val temperature = temperatureLoadService.getTemperature()
                val formattedTemperature = "${temperature}°"
                TemperatureData(currentTemperature = formattedTemperature, syncDate = syncDate)
            } catch (e: TemperatureDataLoadException) {
                TemperatureData(statusMessage = e.message ?: "", syncDate = syncDate)
            }
        }
    }
}