package ua.skachkov.temperature.myapplication

import android.content.Context
import dagger.Module
import dagger.Provides
import ua.skachkov.temperature.myapplication.data.ConfigData
import ua.skachkov.temperature.myapplication.di.ConfigModule
import javax.inject.Singleton

const val defaultMeasurementsLoadingPeriodInSeconds = 60

/**
 * @author Ivan Skachkov
 * Created on 3/22/2018.
 */
@Module
class MockConfigModule(context: Context, var measurementsUrl: String = "localhost:8080") : ConfigModule(context) {
    override fun provideConfigData(): ConfigData {
        return ConfigData(measurementsUrl, defaultMeasurementsLoadingPeriodInSeconds)
    }

    @Provides
    @Singleton
    fun provideMockConfigModule() = this
}