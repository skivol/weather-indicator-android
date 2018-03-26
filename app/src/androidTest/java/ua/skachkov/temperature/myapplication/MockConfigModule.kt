package ua.skachkov.temperature.myapplication

import ua.skachkov.temperature.myapplication.data.ConfigData
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author Ivan Skachkov
 * Created on 3/22/2018.
 */
@Module
class MockConfigModule(var configData: ConfigData = ConfigData("localhost:8080")) {
    @Provides
    @Singleton
    fun provideConfig() = configData

    @Provides
    @Singleton
    fun provideConfigModule() = this
}