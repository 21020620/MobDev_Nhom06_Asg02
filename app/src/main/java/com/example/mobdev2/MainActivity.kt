package com.example.mobdev2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import com.example.mobdev2.ui.screens.NavGraphs
import com.example.mobdev2.ui.theme.MobDev2Theme
import com.example.mobdev2.ui.theme.ThemeState
import com.ramcosta.composedestinations.DestinationsNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {

    MobDev2Theme(darkTheme = ThemeState.darkModeState) {
        DestinationsNavHost(
            navGraph = NavGraphs.root
        )
    }
}