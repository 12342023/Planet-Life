package com.example.planetlife.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "planets")
data class PlanetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val level: Int = 1,
    val forestValue: Int = 50,
    val crystalValue: Int = 35,
    val dreamValue: Int = 45,
    val cityValue: Int = 20,
    val desertValue: Int = 10,
    val shadowValue: Int = 10,
    val currentTheme: String = "default",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
)
