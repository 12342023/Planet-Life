package com.example.planetlife.data.repository

import com.example.planetlife.data.local.dao.DailyEnergyDao
import com.example.planetlife.data.local.entity.DailyEnergyEntity
import com.example.planetlife.domain.model.EnergyType
import kotlinx.coroutines.flow.Flow

class DailyEnergyRepository(
    private val dailyEnergyDao: DailyEnergyDao,
) {
    fun observeEnergyByDate(date: String): Flow<DailyEnergyEntity?> =
        dailyEnergyDao.observeEnergyByDate(date)

    suspend fun getEnergyByDate(date: String): DailyEnergyEntity? =
        dailyEnergyDao.getEnergyByDate(date)

    suspend fun saveEnergy(energy: DailyEnergyEntity) {
        dailyEnergyDao.upsertEnergy(energy.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun addEnergy(
        date: String,
        energyType: EnergyType,
        amount: Int = 1,
    ): DailyEnergyEntity {
        val current = dailyEnergyDao.getEnergyByDate(date) ?: DailyEnergyEntity(date = date)
        val updated = current.plus(energyType, amount.coerceAtLeast(0))
            .copy(updatedAt = System.currentTimeMillis())
        dailyEnergyDao.upsertEnergy(updated)
        return updated
    }

    private fun DailyEnergyEntity.plus(
        energyType: EnergyType,
        amount: Int,
    ): DailyEnergyEntity {
        return when (energyType) {
            EnergyType.OCEAN -> copy(ocean = ocean + amount)
            EnergyType.SOIL -> copy(soil = soil + amount)
            EnergyType.FOREST -> copy(forest = forest + amount)
            EnergyType.DREAM -> copy(dream = dream + amount)
            EnergyType.LIGHT -> copy(light = light + amount)
            EnergyType.STAR -> copy(star = star + amount)
            EnergyType.CORE -> copy(core = core + amount)
        }
    }
}
