package ua.skachkov.temperature.myapplication

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import ua.skachkov.temperature.myapplication.activity.TEMPERATURE_MESSAGE_ID
import ua.skachkov.temperature.myapplication.activity.TemperatureActivity
import ua.skachkov.temperature.myapplication.data.ConfigData
import ua.skachkov.temperature.myapplication.preferences.kotlinInfoTextViewId
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@LargeTest
class TemperatureInstrumentedTest {
    @Rule
    @JvmField
    val activityRule = ActivityTestRule(TemperatureActivity::class.java, false, false)

    @Inject
    lateinit var mockConfigModule: MockConfigModule

    private lateinit var mockWebServer: MockWebServer

    private val defaultTemperatureValue = -4.0

    @Before
    fun setUp() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val appContext = instrumentation.targetContext.applicationContext as App
        val mockAppComponent = appContext.component as MockAppComponent
        mockAppComponent.inject(this)

        mockWebServer = setupMockServer()
        mockTemperatureResponse(defaultTemperatureValue)
        mockConfigModule.configData = ConfigData(mockWebServer.url("/").toString())

        activityRule.launchActivity(Intent())
    }

    @Test
    fun temperatureMessage() {
        // Verify
        onView(withId(TEMPERATURE_MESSAGE_ID)).check(matches(withText("${defaultTemperatureValue}°")))
    }

    @Test
    fun checkSettingsAboutTechnologiesUsed() {
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext())
        onView(withText(R.string.pref_menu_title))
                .perform(click())
        onView(withText(R.string.about_technologies_used))
                .perform(click())
        onView(withId(kotlinInfoTextViewId)).check(matches(withText("1. Kotlin (https://kotlinlang.org)")))
    }

    @After
    fun tearDown() {
        // Tear down
        activityRule.finishActivity()
        mockWebServer.shutdown()
    }

    private fun setupMockServer() = MockWebServer()

    private fun mockTemperatureResponse(temperatureValue: Double) {
        // https://android-arsenal.com/details/1/3397
        mockWebServer.enqueue(
                MockResponse()
                        .setHeader("ContentType", "application/json")
                        .setHeadersDelay(2, TimeUnit.SECONDS)
                        .setBody("{ 'Temperature': $temperatureValue }")
                        .setBodyDelay(2, TimeUnit.SECONDS)
        )
    }
}