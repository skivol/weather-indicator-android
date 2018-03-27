package ua.skachkov.temperature.myapplication

import ua.skachkov.temperature.myapplication.di.AppModule


/**
 * @author Ivan Skachkov
 * Created on 3/11/2018.
 */
class TestApp() : App() {
    override val component: MockAppComponent by lazy {
        DaggerMockAppComponent
                .builder()
                .appModule(AppModule(this))
                .mockConfigModule(MockConfigModule(this))
                .build()
    }
}