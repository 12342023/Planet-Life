package com.example.planetlife.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "planet_events")
data class PlanetEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val eventType: String,
    val title: String,
    val description: String,
    val relatedValue: String,
    val rarity: String,
    val createdAt: Long = System.currentTimeMillis()
)
