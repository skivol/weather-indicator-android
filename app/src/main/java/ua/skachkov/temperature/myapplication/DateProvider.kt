package ua.skachkov.temperature.myapplication

import java.text.DateFormat
import java.util.*
import javax.inject.Inject

/**
 * @author Ivan Skachkov
 * Created on 3/12/2018.
 */
class DateProvider @Inject constructor() {
    fun currentDateFormatted() = DateFormat.getDateTimeInstance().format(currentDate())
    fun currentDate() = Calendar.getInstance().time
}