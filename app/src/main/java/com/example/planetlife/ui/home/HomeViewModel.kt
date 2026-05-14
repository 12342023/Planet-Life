package com.example.planetlife.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planetlife.data.local.entity.DailyStatsEntity
import com.example.planetlife.data.local.entity.PlanetEntity
import com.example.planetlife.data.local.entity.PlanetEventEntity
import com.example.planetlife.data.repository.CollectionRepository
import com.example.planetlife.data.repository.DailyStatsRepository
import com.example.planetlife.data.repository.PlanetEventRepository
import com.example.planetlife.data.repository.PlanetRepository
import com.example.planetlife.data.repository.TaskRepository
import com.example.planetlife.data.settings.SettingsDataStore
import com.example.planetlife.data.settings.UserSettings
import com.example.planetlife.domain.rules.CollectionUnlocker
import com.example.planetlife.domain.rules.EcologyRuleEngine
import com.example.planetlife.domain.rules.EventGenerator
import com.example.planetlife.domain.rules.PlanetStateCalculator
import com.example.planetlife.notification.SedentaryReminderNotifier
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

data class HomeUiState(
    val appName: String = "星球宠物",
    val planet: PlanetEntity? = null,
    val todayStats: DailyStatsEntity? = null,
    val todayEvents: List<PlanetEventEntity> = emptyList(),
    val settings: UserSettings = UserSettings(),
    val planetStatus: String = "生态稳定",
    val isLoading: Boolean = true
)

