package com.example.planetlife.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.planetlife.data.local.entity.DailyEnergyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyEnergyDao {
    @Query("SELECT * FROM daily_energy WHERE date = :date LIMIT 1")
    suspend fun getEnergyByDate(date: String): DailyEnergyEntity?

    @Query("SELECT * FROM daily_energy WHERE date = :date LIMIT 1")
    fun observeEnergyByDate(date: String): Flow<DailyEnergyEntity?>

    @Upsert
    suspend fun upsertEnergy(energy: DailyEnergyEntity)

    @Query("DELETE FROM daily_energy")
    suspend fun deleteAllEnergy()
}
