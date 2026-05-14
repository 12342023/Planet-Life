package com.example.planetlife.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.planetlife.data.local.dao.CreatureDao
import com.example.planetlife.data.local.dao.DailyEnergyDao
import com.example.planetlife.data.local.dao.DailyStatsDao
import com.example.planetlife.data.local.dao.FocusSessionDao
import com.example.planetlife.data.local.dao.PlanetDao
import com.example.planetlife.data.local.dao.PlanetEventDao
import com.example.planetlife.data.local.dao.PlanetTaskDao
import com.example.planetlife.data.local.entity.CreatureEntity
import com.example.planetlife.data.local.entity.DailyEnergyEntity
import com.example.planetlife.data.local.entity.DailyStatsEntity
import com.example.planetlife.data.local.entity.FocusSessionEntity
import com.example.planetlife.data.local.entity.PlanetEntity
import com.example.planetlife.data.local.entity.PlanetEventEntity
import com.example.planetlife.data.local.entity.PlanetTaskEntity

@Database(
    entities = [
        PlanetEntity::class,
        DailyStatsEntity::class,
        DailyEnergyEntity::class,
        PlanetEventEntity::class,
        FocusSessionEntity::class,
        PlanetTaskEntity::class,
        CreatureEntity::class
    ],
    version = 8,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun planetDao(): PlanetDao
    abstract fun dailyStatsDao(): DailyStatsDao
    abstract fun dailyEnergyDao(): DailyEnergyDao
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
                .addMigrations(MIGRATION_6_7, MIGRATION_7_8)
                .fallbackToDestructiveMigrationFrom(true, 1, 2, 3, 4, 5)
                .build().also { instance = it }
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE planet_events ADD COLUMN logType TEXT")
                db.execSQL("ALTER TABLE planet_events ADD COLUMN energyType TEXT")
                db.execSQL("ALTER TABLE planet_events ADD COLUMN moodWeather TEXT")
                db.execSQL("ALTER TABLE planet_events ADD COLUMN displayDate TEXT")
                db.execSQL("ALTER TABLE planet_events ADD COLUMN metadata TEXT")
                db.execSQL("UPDATE planet_events SET displayDate = date WHERE displayDate IS NULL")
            }
        }

        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `daily_energy` (
                        `date` TEXT NOT NULL,
                        `ocean` INTEGER NOT NULL,
                        `soil` INTEGER NOT NULL,
                        `forest` INTEGER NOT NULL,
                        `dream` INTEGER NOT NULL,
                        `light` INTEGER NOT NULL,
                        `star` INTEGER NOT NULL,
                        `core` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL,
                        PRIMARY KEY(`date`)
                    )
                    """.trimIndent()
                )
            }
        }
    }
}
