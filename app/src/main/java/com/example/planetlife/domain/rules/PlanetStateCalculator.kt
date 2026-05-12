package com.example.planetlife.domain.rules

import com.example.planetlife.data.local.entity.PlanetEntity

object PlanetStateCalculator {
    fun calculate(planet: PlanetEntity): String {
        val positive = planet.forestValue + planet.crystalValue + planet.dreamValue
        val negative = planet.desertValue + planet.shadowValue
        val balanceScore = positive - negative

        return when {
            balanceScore > 80 -> "生态繁荣"
            balanceScore > 30 -> "生态稳定"
            balanceScore >= -30 -> "轻度失衡"
            balanceScore >= -80 -> "生态恶化"
            else -> "星球危机"
        }
    }
}
