package com.example.planetlife

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.planetlife.data.settings.UserSettings
import com.example.planetlife.ui.theme.PlanetLifeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val app = application as PlanetLifeApplication
        
        setContent {
            val settings by app.settingsDataStore.settings.collectAsState(initial = UserSettings())
            PlanetLifeTheme(themeMode = settings.themeMode) {
                PlanetLifeApp(
                    planetRepository = app.planetRepository,
                    dailyStatsRepository = app.dailyStatsRepository,
                    dailyEnergyRepository = app.dailyEnergyRepository,
                    moodRepository = app.moodRepository,
                    planetEventRepository = app.planetEventRepository,
                    focusRepository = app.focusRepository,
                    taskRepository = app.taskRepository,
                    collectionRepository = app.collectionRepository,
                    appDataRepository = app.appDataRepository,
                    settingsDataStore = app.settingsDataStore,
                    sedentaryReminderNotifier = app.sedentaryReminderNotifier,
                )
            }
        }
    }
}
