package com.example.planetlife.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.planetlife.data.local.dao.DailyStatsDao
import com.example.planetlife.data.local.dao.FocusSessionDao
import com.example.planetlife.data.local.dao.PlanetDao
import com.example.planetlife.data.local.dao.PlanetEventDao
import com.example.planetlife.data.local.dao.PlanetTaskDao
import com.example.planetlife.data.local.dao.CreatureDao
import com.example.planetlife.data.local.entity.DailyStatsEntity
import com.example.planetlife.data.local.entity.FocusSessionEntity
import com.example.planetlife.data.local.entity.PlanetEntity
import com.example.planetlife.data.local.entity.PlanetEventEntity
import com.example.planetlife.data.local.entity.PlanetTaskEntity
import com.example.planetlife.data.local.entity.CreatureEntity

@Database(
    entities = [
        PlanetEntity::class,
        DailyStatsEntity::class,
        PlanetEventEntity::class,
        FocusSessionEntity::class,
        PlanetTaskEntity::class,
        CreatureEntity::class
    ],
    version = 6,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun planetDao(): PlanetDao
    abstract fun dailyStatsDao(): DailyStatsDao
    abstract fun planetEventDao(): PlanetEventDao
    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun planetTaskDao(): PlanetTaskDao
    abstract fun creatureDao(): CreatureDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "planet_life.db",
                )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build().also { instance = it }
            }
        }
    }
}
