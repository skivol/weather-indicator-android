package ua.skachkov.temperature.myapplication

import ua.skachkov.temperature.myapplication.di.AppComponent
import ua.skachkov.temperature.myapplication.di.AppModule
import ua.skachkov.temperature.myapplication.di.NetworkModule
import dagger.Component
import javax.inject.Singleton


/**
 * @author Ivan Skachkov
 * Created on 3/11/2018.
 */
@Singleton
@Component(modules = [AppModule::class, NetworkModule::class, MockConfigModule::class])
interface MockAppComponent : AppComponent {
    fun inject(measurementsTest : MeasurementsInstrumentedTest)
}