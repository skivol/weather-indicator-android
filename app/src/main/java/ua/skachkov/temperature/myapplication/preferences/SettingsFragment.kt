package ua.skachkov.temperature.myapplication.preferences

import android.os.Bundle
import android.preference.EditTextPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import org.jetbrains.anko.alert
import ua.skachkov.temperature.myapplication.R

/**
 * @author Ivan Skachkov
 * Created on 3/20/2018.
 */
class SettingsFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)

        val loadingPeriodPreference = preferenceScreen.findPreference(getString(R.string.pref_measurements_loading_period_in_seconds_key)) as EditTextPreference
        loadingPeriodPreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            val newValueIsValid = Integer.valueOf(newValue.toString()) >= 5
            if (!newValueIsValid) {
                alert {
                    messageResource = R.string.pref_measurements_update_period_error
                    positiveButton("Ok", {})
                }.show()
            }
            newValueIsValid
        }
    }
}