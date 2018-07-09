package ua.skachkov.temperature.myapplication.service

import android.app.Service
import com.evernote.android.job.Job
import com.evernote.android.job.JobRequest
import com.evernote.android.job.util.support.PersistableBundleCompat
import ua.skachkov.temperature.myapplication.App
import ua.skachkov.temperature.myapplication.app
import ua.skachkov.temperature.myapplication.constants.MEASUREMENTS_URL_EXTRA
import ua.skachkov.temperature.myapplication.data.WeatherData
import ua.skachkov.temperature.myapplication.network.MeasurementsDataLoadException
import ua.skachkov.temperature.myapplication.network.NetworkMeasurementsLoader
import ua.skachkov.temperature.myapplication.storage.updateSuccessfulMeasurementsDataLoaded
import ua.skachkov.temperature.myapplication.utils.DateProvider
import ua.skachkov.temperature.myapplication.utils.sendMeasurementsLoadedBroadcast
import ua.skachkov.temperature.myapplication.utils.sendMeasurementsStartedLoadingBroadcast
import javax.inject.Inject

const val FETCH_MEASUREMENTS_JOB_TAG = "ua.skachkov.temperature.FETCH_MEASUREMENTS_JOB_TAG"

class UpdateWeatherMeasurementsJob : Job() {
    @Inject
    lateinit var dateProvider: DateProvider
    @Inject
    lateinit var networkMeasurementsLoader: NetworkMeasurementsLoader

    override fun onRunJob(params: Params): Result {
        // See docs for "getContext"
        when (context) {
            is Service -> (context as Service).app.component.inject(this)
            is App -> (context as App).component.inject(this)
            else -> return Result.FAILURE
        }
        val measurementsUrl = params.extras.getString(MEASUREMENTS_URL_EXTRA, null)
                ?: return Result.FAILURE

        sendMeasurementsStartedLoadingBroadcast(context)
        val measurementsData = loadWeatherData(measurementsUrl, networkMeasurementsLoader, dateProvider)
        updateSuccessfulMeasurementsDataLoaded(context, measurementsData)
        sendMeasurementsLoadedBroadcast(context, measurementsData)

        return Result.SUCCESS
    }

    companion object {
        fun loadWeatherData(measurementsUrl: String, networkMeasurementsLoader: NetworkMeasurementsLoader, dateProvider: DateProvider): WeatherData {
            val syncDate = dateProvider.currentDateFormatted()
            return try {
                val (temperature, humidity) = networkMeasurementsLoader.getMeasurements(measurementsUrl)
                WeatherData(temperature, humidity, syncDate)
            } catch (e: MeasurementsDataLoadException) {
                WeatherData(statusMessage = e.message, syncDate = syncDate)
            }
        }

        fun scheduleJob(measurementsUrl: String) {
            val bundle = PersistableBundleCompat()
            bundle.putString(MEASUREMENTS_URL_EXTRA, measurementsUrl)
            JobRequest.Builder(FETCH_MEASUREMENTS_JOB_TAG)
                    .addExtras(bundle).startNow()
                    .build()
                    .schedule()
        }
    }
}