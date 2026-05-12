package com.example.planetlife.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.planetlife.data.local.entity.DailyStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyStatsDao {
    @Query("SELECT * FROM daily_stats WHERE date = :date LIMIT 1")
    suspend fun getStatsByDate(date: String): DailyStatsEntity?

    @Query("SELECT * FROM daily_stats WHERE date = :date LIMIT 1")
    fun observeStatsByDate(date: String): Flow<DailyStatsEntity?>

    @Query("SELECT * FROM daily_stats")
    fun observeAllStats(): Flow<List<DailyStatsEntity>>

    @Upsert
    suspend fun upsertStats(stats: DailyStatsEntity)

    @Query("DELETE FROM daily_stats")
    suspend fun deleteAllStats()
}
