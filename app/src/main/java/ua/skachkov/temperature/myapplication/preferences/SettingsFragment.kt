package ua.skachkov.temperature.myapplication.preferences

import android.os.Bundle
import android.preference.PreferenceFragment
import ua.skachkov.temperature.myapplication.R

/**
 * @author Ivan Skachkov
 * Created on 3/20/2018.
 */
class SettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)
    }
}