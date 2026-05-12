package com.example.planetlife.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.planetlife.data.local.entity.PlanetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanetDao {
    @Query("SELECT * FROM planets ORDER BY id LIMIT 1")
    fun observePlanet(): Flow<PlanetEntity?>

    @Query("SELECT * FROM planets ORDER BY id LIMIT 1")
    suspend fun getPlanet(): PlanetEntity?

    @Upsert
    suspend fun upsertPlanet(planet: PlanetEntity)

    @Query("DELETE FROM planets")
    suspend fun deleteAllPlanets()
}
