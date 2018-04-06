package ua.skachkov.temperature.myapplication.utils

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import ua.skachkov.temperature.myapplication.R
import ua.skachkov.temperature.myapplication.widget.UpdateWeatherMeasurementsWidgetService

/**
 * @author Ivan Skachkov
 * Created on 4/4/2018.
 */
const val NOTIFICATION_CHANNEL_ID = "measurements_notifications_channel"
const val WEATHER_MEASUREMENTS_WIDGET_SERVICE_STOP_ACTION = "ua.skachkov.temperature.WEATHER_MEASUREMENTS_WIDGET_SERVICE_STOP_ACTION"

fun setupNotificationChannel(context: Context) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = context.getString(R.string.app_name)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val notificationChannel = android.app.NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
        notificationChannel.description = context.getString(R.string.channel_description)
        notificationManager.createNotificationChannel(notificationChannel)
    }
}

fun createWidgetForegroundNotification(context: Context): Notification? {
    val stopIntent = Intent(context, UpdateWeatherMeasurementsWidgetService::class.java)
    stopIntent.action = WEATHER_MEASUREMENTS_WIDGET_SERVICE_STOP_ACTION
    val pendingStopIntent = PendingIntent.getService(context, 0, stopIntent, 0)
    return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.notification_details_text))
            .setSubText(context.getString(R.string.notification_sub_text))
            .setContentIntent(pendingStopIntent)
            .setAutoCancel(true)
            .setOngoing(true)
            .build()
}
