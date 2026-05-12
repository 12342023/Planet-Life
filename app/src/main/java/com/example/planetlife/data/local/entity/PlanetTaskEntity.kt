package com.example.planetlife.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "planet_tasks")
data class PlanetTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // yyyy-MM-dd
    val title: String,
    val description: String,
    val taskType: String, // WALKING, FOCUS, SEDENTARY_LIMIT, NIGHT_ACTIVE_LIMIT
    val targetValue: Int,
    val currentValue: Int,
    val rewardType: String, // FOREST, CRYSTAL, DESERT_REDUCE, DREAM
    val rewardValue: Int,
    val completed: Boolean = false,
    val claimed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
