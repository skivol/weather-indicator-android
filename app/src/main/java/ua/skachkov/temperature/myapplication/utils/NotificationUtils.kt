package ua.skachkov.temperature.myapplication.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import ua.skachkov.temperature.myapplication.R

/**
 * @author Ivan Skachkov
 * Created on 4/4/2018.
 */
fun createWidgetForegroundOngoingNotification(context: Context): Notification? {
    return NotificationCompat.Builder(context)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(context.getString(R.string.notification_details_text))
            .setOngoing(true)
            .build()
}
