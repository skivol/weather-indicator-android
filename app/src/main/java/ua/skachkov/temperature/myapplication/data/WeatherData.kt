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
        private val temperature: String = "",
        private val humidity: String = "",
        val syncDate: String,
        val statusMessage: String = OK_STATUS
) : Parcelable {
    @IgnoredOnParcel
    private val formattedMeasurements = "t: $temperature°, Rh: $humidity"
    @IgnoredOnParcel
    val formattedWeatherMeasurementsOrStatusIfError: String = if (statusMessage == OK_STATUS) formattedMeasurements else statusMessage
}