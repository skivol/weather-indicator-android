package ua.skachkov.temperature.myapplication.activity

import android.content.Context
import android.content.IntentFilter
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import ua.skachkov.temperature.myapplication.DateProvider
import ua.skachkov.temperature.myapplication.R
import ua.skachkov.temperature.myapplication.TemperatureLoadService
import ua.skachkov.temperature.myapplication.app
import ua.skachkov.temperature.myapplication.constants.TEMPERATURE_LOADED_BROADCAST
import ua.skachkov.temperature.myapplication.constants.TEMPERATURE_STARTED_LOADING_BROADCAST
import ua.skachkov.temperature.myapplication.data.TemperatureData
import ua.skachkov.temperature.myapplication.di.defaultTemperatureLoadingPeriod
import ua.skachkov.temperature.myapplication.preferences.SettingsActivity
import ua.skachkov.temperature.myapplication.service.TemperatureUpdatedBroadcastReceiver
import ua.skachkov.temperature.myapplication.service.UpdateTemperatureService
import org.jetbrains.anko.setContentView
import org.jetbrains.anko.startActivity
import java.util.*
import javax.inject.Inject

fun registerTemperatureLoadedBroadcastReceiver(context: Context, temperatureUpdatedBroadcastReceiver: TemperatureUpdatedBroadcastReceiver) {
    val localBroadcastManager = LocalBroadcastManager.getInstance(context)

    val temperatureStartedLoadingIntentFilter = IntentFilter(TEMPERATURE_STARTED_LOADING_BROADCAST)
    localBroadcastManager.registerReceiver(temperatureUpdatedBroadcastReceiver, temperatureStartedLoadingIntentFilter)

    val temperatureLoadedIntentFilter = IntentFilter(TEMPERATURE_LOADED_BROADCAST)
    localBroadcastManager.registerReceiver(temperatureUpdatedBroadcastReceiver, temperatureLoadedIntentFilter)
}

fun unregisterLocalReceiver(context: Context, receiver: TemperatureUpdatedBroadcastReceiver) {
    LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver)
}

class TemperatureActivity() : AppCompatActivity() {
    @Inject
    lateinit var ui: TemperatureActivityUI
    @Inject
    lateinit var dateProvider: DateProvider
    @Inject
    lateinit var temperatureLoadService: TemperatureLoadService

    private var timer: Timer? = null

    private val temperatureUpdateListener = TemperatureUpdatedBroadcastReceiver(
            { ui.onTemperatureStartedLoading() },
            { ui.onTemperatureLoaded(it) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app.component.inject(this)
        // TODO check/request networking permissions

        // Default settings
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)

        ui.setContentView(this)

        registerTemperatureLoadedBroadcastReceiver(this, temperatureUpdateListener)
    }

    override fun onResume() {
        super.onResume()
        scheduleTemperatureUpdate()
    }

    override fun onPause() {
        super.onPause()
        cancelTemperatureUpdate()
    }

    private fun scheduleTemperatureUpdate() {
        // If using the service
        // val loadTemperatureIntent = Intent(this, UpdateTemperatureService::class.java)
        // startService(loadTemperatureIntent)
        timer = Timer()
        timer!!.scheduleAtFixedRate(createTimerTask(), 0, defaultTemperatureLoadingPeriod)
    }

    private fun createTimerTask(): TimerTask {
        return object : TimerTask() {
            override fun run() {
                // Using AsyncTask in order for Espresso synchronization to work out of the box and simplify the functionality
                createTemperatureLoadingTask().execute()
            }
        }
    }

    private fun createTemperatureLoadingTask(): AsyncTask<Void, Unit, TemperatureData> {
        return object : AsyncTask<Void, Unit, TemperatureData>() {
            override fun onPreExecute() {
                ui.onTemperatureStartedLoading()
            }

            override fun doInBackground(vararg params: Void?): TemperatureData {
                return UpdateTemperatureService.loadTemperatureData(dateProvider, temperatureLoadService)
            }

            override fun onPostExecute(result: TemperatureData) {
                ui.onTemperatureLoaded(result)
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
        super.onDestroy()
        cancelTemperatureUpdate()
        unregisterLocalReceiver(this, temperatureUpdateListener)
    }

    private fun cancelTemperatureUpdate() {
        timer?.cancel()
        timer = null
    }
}

