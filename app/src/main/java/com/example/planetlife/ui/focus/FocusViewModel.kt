package com.example.planetlife.ui.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planetlife.data.local.entity.DailyStatsEntity
import com.example.planetlife.data.local.entity.FocusSessionEntity
import com.example.planetlife.data.repository.CollectionRepository
import com.example.planetlife.data.repository.DailyEnergyRepository
import com.example.planetlife.data.repository.DailyStatsRepository
import com.example.planetlife.data.repository.FocusRepository
import com.example.planetlife.data.repository.PlanetEventRepository
import com.example.planetlife.data.repository.PlanetRepository
import com.example.planetlife.data.repository.TaskRepository
import com.example.planetlife.domain.model.EnergyType
import com.example.planetlife.domain.model.PlanetLogType
import com.example.planetlife.domain.rules.CollectionUnlocker
import com.example.planetlife.domain.text.LocalPlanetTextGenerator
import com.example.planetlife.domain.text.PlanetTextGenerator
import com.example.planetlife.domain.text.TextGenerationRequest
import com.example.planetlife.domain.text.TextGenerationType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class TimerStatus {
    IDLE, RUNNING, PAUSED, FINISHED
}

data class FocusUiState(
    val selectedMinutes: Int = 25,
    val remainingSeconds: Int = 25 * 60,
    val status: TimerStatus = TimerStatus.IDLE,
    val displayTime: String = "25:00",
    val statusText: String = "星核正在安静等待",
    val showFeedback: Boolean = false,
    val lastReward: Int = 0
)

class FocusViewModel(
    private val focusRepository: FocusRepository,
    private val planetRepository: PlanetRepository,
    private val dailyStatsRepository: DailyStatsRepository,
    private val dailyEnergyRepository: DailyEnergyRepository,
    private val eventRepository: PlanetEventRepository,
    private val taskRepository: TaskRepository,
    private val collectionRepository: CollectionRepository,
    private val textGenerator: PlanetTextGenerator = LocalPlanetTextGenerator()
) : ViewModel() {

    private val _uiState = MutableStateFlow(FocusUiState())
    val uiState: StateFlow<FocusUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var startTime: Long = 0
    private val collectionUnlocker = CollectionUnlocker(collectionRepository, eventRepository)

    fun selectMinutes(minutes: Int) {
        if (_uiState.value.status == TimerStatus.IDLE) {
            _uiState.update {
                it.copy(
                    selectedMinutes = minutes,
                    remainingSeconds = minutes * 60,
                    displayTime = formatTime(minutes * 60)
                )
            }
        }
    }

    fun startTimer() {
        if (_uiState.value.status == TimerStatus.IDLE) {
            startTime = System.currentTimeMillis()
        }
        _uiState.update { it.copy(status = TimerStatus.RUNNING, statusText = "星核光流慢慢聚拢中...", showFeedback = false) }
        runTimer()
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(status = TimerStatus.PAUSED, statusText = "光流暂时停在这里") }
    }

    fun resumeTimer() {
        startTimer()
    }

    fun stopTimer() {
        val status = _uiState.value.status
        val wasRunningOrPaused = status == TimerStatus.RUNNING || status == TimerStatus.PAUSED
        timerJob?.cancel()
        
        if (wasRunningOrPaused) {
            saveSession(completed = false)
            logEarlyStop()
        }
        
        resetTimer()
    }

    private fun runTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.remainingSeconds > 0) {
                delay(1000)
                _uiState.update {
                    val newSeconds = it.remainingSeconds - 1
                    it.copy(
                        remainingSeconds = newSeconds,
                        displayTime = formatTime(newSeconds)
                    )
                }
            }
            onTimerFinished()
        }
    }

    private fun onTimerFinished() {
        _uiState.update { 
            it.copy(
                status = TimerStatus.FINISHED, 
                statusText = "星核慢慢亮了起来",
                showFeedback = true,
                lastReward = it.selectedMinutes 
            ) 
        }
        saveSession(completed = true)
        applyRewards()
    }

    private fun resetTimer() {
        _uiState.update {
            it.copy(
                status = TimerStatus.IDLE,
                remainingSeconds = it.selectedMinutes * 60,
                displayTime = formatTime(it.selectedMinutes * 60),
                statusText = "星核正在安静等待"
            )
        }
    }

    private fun saveSession(completed: Boolean) {
        viewModelScope.launch {
            val session = FocusSessionEntity(
                startTime = startTime,
                endTime = System.currentTimeMillis(),
                durationMinutes = _uiState.value.selectedMinutes,
                completed = completed,
                rewardCrystal = if (completed) _uiState.value.selectedMinutes else 0
            )
            focusRepository.saveSession(session)
        }
    }

    private fun logEarlyStop() {
        viewModelScope.launch {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            eventRepository.savePlanetLog(
                date = today,
                logType = PlanetLogType.ENERGY,
                title = "星核小憩",
                description = "这一小段安静也被星球收下了，星核的光没有熄灭，只是变得更轻。",
                energyType = EnergyType.CORE
            )
        }
    }

    private fun applyRewards() {
        viewModelScope.launch {
            val minutes = _uiState.value.selectedMinutes
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            val planet = planetRepository.observePlanet().firstOrNull() ?: return@launch
            val result = textGenerator.generate(
                TextGenerationRequest(
                    type = TextGenerationType.ENERGY_FEEDBACK,
                    planetName = planet.name,
                    energyType = EnergyType.CORE,
                )
            )

            // 1. Update Core Energy
            dailyEnergyRepository.addEnergy(today, EnergyType.CORE)

            // 2. Update Planet
            val updatedPlanet = planet.copy(
                crystalValue = (planet.crystalValue + minutes).coerceIn(0, 100),
                updatedAt = System.currentTimeMillis()
            )
            planetRepository.savePlanet(updatedPlanet)

            // 3. Update Daily Stats
            val stats = dailyStatsRepository.getStatsByDate(today) ?: DailyStatsEntity(date = today)
            val balanceScore = updatedPlanet.forestValue +
                updatedPlanet.crystalValue +
                updatedPlanet.dreamValue -
                updatedPlanet.desertValue -
                updatedPlanet.shadowValue
            val updatedStats = stats.copy(
                focusMinutes = stats.focusMinutes + minutes,
                balanceScore = balanceScore,
                updatedAt = System.currentTimeMillis()
            )
            dailyStatsRepository.saveStats(updatedStats)

            // 4. Update Task Progress
            taskRepository.updateTaskProgress("FOCUS", today, updatedStats.focusMinutes)

            // 5. Check Collection Unlocks
            collectionUnlocker.checkUnlocks(updatedPlanet, updatedStats)

            // 6. Generate Planet Log
            eventRepository.savePlanetLog(
                date = today,
                logType = PlanetLogType.ENERGY,
                title = result.title,
                description = result.body,
                relatedValue = "星核",
                energyType = EnergyType.CORE
            )
        }
    }

    private fun formatTime(seconds: Int): String {
        val m = seconds / 60
        val s = seconds % 60
        return "%02d:%02d".format(m, s)
    }
}
