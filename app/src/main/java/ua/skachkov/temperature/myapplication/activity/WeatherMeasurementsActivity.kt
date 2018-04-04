package ua.skachkov.temperature.myapplication.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import org.jetbrains.anko.setContentView
import org.jetbrains.anko.startActivity
import ua.skachkov.temperature.myapplication.R
import ua.skachkov.temperature.myapplication.app
import ua.skachkov.temperature.myapplication.data.ConfigData
import ua.skachkov.temperature.myapplication.di.ConfigModule
import ua.skachkov.temperature.myapplication.preferences.SettingsActivity
import ua.skachkov.temperature.myapplication.service.MeasurementsUpdatedBroadcastReceiver
import ua.skachkov.temperature.myapplication.service.UpdateWeatherMeasurementsJob
import ua.skachkov.temperature.myapplication.utils.registerMeasurementsLoadedBroadcastReceiver
import ua.skachkov.temperature.myapplication.utils.unregisterLocalReceiver
import java.util.*
import javax.inject.Inject


class WeatherMeasurementsActivity() : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    @Inject
    lateinit var ui: WeatherMeasurementsActivityUI
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
        timer = Timer()
        timer!!.scheduleAtFixedRate(createTimerTask(), 0, configData.measurementsLoadingPeriodInSeconds * 1000L)
    }

    private fun createTimerTask(): TimerTask {
        return object : TimerTask() {
            override fun run() {
                UpdateWeatherMeasurementsJob.scheduleJob(configData.measurementsUrl)
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

