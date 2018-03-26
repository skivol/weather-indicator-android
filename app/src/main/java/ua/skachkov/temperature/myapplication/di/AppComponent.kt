package ua.skachkov.temperature.myapplication.di

import ua.skachkov.temperature.myapplication.App
import ua.skachkov.temperature.myapplication.activity.TemperatureActivity
import ua.skachkov.temperature.myapplication.service.UpdateTemperatureService
import dagger.Component
import javax.inject.Singleton

/**
 * @author Ivan Skachkov
 * Created on 3/10/2018.
 */
@Singleton
@Component(modules = [AppModule::class, NetworkModule::class, ConfigModule::class])
interface AppComponent {
    fun inject(app: App)
    fun inject(temperatureActivity: TemperatureActivity)
    fun inject(updateTemperatureService: UpdateTemperatureService)
}