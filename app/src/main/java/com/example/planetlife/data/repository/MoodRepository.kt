package com.example.planetlife.data.repository

import com.example.planetlife.data.local.dao.MoodRecordDao
import com.example.planetlife.data.local.entity.MoodRecordEntity
import com.example.planetlife.domain.model.MoodWeather
import kotlinx.coroutines.flow.Flow

class MoodRepository(
    private val moodRecordDao: MoodRecordDao,
) {
    fun observeMoodByDate(date: String): Flow<MoodRecordEntity?> =
        moodRecordDao.observeMoodRecordByDate(date)

    suspend fun getMoodByDate(date: String): MoodRecordEntity? =
        moodRecordDao.getMoodRecordByDate(date)

    suspend fun saveMood(
        date: String,
        moodWeather: MoodWeather,
        message: String = "",
    ): MoodRecordEntity {
        val now = System.currentTimeMillis()
        val existing = moodRecordDao.getMoodRecordByDate(date)
        val record = MoodRecordEntity(
            date = date,
            moodWeather = moodWeather.name,
            message = message,
            createdAt = existing?.createdAt ?: now,
            updatedAt = now,
        )
        moodRecordDao.upsertMoodRecord(record)
        return record
    }
}
