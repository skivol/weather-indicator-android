package ua.skachkov.temperature.myapplication.activity

import android.support.constraint.ConstraintSet.PARENT_ID
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import ua.skachkov.temperature.myapplication.data.WeatherData
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.ConstraintSetBuilder.Side.*
import org.jetbrains.anko.constraint.layout.applyConstraintSet
import org.jetbrains.anko.constraint.layout.constraintLayout
import javax.inject.Inject
import javax.inject.Singleton

const val MEASUREMENTS_MESSAGE_ID = 2
const val LOAD_INDICATOR_ID = 3
const val SYNC_DATE_ID = 4

@Singleton
class WeatherMeasurementsActivityUI @Inject constructor() : AnkoComponent<WeatherMeasurementsActivity> {
    private lateinit var ankoContext: AnkoContext<WeatherMeasurementsActivity>
    private lateinit var loadIndicator: ProgressBar
    private lateinit var temperatureOrMessage: TextView
    private lateinit var loadDate: TextView

    override fun createView(ui: AnkoContext<WeatherMeasurementsActivity>) = with(ui) {
        ankoContext = ui
        // https://developer.android.com/reference/android/support/constraint/ConstraintLayout.html
        constraintLayout {
            loadIndicator = progressBar {
                    id = LOAD_INDICATOR_ID
            } /* https://stackoverflow.com/a/13046535 */
            temperatureOrMessage = textView {
                id = MEASUREMENTS_MESSAGE_ID
                textSize = 24f
            }
            loadDate = textView {
                id = SYNC_DATE_ID
            }

            applyConstraintSet {
                loadDate {
                    connect(
                            TOP to TOP of PARENT_ID,
                            LEFT to LEFT of PARENT_ID,
                            RIGHT to RIGHT of PARENT_ID,
                            BOTTOM to BOTTOM of PARENT_ID
                    )
                    verticalBias = 0.3f
                }

                temperatureOrMessage {
                    connect(
                            TOP to TOP of PARENT_ID,
                            LEFT to LEFT of PARENT_ID,
                            RIGHT to RIGHT of PARENT_ID,
                            BOTTOM to BOTTOM of PARENT_ID
                    )
                    verticalBias = 0.2f
                }

                loadIndicator {
                    connect(
                            TOP to TOP of PARENT_ID,
                            LEFT to LEFT of PARENT_ID,
                            RIGHT to RIGHT of PARENT_ID,
                            BOTTOM to BOTTOM of PARENT_ID
                    )
                    verticalBias = 0.2f
                }
            }
        }
    }

    fun onMeasurementsStartedLoading() {
        ankoContext.doAsync {
            activityUiThread {
                loadIndicator.visibility = View.VISIBLE
            }
        }
    }

    fun onMeasurementsLoaded(weatherData: WeatherData) {
        ankoContext.doAsync {
            activityUiThread {
                temperatureOrMessage.text = weatherData.formattedWeatherMeasurementsOrStatusIfError
                loadDate.text = weatherData.syncDate

                loadIndicator.visibility = View.GONE
                temperatureOrMessage.visibility = View.VISIBLE
                loadDate.visibility = View.VISIBLE
            }
        }
    }
}