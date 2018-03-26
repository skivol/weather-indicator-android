package ua.skachkov.temperature.myapplication.di

import android.content.Context
import android.preference.PreferenceManager
import ua.skachkov.temperature.myapplication.R
import ua.skachkov.temperature.myapplication.data.ConfigData
import dagger.Module
import dagger.Provides

const val defaultMeasurementsLoadingPeriod = 60L * 1000
const val defaultTemperatureLoadingTimeout = 30L

/**
 * @author Ivan Skachkov
 * Created on 3/20/2018.
 */
@Module
class ConfigModule {
    @Provides
    fun provideConfig(context : Context): ConfigData = ConfigData(provideTemperatureUrl(context))

    fun provideTemperatureUrl(context : Context): String = PreferenceManager.getDefaultSharedPreferences(context)
            .getString(context.getString(R.string.pref_temperature_url_key), context.getString(R.string.default_temperature_url))
}