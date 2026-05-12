package com.example.planetlife.domain.rules

import com.example.planetlife.data.local.entity.PlanetEventEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object EventGenerator {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun generateEventsFromBehavior(
        walking: Int,
        sedentary: Int,
        nightActive: Int,
        commute: Int
    ): List<PlanetEventEntity> {
        val events = mutableListOf<PlanetEventEntity>()
        val today = dateFormat.format(Date())

        if (walking >= 30) {
            events.add(
                PlanetEventEntity(
                    date = today,
                    eventType = "FOREST_RAIN",
                    title = "森林降雨",
                    description = "南部森林迎来一场温柔的雨，沉睡的种子开始苏醒。",
                    relatedValue = "森林",
                    rarity = "普通"
                )
            )
        }

        if (sedentary >= 120) {
            events.add(
                PlanetEventEntity(
                    date = today,
                    eventType = "DESERT_FISSURE",
                    title = "沙漠裂缝",
                    description = "荒漠边缘出现新的裂缝，星球发出轻微警报。",
                    relatedValue = "荒漠",
                    rarity = "警告"
                )
            )
        }

        if (nightActive >= 60) {
            events.add(
                PlanetEventEntity(
                    date = today,
                    eventType = "SHADOW_SPORE",
                    title = "暗影孢子",
                    description = "夜色过深，暗影孢子在背光处悄悄生长。",
                    relatedValue = "暗影",
                    rarity = "普通"
                )
            )
        }

        if (commute >= 30) {
            events.add(
                PlanetEventEntity(
                    date = today,
                    eventType = "PORT_LIGHT",
                    title = "港口点亮",
                    description = "远行的风吹亮港口灯塔，城市区域传来新的回声。",
                    relatedValue = "城市",
                    rarity = "普通"
                )
            )
        }

        return events
    }
}
