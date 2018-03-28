package ua.skachkov.temperature.myapplication.widget

import android.app.PendingIntent
import android.app.Service
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.util.TypedValue.COMPLEX_UNIT_SP
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

fun createAppWidgetRemoteViews(packageName: String?) = RemoteViews(packageName, R.layout.appwidget_layout)

/**
 * @author Ivan Skachkov
 * Created on 3/13/2018.
 */
class WeatherMeasurementsAppWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        context?.startService(Intent(context, UpdateWeatherMeasurementsWidgetService::class.java))
        if (appWidgetManager != null && appWidgetIds != null) {
            for (appWidgetId in appWidgetIds) {
                val widgetOptions = appWidgetManager.getAppWidgetOptions(appWidgetIds[0])
                onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, widgetOptions)
            }
        }
    }

    override fun onAppWidgetOptionsChanged(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetId: Int, newOptions: Bundle?) {
        // Adjust measurements text size depending on the size of widget
        if (context != null && appWidgetManager != null && newOptions != null) {
            val minHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
            val cellsCount = (minHeight + 30) / 70 // https://developer.android.com/guide/practices/ui_guidelines/widget_design.html#anatomy_determining_size

            val textSize = when(cellsCount) {
                2 -> 26
                3 -> 42
                in 4..Int.MAX_VALUE -> 54
                else -> 22 // default text size for 1 cell height
            }.toFloat()
            val remoteViews = createAppWidgetRemoteViews(context.packageName)
            remoteViews.setTextViewTextSize(R.id.temperature_text, COMPLEX_UNIT_SP, textSize)
            remoteViews.setTextViewTextSize(R.id.humidity_text, COMPLEX_UNIT_SP, textSize)
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
        }
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

                // Sync date & refresh button
                remoteViews.setViewVisibility(R.id.refresh_button, View.VISIBLE)
                remoteViews.setViewVisibility(R.id.load_progressbar, View.GONE)
                remoteViews.setViewVisibility(R.id.sync_date_text, View.VISIBLE)
                remoteViews.setTextViewText(R.id.sync_date_text, it.syncDate)

                val refreshIntent = Intent(this, UpdateWeatherMeasurementsWidgetService::class.java)
                val pendingRefreshIntent = PendingIntent.getService(this, 0, refreshIntent, 0)
                remoteViews.setOnClickPendingIntent(R.id.sync_date_and_refresh_button_section, pendingRefreshIntent)

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
