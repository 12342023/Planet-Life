package com.example.planetlife.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTime: Long,
    val endTime: Long? = null,
    val durationMinutes: Int,
    val completed: Boolean,
    val rewardCrystal: Int,
    val createdAt: Long = System.currentTimeMillis()
)
