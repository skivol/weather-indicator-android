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
import ua.skachkov.temperature.myapplication.activity.TemperatureActivity
import ua.skachkov.temperature.myapplication.activity.registerTemperatureLoadedBroadcastReceiver
import ua.skachkov.temperature.myapplication.activity.unregisterLocalReceiver
import ua.skachkov.temperature.myapplication.service.TemperatureUpdatedBroadcastReceiver
import ua.skachkov.temperature.myapplication.service.UpdateTemperatureService

/**
 * @author Ivan Skachkov
 * Created on 3/13/2018.
 */
class TemperatureAppWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        // TODO Optimize networking (save loaded data: cache/db?, avoid excessive requests etc)
        context?.startService(Intent(context, UpdateTempWidgetService::class.java))
    }
}

class UpdateTempWidgetService : Service() {
    private val binder = Binder()

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    private val temperatureUpdateListener = TemperatureUpdatedBroadcastReceiver(
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

                val intent = Intent(this, TemperatureActivity::class.java)
                val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
                remoteViews.setOnClickPendingIntent(R.id.temperature_text, pendingIntent)

                remoteViews.setTextViewText(R.id.temperature_text, it.temperatureOrStatusIfError)

                val refreshIntent = Intent(this, UpdateTempWidgetService::class.java)
                val pendingRefreshIntent = PendingIntent.getService(this, 0, refreshIntent, 0)
                remoteViews.setOnClickPendingIntent(R.id.refresh_button, pendingRefreshIntent)

                updateWidgets(remoteViews)
                stopSelf()
            })

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        registerTemperatureLoadedBroadcastReceiver(this, temperatureUpdateListener)

        val serviceIntent = Intent(this, UpdateTemperatureService::class.java)
        startService(serviceIntent)

        // Option to run the loading periodically (can potentially drain the battery)
        /* val pendingServiceIntent = PendingIntent.getService(this, 0, serviceIntent, 0)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingServiceIntent)
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), defaultTemperatureLoadingPeriod, pendingServiceIntent) */

        return super.onStartCommand(intent, flags, startId)
    }

    private fun createAppWidgetRemoteViews() = RemoteViews(packageName, R.layout.appwidget_layout)

    private fun updateWidgets(remoteViews: RemoteViews) {
        val thisWidget = ComponentName(this, TemperatureAppWidgetProvider::class.java)
        val appWidgetManager = AppWidgetManager.getInstance(this)
        appWidgetManager.updateAppWidget(thisWidget, remoteViews)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterLocalReceiver(this, temperatureUpdateListener)
    }
}
