package com.example.planetlife.data.repository

import com.example.planetlife.data.local.dao.CreatureDao
import com.example.planetlife.data.local.entity.CreatureEntity
import kotlinx.coroutines.flow.Flow

class CollectionRepository(private val creatureDao: CreatureDao) {

    fun getAllCreatures(): Flow<List<CreatureEntity>> = creatureDao.getAllCreatures()

    suspend fun ensureDefaultCreatures() {
        if (creatureDao.getCreatureCount() == 0) {
            val defaultCreatures = listOf(
                CreatureEntity(
                    name = "森林鹿",
                    type = "生物",
                    rarity = "普通",
                    description = "在南部森林雨后出现，角上会挂着细小露珠。",
                    unlockCondition = "累计步行 30 分钟"
                ),
                CreatureEntity(
                    name = "水晶猫",
                    type = "生物",
                    rarity = "普通",
                    description = "栖息在水晶塔旁，会回应专注时产生的微光。",
                    unlockCondition = "累计专注 60 分钟"
                ),
                CreatureEntity(
                    name = "睡眠水母",
                    type = "生物",
                    rarity = "稀有",
                    description = "漂浮在梦境海洋中，只在星球足够安静时靠近。",
                    unlockCondition = "梦境值达到 60"
                ),
                CreatureEntity(
                    name = "沙漠机械虫",
                    type = "生物",
                    rarity = "普通",
                    description = "在荒漠边缘巡游，喜欢收集被风吹散的齿轮。",
                    unlockCondition = "荒漠值达到 50"
                ),
                CreatureEntity(
                    name = "夜行蘑菇人",
                    type = "生物",
                    rarity = "稀有",
                    description = "在暗影区域悄悄行走，帽檐下藏着微弱星光。",
                    unlockCondition = "暗影值达到 50"
                )
            )
            creatureDao.insertCreatures(defaultCreatures)
        }
    }

    suspend fun unlockCreature(name: String): Boolean {
        val creature = creatureDao.getCreatureByName(name)
        if (creature != null && !creature.isUnlocked) {
            creatureDao.updateCreature(
                creature.copy(
                    isUnlocked = true,
                    unlockedAt = System.currentTimeMillis()
                )
            )
            return true
        }
        return false
    }
}
