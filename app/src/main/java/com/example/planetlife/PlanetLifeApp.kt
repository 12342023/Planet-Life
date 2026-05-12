package com.example.planetlife

import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.example.planetlife.navigation.AppNavGraph
import com.example.planetlife.navigation.AppRoute
import com.example.planetlife.data.repository.PlanetRepository
import com.example.planetlife.data.repository.DailyStatsRepository
import com.example.planetlife.data.repository.PlanetEventRepository
import com.example.planetlife.data.repository.FocusRepository
import com.example.planetlife.data.repository.TaskRepository
import com.example.planetlife.data.repository.CollectionRepository
import com.example.planetlife.data.repository.AppDataRepository
import com.example.planetlife.data.settings.SettingsDataStore
import com.example.planetlife.notification.SedentaryReminderNotifier
import kotlinx.coroutines.flow.firstOrNull

@Composable
fun PlanetLifeApp(
    planetRepository: PlanetRepository,
    dailyStatsRepository: DailyStatsRepository,
    planetEventRepository: PlanetEventRepository,
    focusRepository: FocusRepository,
    taskRepository: TaskRepository,
    collectionRepository: CollectionRepository,
    appDataRepository: AppDataRepository,
    settingsDataStore: SettingsDataStore,
    sedentaryReminderNotifier: SedentaryReminderNotifier,
) {
    val navController = rememberNavController()
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val planet = planetRepository.observePlanet().firstOrNull()
        startDestination = if (planet == null) {
            AppRoute.Onboarding.route
        } else {
            AppRoute.Home.route
        }
    }

    startDestination?.let { destination ->
        AppNavGraph(
            navController = navController,
            startDestination = destination,
            planetRepository = planetRepository,
            dailyStatsRepository = dailyStatsRepository,
            planetEventRepository = planetEventRepository,
            focusRepository = focusRepository,
            taskRepository = taskRepository,
            collectionRepository = collectionRepository,
            appDataRepository = appDataRepository,
            settingsDataStore = settingsDataStore,
            sedentaryReminderNotifier = sedentaryReminderNotifier,
        )
    }
}
