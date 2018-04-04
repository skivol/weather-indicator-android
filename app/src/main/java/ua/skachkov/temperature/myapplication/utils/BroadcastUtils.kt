package ua.skachkov.temperature.myapplication.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import ua.skachkov.temperature.myapplication.constants.MEASUREMENTS_DATA
import ua.skachkov.temperature.myapplication.constants.MEASUREMENTS_LOADED_BROADCAST
import ua.skachkov.temperature.myapplication.constants.MEASUREMENTS_STARTED_LOADING_BROADCAST
import ua.skachkov.temperature.myapplication.data.WeatherData
import ua.skachkov.temperature.myapplication.service.MeasurementsUpdatedBroadcastReceiver

/**
 * @author Ivan Skachkov
 * Created on 4/4/2018.
 */

fun registerMeasurementsLoadedBroadcastReceiver(context: Context, measurementsUpdatedBroadcastReceiver: MeasurementsUpdatedBroadcastReceiver) {
    val localBroadcastManager = LocalBroadcastManager.getInstance(context)

    val measurementsStartedLoadingIntentFilter = IntentFilter(MEASUREMENTS_STARTED_LOADING_BROADCAST)
    localBroadcastManager.registerReceiver(measurementsUpdatedBroadcastReceiver, measurementsStartedLoadingIntentFilter)

    val temperatureLoadedIntentFilter = IntentFilter(MEASUREMENTS_LOADED_BROADCAST)
    localBroadcastManager.registerReceiver(measurementsUpdatedBroadcastReceiver, temperatureLoadedIntentFilter)
}

fun unregisterLocalReceiver(context: Context, receiver: BroadcastReceiver) {
    LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver)
}

fun sendMeasurementsLoadedBroadcast(context: Context, temperatureData: WeatherData) {
    val loadedMeasurementsIntent = Intent(MEASUREMENTS_LOADED_BROADCAST)
    loadedMeasurementsIntent.putExtra(MEASUREMENTS_DATA, temperatureData)
    val localBroadcastManager = LocalBroadcastManager.getInstance(context)
    localBroadcastManager.sendBroadcast(loadedMeasurementsIntent)
}

fun sendMeasurementsStartedLoadingBroadcast(context: Context) {
    val localBroadcastManager = LocalBroadcastManager.getInstance(context)
    val measurementsStartedLoadingIntent = Intent(MEASUREMENTS_STARTED_LOADING_BROADCAST)
    localBroadcastManager.sendBroadcast(measurementsStartedLoadingIntent)
}
