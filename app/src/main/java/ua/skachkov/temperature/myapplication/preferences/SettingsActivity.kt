package ua.skachkov.temperature.myapplication.preferences

import android.app.Activity
import android.os.Bundle

/**
 * @author Ivan Skachkov
 * Created on 3/20/2018.
 */
class SettingsActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentManager.beginTransaction()
                .replace(android.R.id.content, SettingsFragment())
                .commit()
    }
}