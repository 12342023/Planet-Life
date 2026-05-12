package com.example.planetlife.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planetlife.data.local.entity.PlanetEntity
import com.example.planetlife.data.repository.AppDataRepository
import com.example.planetlife.data.repository.CollectionRepository
import com.example.planetlife.data.repository.DailyStatsRepository
import com.example.planetlife.data.repository.PlanetRepository
import com.example.planetlife.data.settings.SettingsDataStore
import com.example.planetlife.data.settings.UserSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProfileUiState(
    val planet: PlanetEntity? = null,
    val settings: UserSettings = UserSettings(),
    val totalFocusMinutes: Int = 0,
    val totalWalkingMinutes: Int = 0,
    val collectionUnlockedCount: Int = 0,
    val collectionTotalCount: Int = 0,
    val isLoading: Boolean = true
)

class ProfileViewModel(
    private val planetRepository: PlanetRepository,
    private val dailyStatsRepository: DailyStatsRepository,
    private val collectionRepository: CollectionRepository,
    private val settingsDataStore: SettingsDataStore,
    private val appDataRepository: AppDataRepository
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = combine(
        planetRepository.observePlanet(),
        settingsDataStore.settings,
        dailyStatsRepository.observeAllStats(),
        collectionRepository.getAllCreatures()
    ) { planet, settings, allStats, allCreatures ->
        ProfileUiState(
            planet = planet,
            settings = settings,
            totalFocusMinutes = allStats.sumOf { it.focusMinutes },
            totalWalkingMinutes = allStats.sumOf { it.walkingMinutes },
            collectionUnlockedCount = allCreatures.count { it.isUnlocked },
            collectionTotalCount = allCreatures.size,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState()
    )

    fun updateNickname(nickname: String) {
        viewModelScope.launch {
            settingsDataStore.updateNickname(nickname)
        }
    }

    fun updatePlanetName(name: String) {
        viewModelScope.launch {
            val currentPlanet = uiState.value.planet ?: return@launch
            if (name.isNotBlank()) {
                planetRepository.savePlanet(currentPlanet.copy(name = name, updatedAt = System.currentTimeMillis()))
            }
        }
    }

    fun updateThemeMode(mode: String) {
        viewModelScope.launch {
            settingsDataStore.updateThemeMode(mode)
        }
    }

    fun updateWalkingGoal(goal: Int) {
        viewModelScope.launch {
            settingsDataStore.updateDailyWalkingGoal(goal)
        }
    }

    fun updateFocusGoal(goal: Int) {
        viewModelScope.launch {
            settingsDataStore.updateDailyFocusGoal(goal)
        }
    }

    fun updateSedentaryReminder(minutes: Int) {
        viewModelScope.launch {
            settingsDataStore.updateSedentaryReminderMinutes(minutes)
        }
    }

    fun updateNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.updateNotificationEnabled(enabled)
        }
    }

    fun clearAllData(onComplete: () -> Unit) {
        viewModelScope.launch {
            appDataRepository.clearAllData()
            onComplete()
        }
    }
}
