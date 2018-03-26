package ua.skachkov.temperature.myapplication.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton


/**
 * @author Ivan Skachkov
 * Created on 3/10/2018.
 */
@Module
open class AppModule(private val app: Application) {
    @Provides @Singleton fun provideContext(): Context = app
}