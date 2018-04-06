package ua.skachkov.temperature.myapplication.activity

import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintLayout.LayoutParams.PARENT_ID
import android.support.constraint.Guideline
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.ConstraintSetBuilder
import org.jetbrains.anko.constraint.layout.ConstraintSetBuilder.Side.TOP
import org.jetbrains.anko.constraint.layout.applyConstraintSet
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.constraint.layout.guideline
import ua.skachkov.temperature.myapplication.R
import ua.skachkov.temperature.myapplication.data.WeatherData
import javax.inject.Inject
import javax.inject.Singleton

const val TEMPERATURE_ICON_ID = 10
const val TEMPERATURE_MESSAGE_ID = 15
const val HUMIDITY_ICON_ID = 20
const val HUMIDITY_MESSAGE_ID = 25
const val ERROR_MESSAGE_ID = 30
const val MESSAGES_GUIDELINE_ID = 50
const val LOAD_INDICATOR_ID = 3
const val SYNC_DATE_ID = 4

const val DEFAULT_TEXT_SIZE = 36f
const val DEFAULT_ICON_SIZE = 100
const val DEFAULT_MARGIN_VALUE = 10

@Singleton
class WeatherMeasurementsActivityUI @Inject constructor() : AnkoComponent<WeatherMeasurementsActivity> {
    private lateinit var ankoContext: AnkoContext<WeatherMeasurementsActivity>

    private lateinit var messagesGuideline: Guideline
    private lateinit var temperatureIcon: ImageView
    private lateinit var temperatureMessage: TextView
    private lateinit var humidityIcon: ImageView
    private lateinit var humidityMessage: TextView
    private lateinit var errorMessage: TextView
    private lateinit var loadIndicator: ProgressBar
    private lateinit var loadDate: TextView

    override fun createView(ui: AnkoContext<WeatherMeasurementsActivity>) = with(ui) {
        ankoContext = ui
        // https://developer.android.com/reference/android/support/constraint/ConstraintLayout.html
        constraintLayout {
            loadIndicator = progressBar {
                id = LOAD_INDICATOR_ID
            }

            messagesGuideline = guideline {
                id = MESSAGES_GUIDELINE_ID
            }.lparams {
                orientation = ConstraintLayout.LayoutParams.HORIZONTAL
                guidePercent = 0.2f
            }

            temperatureIcon = imageView(R.drawable.thermometer_black_96px) {
                id = TEMPERATURE_ICON_ID
            }.lparams(0, DEFAULT_ICON_SIZE)
            temperatureMessage = textView {
                id = TEMPERATURE_MESSAGE_ID
                textSize = DEFAULT_TEXT_SIZE
            }.lparams(0, wrapContent)

            humidityIcon = imageView(R.drawable.water_percent_black_96px) {
                id = HUMIDITY_ICON_ID
            }.lparams(0, DEFAULT_ICON_SIZE)
            humidityMessage = textView {
                id = HUMIDITY_MESSAGE_ID
                textSize = DEFAULT_TEXT_SIZE
            }.lparams(0, wrapContent)

            errorMessage = textView {
                id = ERROR_MESSAGE_ID
                textSize = DEFAULT_TEXT_SIZE
            }
            loadDate = textView {
                id = SYNC_DATE_ID
            }

            applyConstraintSet {
                temperatureIcon {
                    // TODO Chaining doesn't seem to work defined manually here...
//                    horizontalChainStyle = CHAIN_PACKED
//                    horizontalBias = 0.5f
                    connect(
                            TOP to TOP of messagesGuideline,
                            ConstraintSetBuilder.Side.START to ConstraintSetBuilder.Side.START of PARENT_ID margin dip(DEFAULT_MARGIN_VALUE),
                            ConstraintSetBuilder.Side.END to ConstraintSetBuilder.Side.END of PARENT_ID,
                            ConstraintSetBuilder.Side.END to ConstraintSetBuilder.Side.START of temperatureMessage
                    )
                }
                temperatureMessage {
                    //                    horizontalBias = 0.5f
                    connect(
                            TOP to TOP of temperatureIcon,
                            ConstraintSetBuilder.Side.START to ConstraintSetBuilder.Side.END of temperatureIcon,
                            ConstraintSetBuilder.Side.END to ConstraintSetBuilder.Side.START of humidityIcon
                    )
                }
                humidityIcon {
                    //                    horizontalBias = 0.5f
                    connect(
                            TOP to TOP of temperatureMessage,
                            ConstraintSetBuilder.Side.START to ConstraintSetBuilder.Side.END of temperatureMessage,
                            ConstraintSetBuilder.Side.END to ConstraintSetBuilder.Side.START of humidityMessage
                    )
                }
                humidityMessage {
                    //                    horizontalBias = 0.5f
                    connect(
                            TOP to TOP of humidityIcon,
                            ConstraintSetBuilder.Side.START to ConstraintSetBuilder.Side.END of humidityIcon,
                            ConstraintSetBuilder.Side.END to ConstraintSetBuilder.Side.END of PARENT_ID margin dip(DEFAULT_MARGIN_VALUE)
                    )
                }

                loadDate {
                    connect(
                            TOP to TOP of PARENT_ID,
                            ConstraintSetBuilder.Side.START to ConstraintSetBuilder.Side.START of PARENT_ID,
                            ConstraintSetBuilder.Side.END to ConstraintSetBuilder.Side.END of PARENT_ID,
                            ConstraintSetBuilder.Side.BOTTOM to ConstraintSetBuilder.Side.BOTTOM of PARENT_ID
                    )
                    verticalBias = 0.4f
                }

                errorMessage {
                    connect(
                            TOP to TOP of messagesGuideline,
                            ConstraintSetBuilder.Side.START to ConstraintSetBuilder.Side.START of PARENT_ID,
                            ConstraintSetBuilder.Side.END to ConstraintSetBuilder.Side.END of PARENT_ID
                    )
                }

                loadIndicator {
                    connect(
                            TOP to TOP of messagesGuideline,
                            ConstraintSetBuilder.Side.START to ConstraintSetBuilder.Side.START of PARENT_ID,
                            ConstraintSetBuilder.Side.END to ConstraintSetBuilder.Side.END of PARENT_ID
                    )
                }
            }
        }
    }

    fun onMeasurementsStartedLoading() {
        ankoContext.owner.runOnUiThread {
            loadIndicator.visibility = VISIBLE
        }
    }

    fun onMeasurementsLoaded(weatherData: WeatherData) {
        ankoContext.owner.runOnUiThread {
            // Measurements
            if (weatherData.success) {
                errorMessage.visibility = GONE

                temperatureMessage.text = weatherData.formattedTemperature
                temperatureMessage.visibility = VISIBLE
                temperatureIcon.visibility = VISIBLE

                humidityMessage.text = weatherData.humidity
                humidityMessage.visibility = VISIBLE
                humidityIcon.visibility = VISIBLE
            } else {
                errorMessage.text = weatherData.statusMessage
                errorMessage.visibility = VISIBLE

                temperatureMessage.visibility = GONE
                temperatureIcon.visibility = GONE
                humidityMessage.visibility = GONE
                humidityIcon.visibility = GONE
            }

            // Sync date
            loadDate.text = weatherData.syncDate
            loadDate.visibility = VISIBLE
            loadIndicator.visibility = GONE
        }
    }
}