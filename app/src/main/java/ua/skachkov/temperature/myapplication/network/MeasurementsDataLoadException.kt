package ua.skachkov.temperature.myapplication.network

/**
 * @author Ivan Skachkov
 * Created on 3/19/2018.
 */
class MeasurementsDataLoadException(override val message: String, cause: Throwable? = null) : RuntimeException(message, cause)