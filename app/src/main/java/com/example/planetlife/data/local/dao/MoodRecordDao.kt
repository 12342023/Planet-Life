package com.example.planetlife.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.planetlife.data.local.entity.MoodRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodRecordDao {
    @Query("SELECT * FROM mood_records WHERE date = :date LIMIT 1")
    suspend fun getMoodRecordByDate(date: String): MoodRecordEntity?

    @Query("SELECT * FROM mood_records WHERE date = :date LIMIT 1")
    fun observeMoodRecordByDate(date: String): Flow<MoodRecordEntity?>

    @Upsert
    suspend fun upsertMoodRecord(record: MoodRecordEntity)

    @Query("DELETE FROM mood_records")
    suspend fun deleteAllMoodRecords()
}
