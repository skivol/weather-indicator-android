package ua.skachkov.temperature.myapplication.network

import android.content.Context
import android.net.ConnectivityManager
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import ua.skachkov.temperature.myapplication.R
import ua.skachkov.temperature.myapplication.data.NetworkWeatherMeasurements
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author Ivan Skachkov
 * Created on 3/6/2018.
 */
@Singleton
class NetworkMeasurementsLoader @Inject constructor(
        private val context: Context,
        private val client: OkHttpClient) {

    fun getMeasurements(measurementsUrl: String): NetworkWeatherMeasurements {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connMgr.activeNetworkInfo
        val connectedToWiFi = activeNetwork != null && activeNetwork.type == ConnectivityManager.TYPE_WIFI && activeNetwork.isConnected
        if (!connectedToWiFi) throw MeasurementsDataLoadException(context.getString(R.string.no_network))

        return fetchWeatherMeasurements(measurementsUrl)
    }

    private fun fetchWeatherMeasurements(measurementsUrl: String): NetworkWeatherMeasurements {
        val temperatureRequest = Request.Builder()
                .url(measurementsUrl)
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
            throw MeasurementsDataLoadException(noDataAvailable, e)
        }
    }
}