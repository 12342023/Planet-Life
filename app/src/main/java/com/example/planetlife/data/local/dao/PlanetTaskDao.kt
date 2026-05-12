package com.example.planetlife.data.local.dao

import androidx.room.*
import com.example.planetlife.data.local.entity.PlanetTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlanetTaskDao {
    @Query("SELECT * FROM planet_tasks WHERE date = :date")
    fun getTasksByDate(date: String): Flow<List<PlanetTaskEntity>>

    @Query("SELECT * FROM planet_tasks WHERE date = :date")
    suspend fun getTasksByDateSync(date: String): List<PlanetTaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<PlanetTaskEntity>)

    @Update
    suspend fun updateTask(task: PlanetTaskEntity)

    @Query("UPDATE planet_tasks SET currentValue = :value, completed = :completed WHERE taskType = :taskType AND date = :date")
    suspend fun updateTaskProgress(taskType: String, date: String, value: Int, completed: Boolean)

    @Query("SELECT * FROM planet_tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): PlanetTaskEntity?

    @Query("DELETE FROM planet_tasks")
    suspend fun deleteAllTasks()
}
