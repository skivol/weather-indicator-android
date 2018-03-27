package ua.skachkov.temperature.myapplication.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ua.skachkov.temperature.myapplication.constants.MEASUREMENTS_DATA
import ua.skachkov.temperature.myapplication.constants.MEASUREMENTS_LOADED_BROADCAST
import ua.skachkov.temperature.myapplication.constants.MEASUREMENTS_STARTED_LOADING_BROADCAST
import ua.skachkov.temperature.myapplication.data.WeatherData

/**
 * @author Ivan Skachkov
 * Created on 3/18/2018.
 */
class MeasurementsUpdatedBroadcastReceiver(
        val onTemperatureStartedLoading: () -> Unit,
        val onTemperatureLoaded: (WeatherData) -> Unit
) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            when (intent.action) {
                MEASUREMENTS_STARTED_LOADING_BROADCAST -> onTemperatureStartedLoading.invoke()
                MEASUREMENTS_LOADED_BROADCAST -> onTemperatureLoaded.invoke(intent.getParcelableExtra(MEASUREMENTS_DATA))
            }
        }
    }
}