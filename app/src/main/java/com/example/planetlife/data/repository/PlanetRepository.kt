package com.example.planetlife.data.repository

import com.example.planetlife.data.local.dao.PlanetDao
import com.example.planetlife.data.local.entity.PlanetEntity
import kotlinx.coroutines.flow.Flow

class PlanetRepository(
    private val planetDao: PlanetDao,
) {
    fun observePlanet(): Flow<PlanetEntity?> = planetDao.observePlanet()

    suspend fun getPlanet(): PlanetEntity? = planetDao.getPlanet()

    suspend fun savePlanet(planet: PlanetEntity) {
        planetDao.upsertPlanet(planet)
    }
}
