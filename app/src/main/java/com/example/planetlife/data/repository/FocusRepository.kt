package com.example.planetlife.data.repository

import com.example.planetlife.data.local.dao.FocusSessionDao
import com.example.planetlife.data.local.entity.FocusSessionEntity
import kotlinx.coroutines.flow.Flow

class FocusRepository(
    private val focusSessionDao: FocusSessionDao
) {
    fun observeAllSessions(): Flow<List<FocusSessionEntity>> = focusSessionDao.observeAllSessions()

    suspend fun saveSession(session: FocusSessionEntity): Long {
        return focusSessionDao.insertSession(session)
    }

    suspend fun getSessionById(id: Long): FocusSessionEntity? {
        return focusSessionDao.getSessionById(id)
    }
}
