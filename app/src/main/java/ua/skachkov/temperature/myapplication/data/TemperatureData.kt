package ua.skachkov.temperature.myapplication.data

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

const val OK_STATUS = "OK"

/**
 * @author Ivan Skachkov
 * Created on 3/12/2018.
 */
@Parcelize
data class TemperatureData(
        val currentTemperature: String = "",
        val statusMessage: String = OK_STATUS,
        val syncDate: String
) : Parcelable {
    @IgnoredOnParcel
    val temperatureOrStatusIfError: String = if (statusMessage == OK_STATUS) currentTemperature else statusMessage
}