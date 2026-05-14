package com.example.planetlife.data.repository

import com.example.planetlife.data.local.dao.PlanetEventDao
import com.example.planetlife.data.local.entity.PlanetEventEntity
import com.example.planetlife.domain.model.EnergyType
import com.example.planetlife.domain.model.MoodWeather
import com.example.planetlife.domain.model.PlanetLogType
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

    suspend fun savePlanetLog(
        date: String,
        logType: PlanetLogType,
        title: String,
        description: String,
        relatedValue: String = "",
        rarity: String = "普通",
        energyType: EnergyType? = null,
        moodWeather: MoodWeather? = null,
        displayDate: String = date,
        metadata: String? = null,
    ) {
        planetEventDao.insertEvent(
            PlanetEventEntity(
                date = date,
                eventType = logType.name,
                title = title,
                description = description,
                relatedValue = relatedValue,
                rarity = rarity,
                logType = logType.name,
                energyType = energyType?.name,
                moodWeather = moodWeather?.name,
                displayDate = displayDate,
                metadata = metadata,
            )
        )
    }
}
