package ua.skachkov.temperature.myapplication.preferences

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import ua.skachkov.temperature.myapplication.R
import org.jetbrains.anko.applyRecursively
import org.jetbrains.anko.padding
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout

const val kotlinInfoTextViewId = 100

/**
 * @author Ivan Skachkov
 * Created on 3/20/2018.
 */
class AboutActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO create "Inspiring projects" to list useful examples
        // TODO create "Limitations" to list some of the limitations of current project
        // TODO create "Helpful resources"
        super.onCreate(savedInstanceState)
        verticalLayout {
            padding = 15
            textView {
                text = getString(R.string.about_technologies_used)
            }
            textView {
                id = kotlinInfoTextViewId
                text = "1. Kotlin (https://kotlinlang.org)"
            }
            textView {
                text = "2. Anko (https://github.com/Kotlin/anko)"
            }
            textView {
                text = "3. Dagger 2 (https://google.github.io/dagger/android)"
            }
            textView {
                text = "4. OkHttp (https://github.com/square/okhttp)"
            }
            textView {
                text = "5. Espresso, Mockito, MockWebServer"
            }
        }.applyRecursively {
            when(it) {
                is TextView -> it.textSize = 24f
            }
        }
    }
}