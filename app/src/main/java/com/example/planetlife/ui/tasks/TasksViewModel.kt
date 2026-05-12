package com.example.planetlife.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planetlife.data.local.entity.PlanetEventEntity
import com.example.planetlife.data.local.entity.PlanetTaskEntity
import com.example.planetlife.data.repository.CollectionRepository
import com.example.planetlife.data.repository.DailyStatsRepository
import com.example.planetlife.data.repository.PlanetEventRepository
import com.example.planetlife.data.repository.PlanetRepository
import com.example.planetlife.data.repository.TaskRepository
import com.example.planetlife.domain.rules.CollectionUnlocker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TasksUiState(
    val tasks: List<PlanetTaskEntity> = emptyList(),
    val isLoading: Boolean = true
)

class TasksViewModel(
    private val taskRepository: TaskRepository,
    private val planetRepository: PlanetRepository,
    private val dailyStatsRepository: DailyStatsRepository,
    private val eventRepository: PlanetEventRepository,
    private val collectionRepository: CollectionRepository
) : ViewModel() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val today: String = dateFormat.format(Date())
    private val collectionUnlocker = CollectionUnlocker(collectionRepository, eventRepository)

    val uiState: StateFlow<TasksUiState> = taskRepository.getTasksByDate(today)
        .map { tasks ->
            TasksUiState(tasks = tasks, isLoading = false)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TasksUiState()
        )

    init {
        ensureTodayTasks()
    }

    private fun ensureTodayTasks() {
        viewModelScope.launch {
            taskRepository.ensureTodayTasks(today)
        }
    }

    fun claimReward(task: PlanetTaskEntity) {
        viewModelScope.launch {
            val updatedTask = taskRepository.claimReward(task.id) ?: return@launch
            
            val planet = planetRepository.getPlanet() ?: return@launch
            val newPlanet = when (updatedTask.rewardType) {
                "FOREST" -> planet.copy(forestValue = (planet.forestValue + updatedTask.rewardValue).coerceIn(0, 100))
                "CRYSTAL" -> planet.copy(crystalValue = (planet.crystalValue + updatedTask.rewardValue).coerceIn(0, 100))
                "DESERT_REDUCE" -> planet.copy(desertValue = (planet.desertValue - updatedTask.rewardValue).coerceIn(0, 100))
                "DREAM" -> planet.copy(dreamValue = (planet.dreamValue + updatedTask.rewardValue).coerceIn(0, 100))
                else -> planet
            }
            val finalPlanet = newPlanet.copy(updatedAt = System.currentTimeMillis())
            planetRepository.savePlanet(finalPlanet)

            // Generate Log
            val rewardName = when (updatedTask.rewardType) {
                "FOREST" -> "森林"
                "CRYSTAL" -> "水晶"
                "DESERT_REDUCE" -> "荒漠"
                "DREAM" -> "梦境"
                else -> "未知"
            }
            
            eventRepository.saveEvent(
                PlanetEventEntity(
                    date = today,
                    eventType = "TASK_REWARD",
                    title = "任务奖励已送达",
                    description = "星球收到了你的行动回响，生态能量正在重新分配。",
                    relatedValue = rewardName,
                    rarity = "普通"
                )
            )

            // Check Collection Unlocks
            val todayStats = dailyStatsRepository.getStatsByDate(today)
            collectionUnlocker.checkUnlocks(finalPlanet, todayStats)
        }
    }
}
