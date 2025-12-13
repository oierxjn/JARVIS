package org.oierxjn.jarvis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.oierxjn.jarvis.ui.MainHome
import org.oierxjn.jarvis.ui.theme.JARVISTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JARVISTheme {
                MainHome()
            }
        }
    }
}


