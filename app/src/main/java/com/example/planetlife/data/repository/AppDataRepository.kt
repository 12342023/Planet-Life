package com.example.planetlife.data.repository

import com.example.planetlife.data.local.dao.*
import com.example.planetlife.data.settings.SettingsDataStore

class AppDataRepository(
    private val planetDao: PlanetDao,
    private val dailyStatsDao: DailyStatsDao,
    private val eventDao: PlanetEventDao,
    private val focusDao: FocusSessionDao,
    private val taskDao: PlanetTaskDao,
    private val creatureDao: CreatureDao,
    private val settingsDataStore: SettingsDataStore
) {
    suspend fun clearAllData() {
        planetDao.deleteAllPlanets()
        dailyStatsDao.deleteAllStats()
        eventDao.deleteAllEvents()
        focusDao.deleteAllSessions()
        taskDao.deleteAllTasks()
        creatureDao.deleteAllCreatures()
        settingsDataStore.clearData()
    }
}
