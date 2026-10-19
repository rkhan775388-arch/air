package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.data.repository.AirNineRepository
import com.example.speech.VoiceManager
import com.example.ui.AirNineApp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NavyDarkBackground

class MainActivity : ComponentActivity() {

    private lateinit var repository: AirNineRepository
    private lateinit var voiceManager: VoiceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        repository = AirNineRepository(applicationContext)
        voiceManager = VoiceManager(applicationContext)

        setContent {
            MyApplicationTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = NavyDarkBackground
                ) {
                    AirNineApp(
                        repository = repository,
                        voiceManager = voiceManager
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::voiceManager.isInitialized) {
            voiceManager.destroy()
        }
    }
}
