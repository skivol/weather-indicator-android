package ua.skachkov.temperature.myapplication.data

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

const val OK_STATUS = "OK"

/**
 *
 * @author Ivan Skachkov
 * Created on 3/12/2018.
 */
@Parcelize
data class WeatherData(
        val temperature: String = "",
        val humidity: String = "",
        val syncDate: String,
        val statusMessage: String = OK_STATUS
) : Parcelable {
    @IgnoredOnParcel
    val formattedTemperature = "$temperature°"
    @IgnoredOnParcel
    private val formattedMeasurements = "t: $formattedTemperature, Rh: $humidity"

    @IgnoredOnParcel
    val success = statusMessage == OK_STATUS
    @IgnoredOnParcel
    val formattedWeatherMeasurementsOrStatusIfError: String = if (success) formattedMeasurements else statusMessage
}