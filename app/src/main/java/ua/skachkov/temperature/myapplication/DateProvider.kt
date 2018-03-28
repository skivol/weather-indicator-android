package ua.skachkov.temperature.myapplication

import java.text.DateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author Ivan Skachkov
 * Created on 3/12/2018.
 */
@Singleton
class DateProvider @Inject constructor() {
    fun currentDateFormatted() = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(currentDate())
    fun currentDate() = Calendar.getInstance().time
}