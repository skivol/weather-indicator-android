package ua.skachkov.temperature.myapplication.storage

import android.content.Context
import android.preference.PreferenceManager
import ua.skachkov.temperature.myapplication.data.WeatherData

/**
 * @author Ivan Skachkov
 * Created on 09-Jul-18.
 */

const val successfulMeasurementsDataLoadedKey = "successful_measurements_data_loaded"
const val lastLoadedTemperatureKey = "last_loaded_temperature"
const val lastLoadedHumidityKey = "last_loaded_humidity"
const val lastLoadDateKey = "last_load_date"

fun updateSuccessfulMeasurementsDataLoaded(context: Context, measurementsData: WeatherData) {
    if (!measurementsData.success) return
    val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    preferences.edit()
            .putBoolean(successfulMeasurementsDataLoadedKey, true)
            .putString(lastLoadedTemperatureKey, measurementsData.temperature)
            .putString(lastLoadedHumidityKey, measurementsData.humidity)
            .putString(lastLoadDateKey, measurementsData.syncDate)
            .apply()
}

fun previousSuccessfulMeasurementsData(context: Context): WeatherData? {
    val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val hasPreviousValues = defaultSharedPreferences.getBoolean(successfulMeasurementsDataLoadedKey, false)
    if (!hasPreviousValues) return null

    val previousTemp = defaultSharedPreferences.getString(lastLoadedTemperatureKey, "")
    val previousHumidity = defaultSharedPreferences.getString(lastLoadedHumidityKey, "")
    val lastLoadDate = defaultSharedPreferences.getString(lastLoadDateKey, "")
    return WeatherData(previousTemp, previousHumidity, lastLoadDate)
}
