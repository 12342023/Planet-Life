package com.example.planetlife.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.planetlife.data.local.entity.PlanetEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanetEventDao {
    @Query("SELECT * FROM planet_events ORDER BY createdAt DESC")
    fun observeAllEvents(): Flow<List<PlanetEventEntity>>

    @Query("SELECT * FROM planet_events WHERE date = :date ORDER BY createdAt DESC LIMIT :limit")
    fun observeEventsByDate(date: String, limit: Int): Flow<List<PlanetEventEntity>>

    @Insert
    suspend fun insertEvent(event: PlanetEventEntity)

    @Query("DELETE FROM planet_events")
    suspend fun deleteAllEvents()
}
