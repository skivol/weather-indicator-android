package ua.skachkov.temperature.myapplication.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import org.jetbrains.anko.setContentView
import org.jetbrains.anko.startActivity
import ua.skachkov.temperature.myapplication.DateProvider
import ua.skachkov.temperature.myapplication.R
import ua.skachkov.temperature.myapplication.app
import ua.skachkov.temperature.myapplication.constants.MEASUREMENTS_LOADED_BROADCAST
import ua.skachkov.temperature.myapplication.constants.MEASUREMENTS_STARTED_LOADING_BROADCAST
import ua.skachkov.temperature.myapplication.data.ConfigData
import ua.skachkov.temperature.myapplication.data.WeatherData
import ua.skachkov.temperature.myapplication.di.ConfigModule
import ua.skachkov.temperature.myapplication.network.NetworkMeasurementsLoader
import ua.skachkov.temperature.myapplication.preferences.SettingsActivity
import ua.skachkov.temperature.myapplication.service.MeasurementsUpdatedBroadcastReceiver
import ua.skachkov.temperature.myapplication.service.UpdateWeatherMeasurementsService
import java.util.*
import javax.inject.Inject

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

class WeatherMeasurementsActivity() : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    @Inject
    lateinit var ui: WeatherMeasurementsActivityUI
    @Inject
    lateinit var dateProvider: DateProvider
    @Inject
    lateinit var weatherNetworkMeasurementsLoader: NetworkMeasurementsLoader
    @Inject
    lateinit var configModule: ConfigModule

    lateinit var configData: ConfigData

    private var timer: Timer? = null

    private val measurementsUpdateListener = MeasurementsUpdatedBroadcastReceiver(
            { ui.onMeasurementsStartedLoading() },
            { ui.onMeasurementsLoaded(it) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app.component.inject(this)

        // Set default settings
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
        // Fetch settings
        refreshConfigData()
        // Listen to settings updates
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this)

        ui.setContentView(this)

        registerMeasurementsLoadedBroadcastReceiver(this, measurementsUpdateListener)
    }

    override fun onResume() {
        super.onResume()
        scheduleTemperatureUpdate()
    }

    override fun onPause() {
        super.onPause()
        cancelMeasurementsUpdate()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        refreshConfigData()
    }

    private fun refreshConfigData() {
        configData = configModule.provideConfigData()
    }

    private fun scheduleTemperatureUpdate() {
        // If using the service
        // val loadTemperatureIntent = Intent(this, UpdateWeatherMeasurementsService::class.java)
        // startService(loadTemperatureIntent)
        timer = Timer()
        timer!!.scheduleAtFixedRate(createTimerTask(), 0, configData.measurementsLoadingPeriodInSeconds * 1000L)
    }

    private fun createTimerTask(): TimerTask {
        return object : TimerTask() {
            override fun run() {
                // Using AsyncTask in order for Espresso synchronization to work out of the box and simplify the functionality
                createTemperatureLoadingTask().execute()
            }
        }
    }

    private fun createTemperatureLoadingTask(): AsyncTask<Void, Unit, WeatherData> {
        return object : AsyncTask<Void, Unit, WeatherData>() {
            override fun onPreExecute() {
                ui.onMeasurementsStartedLoading()
            }

            override fun doInBackground(vararg params: Void?): WeatherData {
                return UpdateWeatherMeasurementsService.loadWeatherData(configData.measurementsUrl, weatherNetworkMeasurementsLoader, dateProvider)
            }

            override fun onPostExecute(result: WeatherData) {
                ui.onMeasurementsLoaded(result)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Menu
        val menuInflater = MenuInflater(this)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.settings_menu -> {
                startActivity<SettingsActivity>()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        unregisterLocalReceiver(this, measurementsUpdateListener)
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
        super.onDestroy()
    }

    private fun cancelMeasurementsUpdate() {
        timer?.cancel()
        timer = null
    }
}

