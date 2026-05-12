package com.example.planetlife.data.repository

import com.example.planetlife.data.local.dao.PlanetTaskDao
import com.example.planetlife.data.local.entity.PlanetTaskEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class TaskRepository(private val planetTaskDao: PlanetTaskDao) {

    fun getTasksByDate(date: String): Flow<List<PlanetTaskEntity>> {
        return planetTaskDao.getTasksByDate(date)
    }

    suspend fun ensureTodayTasks(date: String) {
        val existingTasks = planetTaskDao.getTasksByDateSync(date)
        if (existingTasks.isEmpty()) {
            val defaultTasks = listOf(
                PlanetTaskEntity(
                    date = date,
                    title = "让南部森林下雨",
                    description = "今日步行 30 分钟，唤醒森林种子",
                    taskType = "WALKING",
                    targetValue = 30,
                    currentValue = 0,
                    rewardType = "FOREST",
                    rewardValue = 8
                ),
                PlanetTaskEntity(
                    date = date,
                    title = "点亮水晶塔",
                    description = "完成 60 分钟专注，为水晶塔充能",
                    taskType = "FOCUS",
                    targetValue = 60,
                    currentValue = 0,
                    rewardType = "CRYSTAL",
                    rewardValue = 10
                ),
                PlanetTaskEntity(
                    date = date,
                    title = "阻止沙漠扩张",
                    description = "今日久坐控制在 120 分钟以内",
                    taskType = "SEDENTARY_LIMIT",
                    targetValue = 120,
                    currentValue = 0,
                    rewardType = "DESERT_REDUCE",
                    rewardValue = 8
                ),
                PlanetTaskEntity(
                    date = date,
                    title = "召回睡眠水母",
                    description = "减少夜间活跃，让梦境海洋恢复平静",
                    taskType = "NIGHT_ACTIVE_LIMIT",
                    targetValue = 60,
                    currentValue = 0,
                    rewardType = "DREAM",
                    rewardValue = 8
                )
            )
            planetTaskDao.insertTasks(defaultTasks)
        }
    }

    suspend fun updateTaskProgress(taskType: String, date: String, value: Int) {
        ensureTodayTasks(date)
        val tasks = planetTaskDao.getTasksByDateSync(date)
        val task = tasks.find { it.taskType == taskType } ?: return
        
        val completed = when (taskType) {
            "WALKING", "FOCUS" -> value >= task.targetValue
            "SEDENTARY_LIMIT", "NIGHT_ACTIVE_LIMIT" -> value <= task.targetValue && value > 0 // 为避免初始就完成，要求有记录
            else -> false
        }
        
        planetTaskDao.updateTaskProgress(taskType, date, value, completed)
    }

    suspend fun claimReward(taskId: Long): PlanetTaskEntity? {
        val task = planetTaskDao.getTaskById(taskId)
        if (task != null && task.completed && !task.claimed) {
            val updatedTask = task.copy(claimed = true)
            planetTaskDao.updateTask(updatedTask)
            return updatedTask
        }
        return null
    }
}
