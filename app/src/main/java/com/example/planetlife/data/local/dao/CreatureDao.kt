package com.example.planetlife.data.local.dao

import androidx.room.*
import com.example.planetlife.data.local.entity.CreatureEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CreatureDao {
    @Query("SELECT * FROM creatures")
    fun getAllCreatures(): Flow<List<CreatureEntity>>

    @Query("SELECT COUNT(*) FROM creatures")
    suspend fun getCreatureCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCreatures(creatures: List<CreatureEntity>)

    @Update
    suspend fun updateCreature(creature: CreatureEntity)

    @Query("SELECT * FROM creatures WHERE name = :name")
    suspend fun getCreatureByName(name: String): CreatureEntity?

    @Query("DELETE FROM creatures")
    suspend fun deleteAllCreatures()
}
