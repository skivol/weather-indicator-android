package ua.skachkov.temperature.myapplication.di

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import ua.skachkov.temperature.myapplication.R
import ua.skachkov.temperature.myapplication.data.ConfigData
import javax.inject.Singleton

/**
 * @author Ivan Skachkov
 * Created on 3/20/2018.
 */
@Module
open class ConfigModule(val context: Context) {
    open fun provideConfigData(): ConfigData {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return ConfigData(provideMeasurementsUrl(preferences), provideMeasurementsLoadingPeriodInSeconds(preferences))
    }

    @Provides
    @Singleton
    fun provideConfigModule() = this

    private fun provideMeasurementsUrl(sharedPreferences: SharedPreferences) = sharedPreferences
            .getString(context.getString(R.string.pref_measurements_url_key), context.getString(R.string.default_measurements_url))

    private fun provideMeasurementsLoadingPeriodInSeconds(sharedPreferences: SharedPreferences): Int {
        val loadingPeriodPrefKey = context.getString(R.string.pref_measurements_loading_period_in_seconds_key)
        val defaultLoadingPeriodPrefValue = context.getString(R.string.default_measurements_update_period_in_seconds)
        return sharedPreferences.getString(loadingPeriodPrefKey, defaultLoadingPeriodPrefValue).toInt()
    }
}