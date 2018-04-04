package ua.skachkov.temperature.myapplication.utils

import android.content.Context
import android.content.Intent
import android.os.Build

/**
 * @author Ivan Skachkov
 * Created on 4/3/2018.
 */
fun startMaybeForegroundService(context: Context?, intent: Intent) {
    if (context == null) return
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
        context.startForegroundService(intent)
    } else {
        context.startService(intent)
    }
}
