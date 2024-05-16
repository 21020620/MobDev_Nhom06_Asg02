package com.example.mobdev2

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.mobdev2.ui.screens.NavGraphs
import com.example.mobdev2.ui.screens.book.UserPreferences
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
    val settingDataStore: UserPreferences
    val localContext = LocalContext.current
    settingDataStore = UserPreferences(localContext)
    val courotineScope = rememberCoroutineScope()
    // Collect the theme from settingDataStore as a state
    val darkThemeState = settingDataStore.themeFlow.collectAsState(initial = false)
    MobDev2Theme(darkTheme = darkThemeState) {
        DestinationsNavHost(
            navGraph = NavGraphs.root
        )
    }
}