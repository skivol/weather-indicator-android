package ua.skachkov.temperature.myapplication.di

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * @author Ivan Skachkov
 * Created on 3/10/2018.
 */
@Module
open class NetworkModule() {
    @Provides
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
            .build()
}