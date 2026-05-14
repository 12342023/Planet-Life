package com.example.planetlife

import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.example.planetlife.data.repository.AppDataRepository
import com.example.planetlife.data.repository.CollectionRepository
import com.example.planetlife.data.repository.DailyEnergyRepository
import com.example.planetlife.data.repository.DailyStatsRepository
import com.example.planetlife.data.repository.FocusRepository
import com.example.planetlife.data.repository.MoodRepository
import com.example.planetlife.data.repository.PlanetEventRepository
import com.example.planetlife.data.repository.PlanetRepository
import com.example.planetlife.data.repository.TaskRepository
import com.example.planetlife.data.settings.SettingsDataStore
import com.example.planetlife.data.settings.StarVisitReward
import com.example.planetlife.domain.model.EnergyType
import com.example.planetlife.domain.model.PlanetLogType
import com.example.planetlife.navigation.AppNavGraph
import com.example.planetlife.navigation.AppRoute
import com.example.planetlife.notification.SedentaryReminderNotifier
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PlanetLifeApp(
    planetRepository: PlanetRepository,
    dailyStatsRepository: DailyStatsRepository,
    dailyEnergyRepository: DailyEnergyRepository,
    moodRepository: MoodRepository,
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
        if (planet != null) {
            recordDailyStarVisit(
                settingsDataStore = settingsDataStore,
                dailyEnergyRepository = dailyEnergyRepository,
                planetEventRepository = planetEventRepository,
            )
        }
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
            dailyEnergyRepository = dailyEnergyRepository,
            moodRepository = moodRepository,
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

private suspend fun recordDailyStarVisit(
    settingsDataStore: SettingsDataStore,
    dailyEnergyRepository: DailyEnergyRepository,
    planetEventRepository: PlanetEventRepository,
) {
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val reward = settingsDataStore.recordDailyVisit(today)
    if (!reward.rewarded || reward.total <= 0) {
        return
    }

    dailyEnergyRepository.addEnergy(today, EnergyType.STAR, reward.total)
    planetEventRepository.savePlanetLog(
        date = today,
        logType = PlanetLogType.ENERGY,
        title = reward.starTitle,
        description = reward.starDescription,
        relatedValue = "星辰",
        energyType = EnergyType.STAR,
    )
}

private val StarVisitReward.starTitle: String
    get() = if (bonus > 0) {
        "连续回来 $streak 天"
    } else {
        "星辰亮起"
    }

private val StarVisitReward.starDescription: String
    get() = if (bonus > 0) {
        "你又回到了星球，夜空亮起 $total 点星辰能量。"
    } else {
        "你回来了一下，星球的夜空就多了一点亮。"
    }
