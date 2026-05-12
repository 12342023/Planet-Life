package com.example.planetlife.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_stats")
data class DailyStatsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // yyyy-MM-dd
    val walkingMinutes: Int = 0,
    val focusMinutes: Int = 0,
    val sedentaryMinutes: Int = 0,
    val nightActiveMinutes: Int = 0,
    val commuteMinutes: Int = 0,
    val balanceScore: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)
