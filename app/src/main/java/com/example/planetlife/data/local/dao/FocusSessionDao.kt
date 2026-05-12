package com.example.planetlife.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.planetlife.data.local.entity.FocusSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {
    @Query("SELECT * FROM focus_sessions ORDER BY createdAt DESC")
    fun observeAllSessions(): Flow<List<FocusSessionEntity>>

    @Insert
    suspend fun insertSession(session: FocusSessionEntity): Long

    @Query("SELECT * FROM focus_sessions WHERE id = :id")
    suspend fun getSessionById(id: Long): FocusSessionEntity?

    @Query("DELETE FROM focus_sessions")
    suspend fun deleteAllSessions()
}
