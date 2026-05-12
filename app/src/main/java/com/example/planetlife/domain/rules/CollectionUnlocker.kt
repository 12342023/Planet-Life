package com.example.planetlife.domain.rules

import com.example.planetlife.data.local.entity.DailyStatsEntity
import com.example.planetlife.data.local.entity.PlanetEntity
import com.example.planetlife.data.local.entity.PlanetEventEntity
import com.example.planetlife.data.repository.CollectionRepository
import com.example.planetlife.data.repository.PlanetEventRepository
import java.text.SimpleDateFormat
import java.util.*

class CollectionUnlocker(
    private val collectionRepository: CollectionRepository,
    private val eventRepository: PlanetEventRepository
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    suspend fun checkUnlocks(planet: PlanetEntity, todayStats: DailyStatsEntity?) {
        collectionRepository.ensureDefaultCreatures()

        // 1. 森林鹿: 累计步行 30 分钟 (本阶段先用今日)
        if ((todayStats?.walkingMinutes ?: 0) >= 30) {
            unlockIfPossible("森林鹿", "普通", "森林鹿在南部森林悄悄现身，它似乎很喜欢你带来的雨水。")
        }

        // 2. 水晶猫: 累计专注 60 分钟 (本阶段先用今日)
        if ((todayStats?.focusMinutes ?: 0) >= 60) {
            unlockIfPossible("水晶猫", "普通", "一只晶莹剔透的小猫出现在水晶塔下，专注的微光吸引了它。")
        }

        // 3. 睡眠水母: 梦境值达到 60
        if (planet.dreamValue >= 60) {
            unlockIfPossible("睡眠水母", "稀有", "梦境海洋变得如此平静，连害羞的睡眠水母也浮出了水面。")
        }

        // 4. 沙漠机械虫: 荒漠值达到 50
        if (planet.desertValue >= 50) {
            unlockIfPossible("沙漠机械虫", "普通", "荒漠的扩张惊动了古老的机械生命，沙漠机械虫开始了巡逻。")
        }

        // 5. 夜行蘑菇人: 暗影值达到 50
        if (planet.shadowValue >= 50) {
            unlockIfPossible("夜行蘑菇人", "稀有", "暗影区域传来了细碎的脚步声，夜行蘑菇人正打着灯笼经过。")
        }
    }

    private suspend fun unlockIfPossible(name: String, rarity: String, eventDesc: String) {
        val unlocked = collectionRepository.unlockCreature(name)
        if (unlocked) {
            eventRepository.saveEvent(
                PlanetEventEntity(
                    date = dateFormat.format(Date()),
                    eventType = "CREATURE_UNLOCKED",
                    title = "发现新生物：$name",
                    description = eventDesc,
                    relatedValue = "图鉴",
                    rarity = rarity
                )
            )
        }
    }
}
