package ua.skachkov.temperature.myapplication

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.TYPE_WIFI
import android.util.Log
import ua.skachkov.temperature.myapplication.data.ConfigData
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import ua.skachkov.temperature.myapplication.data.NetworkWeatherMeasurements
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import javax.inject.Inject

/**
 * @author Ivan Skachkov
 * Created on 3/6/2018.
 */
class MeasurementsLoadService @Inject constructor(
        private val context: Context,
        private val client: OkHttpClient,
        // TODO handle change of configuration (namely temperatureUrl)
        private val configData: ConfigData) {

    fun getMeasurements(): NetworkWeatherMeasurements {
        // https://developer.android.com/training/basics/network-ops/managing.html
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        // TODO use newer methods for checking connectivity
        val wifiEnabled = connMgr.getNetworkInfo(TYPE_WIFI).isConnected
        if (!wifiEnabled) throw TemperatureDataLoadException(context.getString(R.string.no_network))

        return fetchWeatherMeasurements()
    }

    private fun fetchWeatherMeasurements(): NetworkWeatherMeasurements {
        val temperatureRequest = Request.Builder()
                .url(configData.temperatureUrl)
                .build()

        val noDataAvailable = context.getString(R.string.no_data_available)
        return try {
            val response = client.newCall(temperatureRequest).execute()
            val temperatureString = response.body()?.string()

            val temperatureJson = JSONObject(temperatureString) // TODO consider using Retrofit or Volley
            return NetworkWeatherMeasurements(
                    temperatureJson.getDouble("Temperature").toString(),
                    temperatureJson.getDouble("Humidity").toString()
            )
        } catch (e: IOException) {
            Log.d("TEMP_LOADING", e.message, e)
            when (e) {
                is SocketTimeoutException, is ConnectException -> throw TemperatureDataLoadException(noDataAvailable)
                else -> throw e
            }
        }
    }
}