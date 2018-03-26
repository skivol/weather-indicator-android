package ua.skachkov.temperature.myapplication

import android.app.Activity
import android.app.Application
import android.app.Service
import ua.skachkov.temperature.myapplication.di.AppComponent
import ua.skachkov.temperature.myapplication.di.AppModule
import ua.skachkov.temperature.myapplication.di.DaggerAppComponent
import ua.skachkov.temperature.myapplication.di.NetworkModule

val Activity.app: App
    get() = application as App
val Service.app: App
    get() = application as App

/**
 * @author Ivan Skachkov
 * Created on 3/9/2018.
 */
open class App: Application() {
    open val component: AppComponent by lazy {
        DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
                .build()
    }
}