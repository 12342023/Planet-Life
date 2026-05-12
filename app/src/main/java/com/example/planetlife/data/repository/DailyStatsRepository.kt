package com.example.planetlife.data.repository

import com.example.planetlife.data.local.dao.DailyStatsDao
import com.example.planetlife.data.local.entity.DailyStatsEntity
import kotlinx.coroutines.flow.Flow

class DailyStatsRepository(
    private val dailyStatsDao: DailyStatsDao
) {
    fun observeStatsByDate(date: String): Flow<DailyStatsEntity?> = dailyStatsDao.observeStatsByDate(date)

    fun observeAllStats(): Flow<List<DailyStatsEntity>> = dailyStatsDao.observeAllStats()

    suspend fun getStatsByDate(date: String): DailyStatsEntity? = dailyStatsDao.getStatsByDate(date)

    suspend fun saveStats(stats: DailyStatsEntity) {
        dailyStatsDao.upsertStats(stats)
    }
}
