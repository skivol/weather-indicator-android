package ua.skachkov.temperature.myapplication.widget

import android.app.PendingIntent
import android.app.Service
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.view.View
import android.widget.RemoteViews
import ua.skachkov.temperature.myapplication.R
import ua.skachkov.temperature.myapplication.activity.WeatherMeasurementsActivity
import ua.skachkov.temperature.myapplication.activity.registerMeasurementsLoadedBroadcastReceiver
import ua.skachkov.temperature.myapplication.activity.unregisterLocalReceiver
import ua.skachkov.temperature.myapplication.app
import ua.skachkov.temperature.myapplication.constants.MEASUREMENTS_URL_EXTRA
import ua.skachkov.temperature.myapplication.di.ConfigModule
import ua.skachkov.temperature.myapplication.service.MeasurementsUpdatedBroadcastReceiver
import ua.skachkov.temperature.myapplication.service.UpdateWeatherMeasurementsService
import javax.inject.Inject

/**
 * @author Ivan Skachkov
 * Created on 3/13/2018.
 */
class WeatherMeasurementsAppWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        context?.startService(Intent(context, UpdateWeatherMeasurementsWidgetService::class.java))
    }
}

class UpdateWeatherMeasurementsWidgetService : Service() {
    @Inject
    lateinit var configModule: ConfigModule

    private val binder = Binder()

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    private val measurementsUpdateListener = MeasurementsUpdatedBroadcastReceiver(
            {
                val views = createAppWidgetRemoteViews()
                views.setViewVisibility(R.id.load_progressbar, View.VISIBLE)
                views.setViewVisibility(R.id.refresh_button, View.GONE)
                updateWidgets(views)
            },
            {
                val remoteViews = createAppWidgetRemoteViews()
                remoteViews.setViewVisibility(R.id.refresh_button, View.VISIBLE)
                remoteViews.setViewVisibility(R.id.date_text, View.VISIBLE)
                remoteViews.setTextViewText(R.id.date_text, it.syncDate)
                remoteViews.setViewVisibility(R.id.load_progressbar, View.GONE)

                val intent = Intent(this, WeatherMeasurementsActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
                remoteViews.setOnClickPendingIntent(R.id.measurements_text, pendingIntent)

                remoteViews.setTextViewText(R.id.measurements_text, it.formattedWeatherMeasurementsOrStatusIfError)

                val refreshIntent = Intent(this, UpdateWeatherMeasurementsWidgetService::class.java)
                val pendingRefreshIntent = PendingIntent.getService(this, 0, refreshIntent, 0)
                remoteViews.setOnClickPendingIntent(R.id.refresh_button, pendingRefreshIntent)

                updateWidgets(remoteViews)
                stopSelf()
            })

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        app.component.inject(this)
        registerMeasurementsLoadedBroadcastReceiver(this, measurementsUpdateListener)

        val serviceIntent = Intent(this, UpdateWeatherMeasurementsService::class.java)
        serviceIntent.putExtra(MEASUREMENTS_URL_EXTRA, configModule.provideConfigData().measurementsUrl)
        startService(serviceIntent)

        return super.onStartCommand(intent, flags, startId)
    }

    private fun createAppWidgetRemoteViews() = RemoteViews(packageName, R.layout.appwidget_layout)

    private fun updateWidgets(remoteViews: RemoteViews) {
        val thisWidget = ComponentName(this, WeatherMeasurementsAppWidgetProvider::class.java)
        val appWidgetManager = AppWidgetManager.getInstance(this)
        appWidgetManager.updateAppWidget(thisWidget, remoteViews)
    }

    override fun onDestroy() {
        unregisterLocalReceiver(this, measurementsUpdateListener)
        super.onDestroy()
    }
}
