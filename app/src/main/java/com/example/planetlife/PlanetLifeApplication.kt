package com.example.planetlife

import android.app.Application
import com.example.planetlife.data.local.AppDatabase
import com.example.planetlife.data.repository.AppDataRepository
import com.example.planetlife.data.repository.CollectionRepository
import com.example.planetlife.data.repository.DailyEnergyRepository
import com.example.planetlife.data.repository.DailyStatsRepository
import com.example.planetlife.data.repository.FocusRepository
import com.example.planetlife.data.repository.PlanetEventRepository
import com.example.planetlife.data.repository.PlanetRepository
import com.example.planetlife.data.repository.TaskRepository
import com.example.planetlife.data.settings.SettingsDataStore
import com.example.planetlife.notification.SedentaryReminderNotifier

class PlanetLifeApplication : Application() {
    private val database by lazy { AppDatabase.getInstance(this) }
    val planetRepository by lazy { PlanetRepository(database.planetDao()) }
    val dailyStatsRepository by lazy { DailyStatsRepository(database.dailyStatsDao()) }
    val dailyEnergyRepository by lazy { DailyEnergyRepository(database.dailyEnergyDao()) }
    val planetEventRepository by lazy { PlanetEventRepository(database.planetEventDao()) }
    val focusRepository by lazy { FocusRepository(database.focusSessionDao()) }
    val taskRepository by lazy { TaskRepository(database.planetTaskDao()) }
    val collectionRepository by lazy { CollectionRepository(database.creatureDao()) }
    val settingsDataStore by lazy { SettingsDataStore(this) }
    val sedentaryReminderNotifier by lazy { SedentaryReminderNotifier(this) }

    override fun onCreate() {
        super.onCreate()
        sedentaryReminderNotifier.createChannel()
    }
    
    val appDataRepository by lazy {
        AppDataRepository(
            database.planetDao(),
            database.dailyStatsDao(),
            database.planetEventDao(),
            database.focusSessionDao(),
            database.planetTaskDao(),
            database.creatureDao(),
            settingsDataStore
        )
    }
}
