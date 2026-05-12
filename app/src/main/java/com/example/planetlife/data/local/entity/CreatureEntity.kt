package com.example.planetlife.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "creatures")
data class CreatureEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String, // 生物, 地貌, 事件
    val rarity: String, // 普通, 稀有, 传奇
    val description: String,
    val unlockCondition: String,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null
)