class HomeViewModel(
    private val planetRepository: PlanetRepository,
    private val dailyStatsRepository: DailyStatsRepository,
    private val eventRepository: PlanetEventRepository,
    private val taskRepository: TaskRepository,
    private val collectionRepository: CollectionRepository,
    private val settingsDataStore: SettingsDataStore,
    private val sedentaryReminderNotifier: SedentaryReminderNotifier,
) : ViewModel() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val today: String = dateFormat.format(Date())
    private val collectionUnlocker = CollectionUnlocker(collectionRepository, eventRepository)

    val uiState: StateFlow<HomeUiState> = combine(
        planetRepository.observePlanet(),
        settingsDataStore.settings,
        dailyStatsRepository.observeStatsByDate(today),
        eventRepository.observeTodayEvents(today)
    ) { planet, settings, todayStats, todayEvents ->
        HomeUiState(
            planet = planet,
            todayStats = todayStats,
            todayEvents = todayEvents,
            settings = settings,
            planetStatus = planet?.let { PlanetStateCalculator.calculate(it) } ?: "生态稳定",
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    fun recordBehavior(
        walking: Int = 0,
        sedentary: Int = 0,
        nightActive: Int = 0,
        commute: Int = 0,
        onComplete: (String) -> Unit = {},
    ) {
        viewModelScope.launch {
            val currentPlanet = uiState.value.planet
            if (currentPlanet == null) {
                onComplete("请先创建星球，再记录行为")
                return@launch
            }
            val safeWalking = walking.coerceAtLeast(0)
            val safeSedentary = sedentary.coerceAtLeast(0)
            val safeNightActive = nightActive.coerceAtLeast(0)
            val safeCommute = commute.coerceAtLeast(0)
            if (safeWalking + safeSedentary + safeNightActive + safeCommute == 0) {
                onComplete("请输入至少一项行为分钟数")
                return@launch
            }

            // 1. Update Planet
            val updatedPlanet = EcologyRuleEngine.calculateNewValues(
                currentPlanet = currentPlanet,
                walkingMinutes = safeWalking,
                sedentaryMinutes = safeSedentary,
                nightActiveMinutes = safeNightActive,
                commuteMinutes = safeCommute,
            )
            planetRepository.savePlanet(updatedPlanet)

            // 2. Update Daily Stats
            val existingStats = dailyStatsRepository.getStatsByDate(today) ?: DailyStatsEntity(date = today)
            val balanceScore = updatedPlanet.forestValue +
                updatedPlanet.crystalValue +
                updatedPlanet.dreamValue -
                updatedPlanet.desertValue -
                updatedPlanet.shadowValue
            val updatedStats = existingStats.copy(
                walkingMinutes = existingStats.walkingMinutes + safeWalking,
                sedentaryMinutes = existingStats.sedentaryMinutes + safeSedentary,
                nightActiveMinutes = existingStats.nightActiveMinutes + safeNightActive,
                commuteMinutes = existingStats.commuteMinutes + safeCommute,
                balanceScore = balanceScore,
                updatedAt = System.currentTimeMillis()
            )
            dailyStatsRepository.saveStats(updatedStats)

            val settings = uiState.value.settings
            val crossedSedentaryLimit = existingStats.sedentaryMinutes < settings.sedentaryReminderMinutes &&
                updatedStats.sedentaryMinutes >= settings.sedentaryReminderMinutes
            if (settings.notificationEnabled && safeSedentary > 0 && crossedSedentaryLimit) {
                sedentaryReminderNotifier.showSedentaryReminder()
            }

            // 3. Update Task Progress
            taskRepository.updateTaskProgress("WALKING", today, updatedStats.walkingMinutes)
            taskRepository.updateTaskProgress("SEDENTARY_LIMIT", today, updatedStats.sedentaryMinutes)
            taskRepository.updateTaskProgress("NIGHT_ACTIVE_LIMIT", today, updatedStats.nightActiveMinutes)

            // 4. Check Collection Unlocks
            collectionUnlocker.checkUnlocks(updatedPlanet, updatedStats)

            // 5. Generate and Save Events
            val newEvents = EventGenerator.generateEventsFromBehavior(
                walking = safeWalking,
                sedentary = safeSedentary,
                nightActive = safeNightActive,
                commute = safeCommute
            )
            newEvents.forEach { eventRepository.saveEvent(it) }

            onComplete(if (newEvents.isNotEmpty()) "同步成功，星球发生了变化" else "星球生态已同步")
        }
    }

    fun randomizeEcologyForDemo(onComplete: (String) -> Unit = {}) {
        viewModelScope.launch {
            val currentPlanet = uiState.value.planet
            if (currentPlanet == null) {
                onComplete("请先创建星球，再进行生态演示")
                return@launch
            }

            val randomizedPlanet = currentPlanet.copy(
                forestValue = Random.nextInt(20, 96),
                crystalValue = Random.nextInt(20, 96),
                dreamValue = Random.nextInt(20, 96),
                cityValue = Random.nextInt(10, 86),
                desertValue = Random.nextInt(0, 76),
                shadowValue = Random.nextInt(0, 76),
                updatedAt = System.currentTimeMillis(),
            )
            planetRepository.savePlanet(randomizedPlanet)

            val existingStats = dailyStatsRepository.getStatsByDate(today) ?: DailyStatsEntity(date = today)
            val balanceScore = randomizedPlanet.forestValue +
                randomizedPlanet.crystalValue +
                randomizedPlanet.dreamValue -
                randomizedPlanet.desertValue -
                randomizedPlanet.shadowValue
            dailyStatsRepository.saveStats(
                existingStats.copy(
                    balanceScore = balanceScore,
                    updatedAt = System.currentTimeMillis(),
                )
            )

            eventRepository.saveEvent(
                PlanetEventEntity(
                    date = today,
                    eventType = "DEMO_ECOLOGY_RANDOMIZED",
                    title = "演示星潮掠过",
                    description = "一阵演示用的星潮掠过地表，森林、水晶、梦境和荒漠都出现了新的形态。",
                    relatedValue = "生态演示",
                    rarity = "普通",
                )
            )
            collectionUnlocker.checkUnlocks(randomizedPlanet, existingStats)

            onComplete("已随机生成星球生态值")
        }
    }
}
