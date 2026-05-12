package com.example.planetlife.ui.focus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planetlife.data.local.entity.DailyStatsEntity
import com.example.planetlife.data.local.entity.FocusSessionEntity
import com.example.planetlife.data.local.entity.PlanetEventEntity
import com.example.planetlife.data.repository.CollectionRepository
import com.example.planetlife.data.repository.DailyStatsRepository
import com.example.planetlife.data.repository.FocusRepository
import com.example.planetlife.data.repository.PlanetEventRepository
import com.example.planetlife.data.repository.PlanetRepository
import com.example.planetlife.data.repository.TaskRepository
import com.example.planetlife.domain.rules.CollectionUnlocker
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
    val statusText: String = "水晶塔等待充能",
    val showFeedback: Boolean = false,
    val lastReward: Int = 0
)

class FocusViewModel(
    private val focusRepository: FocusRepository,
    private val planetRepository: PlanetRepository,
    private val dailyStatsRepository: DailyStatsRepository,
    private val eventRepository: PlanetEventRepository,
    private val taskRepository: TaskRepository,
    private val collectionRepository: CollectionRepository
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
        _uiState.update { it.copy(status = TimerStatus.RUNNING, statusText = "专注能量汇聚中...", showFeedback = false) }
        runTimer()
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(status = TimerStatus.PAUSED, statusText = "专注暂时中断") }
    }

    fun resumeTimer() {
        startTimer()
    }

    fun stopTimer() {
        val wasRunningOrPaused = _uiState.value.status == TimerStatus.RUNNING || _uiState.value.status == TimerStatus.PAUSED
        timerJob?.cancel()
        
        if (wasRunningOrPaused) {
            saveSession(completed = false)
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
                statusText = "充能完成！", 
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
                statusText = "水晶塔等待充能"
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

    private fun applyRewards() {
        viewModelScope.launch {
            val minutes = _uiState.value.selectedMinutes
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            
            // 1. Update Planet
            val planet = planetRepository.observePlanet().firstOrNull() ?: return@launch
            val updatedPlanet = planet.copy(
                crystalValue = (planet.crystalValue + minutes).coerceIn(0, 100),
                updatedAt = System.currentTimeMillis()
            )
            planetRepository.savePlanet(updatedPlanet)

            // 2. Update Daily Stats
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

            // 3. Update Task Progress
            taskRepository.updateTaskProgress("FOCUS", today, updatedStats.focusMinutes)

            // 4. Check Collection Unlocks
            collectionUnlocker.checkUnlocks(updatedPlanet, updatedStats)

            // 5. Generate Event
            eventRepository.saveEvent(PlanetEventEntity(
                date = today,
                eventType = "CRYSTAL_RESONANCE",
                title = "水晶共鸣",
                description = "水晶塔完成一次充能，星球核心传来清澈的回响。",
                relatedValue = "水晶",
                rarity = "普通"
            ))
        }
    }

    private fun formatTime(seconds: Int): String {
        val m = seconds / 60
        val s = seconds % 60
        return "%02d:%02d".format(m, s)
    }
}
