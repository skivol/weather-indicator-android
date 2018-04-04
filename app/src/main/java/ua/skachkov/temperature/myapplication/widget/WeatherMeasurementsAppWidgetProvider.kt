package ua.skachkov.temperature.myapplication.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.widget.RemoteViews
import ua.skachkov.temperature.myapplication.R
import ua.skachkov.temperature.myapplication.utils.startMaybeForegroundService

fun createAppWidgetRemoteViews(packageName: String?) = RemoteViews(packageName, R.layout.appwidget_layout)

/**
 * @author Ivan Skachkov
 * Created on 3/13/2018.
 */
class WeatherMeasurementsAppWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        startMaybeForegroundService(context, Intent(context, UpdateWeatherMeasurementsWidgetService::class.java))

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
