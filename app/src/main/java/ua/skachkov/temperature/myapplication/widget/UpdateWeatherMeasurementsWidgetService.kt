package ua.skachkov.temperature.myapplication.widget

import android.app.PendingIntent
import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.view.View
import android.widget.RemoteViews
import ua.skachkov.temperature.myapplication.R
import ua.skachkov.temperature.myapplication.activity.WeatherMeasurementsActivity
import ua.skachkov.temperature.myapplication.app
import ua.skachkov.temperature.myapplication.di.ConfigModule
import ua.skachkov.temperature.myapplication.service.MeasurementsUpdatedBroadcastReceiver
import ua.skachkov.temperature.myapplication.service.UpdateWeatherMeasurementsJob
import ua.skachkov.temperature.myapplication.utils.*
import javax.inject.Inject

const val WEATHER_MEASUREMENTS_WIDGET_SERVICE_NOTIFICATION_ID = 1337

class UpdateWeatherMeasurementsWidgetService : Service() {
    @Inject
    lateinit var configModule: ConfigModule

    private val binder = Binder()

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    private val measurementsUpdateListener = MeasurementsUpdatedBroadcastReceiver(
            {
                val views = createAppWidgetRemoteViews(packageName)
                views.setViewVisibility(R.id.load_progressbar, View.VISIBLE)
                views.setViewVisibility(R.id.refresh_button, View.GONE)
                updateWidgets(views)
            },
            {
                val remoteViews = createAppWidgetRemoteViews(packageName)

                // Measurements section
                if (it.success) {
                    // Show measurements
                    remoteViews.setViewVisibility(R.id.error_message_text, View.GONE)
                    remoteViews.setViewVisibility(R.id.measurements_fields, View.VISIBLE)
                    remoteViews.setTextViewText(R.id.temperature_text, it.formattedTemperature)
                    remoteViews.setTextViewText(R.id.humidity_text, it.humidity)
                } else {
                    // Show error message
                    remoteViews.setViewVisibility(R.id.measurements_fields, View.GONE)
                    remoteViews.setViewVisibility(R.id.error_message_text, View.VISIBLE)
                    remoteViews.setTextViewText(R.id.error_message_text, it.statusMessage)
                }

                val intent = Intent(this, WeatherMeasurementsActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
                remoteViews.setOnClickPendingIntent(R.id.measurements_fields, pendingIntent)
                remoteViews.setOnClickPendingIntent(R.id.error_message_text, pendingIntent)

                // Sync date & refresh button
                remoteViews.setViewVisibility(R.id.refresh_button, View.VISIBLE)
                remoteViews.setViewVisibility(R.id.load_progressbar, View.GONE)
                remoteViews.setViewVisibility(R.id.sync_date_text, View.VISIBLE)
                remoteViews.setTextViewText(R.id.sync_date_text, it.syncDate)

                val refreshIntent = Intent(this, UpdateWeatherMeasurementsWidgetService::class.java)
                val pendingRefreshIntent = PendingIntent.getService(this, 0, refreshIntent, 0)
                remoteViews.setOnClickPendingIntent(R.id.sync_date_and_refresh_button_section, pendingRefreshIntent)

                updateWidgets(remoteViews)
            })

    override fun onCreate() {
        app.component.inject(this)

        // Setup notification channel
        setupNotificationChannel(this)
        // Show foreground notification
        val foregroundNotification = createWidgetForegroundNotification(this)
        startForeground(WEATHER_MEASUREMENTS_WIDGET_SERVICE_NOTIFICATION_ID, foregroundNotification)

        registerMeasurementsLoadedBroadcastReceiver(this, measurementsUpdateListener)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            WEATHER_MEASUREMENTS_WIDGET_SERVICE_STOP_ACTION -> stopSelf()
            else -> UpdateWeatherMeasurementsJob.scheduleJob(configModule.provideConfigData().measurementsUrl)
        }
        return START_NOT_STICKY
    }

    private fun updateWidgets(remoteViews: RemoteViews) {
        val thisWidget = ComponentName(this, WeatherMeasurementsAppWidgetProvider::class.java)
        val appWidgetManager = AppWidgetManager.getInstance(this)
        appWidgetManager.updateAppWidget(thisWidget, remoteViews)
    }

    override fun onDestroy() {
        unregisterLocalReceiver(this, measurementsUpdateListener)
        stopForeground(true)
    }
}
