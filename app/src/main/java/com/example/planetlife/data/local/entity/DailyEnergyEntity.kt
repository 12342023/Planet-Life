package com.example.planetlife.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_energy")
data class DailyEnergyEntity(
    @PrimaryKey
    val date: String,
    val ocean: Int = 0,
    val soil: Int = 0,
    val forest: Int = 0,
    val dream: Int = 0,
    val light: Int = 0,
    val star: Int = 0,
    val core: Int = 0,
    val updatedAt: Long = System.currentTimeMillis(),
)
