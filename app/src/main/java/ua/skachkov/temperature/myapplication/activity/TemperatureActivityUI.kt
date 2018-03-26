package ua.skachkov.temperature.myapplication.activity

import android.support.constraint.ConstraintSet.PARENT_ID
import android.support.v7.appcompat.R
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import ua.skachkov.temperature.myapplication.data.TemperatureData
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.ConstraintSetBuilder.Side.*
import org.jetbrains.anko.constraint.layout.applyConstraintSet
import org.jetbrains.anko.constraint.layout.constraintLayout
import javax.inject.Inject

const val TEMPERATURE_MESSAGE_ID = 2
const val LOAD_INDICATOR_ID = 3
const val SYNC_DATE_ID = 4

class TemperatureActivityUI @Inject constructor() : AnkoComponent<TemperatureActivity> {
    private lateinit var ankoContext: AnkoContext<TemperatureActivity>
    private lateinit var loadIndicator: ProgressBar
    private lateinit var temperatureOrMessage: TextView
    private lateinit var loadDate: TextView

    override fun createView(ui: AnkoContext<TemperatureActivity>) = with(ui) {
        ankoContext = ui
        // https://developer.android.com/reference/android/support/constraint/ConstraintLayout.html
        constraintLayout {
            loadIndicator = progressBar {
                    id = LOAD_INDICATOR_ID
            } /* https://stackoverflow.com/a/13046535 */
            temperatureOrMessage = textView {
                id = TEMPERATURE_MESSAGE_ID
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

    fun onTemperatureStartedLoading() {
        ankoContext.doAsync {
            activityUiThread {
                loadIndicator.visibility = View.VISIBLE
            }
        }
    }

    fun onTemperatureLoaded(temperatureData: TemperatureData) {
        ankoContext.doAsync {
            activityUiThread {
                temperatureOrMessage.text = temperatureData.temperatureOrStatusIfError
                loadDate.text = temperatureData.syncDate

                loadIndicator.visibility = View.GONE
                temperatureOrMessage.visibility = View.VISIBLE
                loadDate.visibility = View.VISIBLE
            }
        }
    }
}