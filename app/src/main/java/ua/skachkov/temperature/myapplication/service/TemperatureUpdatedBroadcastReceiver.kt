package ua.skachkov.temperature.myapplication.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ua.skachkov.temperature.myapplication.constants.TEMPERATURE_DATA
import ua.skachkov.temperature.myapplication.constants.TEMPERATURE_LOADED_BROADCAST
import ua.skachkov.temperature.myapplication.constants.TEMPERATURE_STARTED_LOADING_BROADCAST
import ua.skachkov.temperature.myapplication.data.TemperatureData

/**
 * @author Ivan Skachkov
 * Created on 3/18/2018.
 */
class TemperatureUpdatedBroadcastReceiver(
        val onTemperatureStartedLoading: () -> Unit,
        val onTemperatureLoaded: (TemperatureData) -> Unit
) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            when (intent.action) {
                TEMPERATURE_STARTED_LOADING_BROADCAST -> onTemperatureStartedLoading.invoke()
                TEMPERATURE_LOADED_BROADCAST -> onTemperatureLoaded.invoke(intent.getParcelableExtra(TEMPERATURE_DATA))
            }
        }
    }
}