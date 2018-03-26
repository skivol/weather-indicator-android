package ua.skachkov.temperature.myapplication

import android.app.Application
import android.content.Context
import android.support.test.runner.AndroidJUnitRunner

const val MOCK_SERVER_URL = "localhost:8080"

/**
 * @author Ivan Skachkov
 * Created on 3/11/2018.
 */
class MockTestRunner : AndroidJUnitRunner() {
    @Throws(InstantiationException::class, IllegalAccessException::class, ClassNotFoundException::class)
    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        return super.newApplication(cl, TestApp::class.java.name, context)
    }
}

