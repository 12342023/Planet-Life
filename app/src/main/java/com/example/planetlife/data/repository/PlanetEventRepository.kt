package com.example.planetlife.data.repository

import com.example.planetlife.data.local.dao.PlanetEventDao
import com.example.planetlife.data.local.entity.PlanetEventEntity
import kotlinx.coroutines.flow.Flow

class PlanetEventRepository(
    private val planetEventDao: PlanetEventDao
) {
    fun observeAllEvents(): Flow<List<PlanetEventEntity>> = planetEventDao.observeAllEvents()

    fun observeTodayEvents(date: String, limit: Int = 3): Flow<List<PlanetEventEntity>> =
        planetEventDao.observeEventsByDate(date, limit)

    suspend fun saveEvent(event: PlanetEventEntity) {
        planetEventDao.insertEvent(event)
    }
}
